package fr.kazejiyu.discord.rpc.integration.listener;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.DEFAULT_DISCORD_APPLICATION_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.assertj.core.api.WithAssertions;
import org.eclipse.core.resources.IProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcLifecycle;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.Moment;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("An Update Discord On Setting Change Listener")
public class UpdateDiscordOnSettingChangeTest implements WithAssertions {
    
    // SUT
    
    private UpdateDiscordOnSettingChange listener;
    
    // SUT's dependencies (i.e. constructor's arguments)
    
    @Mock
    private EditionContext context;
    
    @Mock
    private Function<EditionContext, Optional<RichPresence>> toRichPresence;
    
    @Mock
    private DiscordRpcLifecycle discord;
    
    @Mock
    private GlobalPreferences preferences;
    
    // Stubs
    
    @Mock
    private RichPresence presence;
    
    static final String CUSTOM_DISCORD_APP_ID = "123";
    
    @BeforeEach
    void create_listener() {
        listener = new UpdateDiscordOnSettingChange(context, toRichPresence, discord, preferences);
        
        when(preferences.getApplicablePreferencesFor(any(IProject.class))).thenReturn(preferences);
    }
    
    @Nested @DisplayName("when instantiated")
    class WhenInstantiated {
        
        @Test @DisplayName("throws if context is null")
        void throws_if_context_is_null() {
            assertThatNullPointerException().isThrownBy(() -> 
                new UpdateDiscordOnSettingChange(null, toRichPresence, discord, preferences)
            );
        }
        
        @Test @DisplayName("throws if RichPresence adapter is null")
        void throws_if_RichPresence_adapter_is_null() {
            assertThatNullPointerException().isThrownBy(() -> 
                new UpdateDiscordOnSettingChange(context, null, discord, preferences)
            );
        }
        
        @Test @DisplayName("throws if Discord proxy is null")
        void throws_if_Discord_proxy_is_null() {
            assertThatNullPointerException().isThrownBy(() -> 
                new UpdateDiscordOnSettingChange(context, toRichPresence, null, preferences)
            );
        }
        
        @Test @DisplayName("throws if GlobalPreferences are null")
        void throws_if_GlobalPreferences_are_null() {
            assertThatNullPointerException().isThrownBy(() -> 
                new UpdateDiscordOnSettingChange(context, toRichPresence, discord, null)
            );
        }
        
    }
    
    @Nested @DisplayName("when no RichPresence can be created from the context")
    class WhenNoRichPresenceCanBeCreatedFromTheContext {
        
        @BeforeEach
        void no_RichPresence_can_be_created_from_the_context() {
            when(toRichPresence.apply(context)).thenReturn(Optional.empty());
        }
        
        @Nested @DisplayName("and 'show Rich Presence' preference is true")
        class AndShowRichPresencePreferenceIsTrue {
            
            @BeforeEach
            void the_show_RichPresence_preference_is_true() {
                when(preferences.showsRichPresence()).thenReturn(true);
            }
            
            @Nested @DisplayName("when the 'Rich Presence visibility' preference changes")
            class WhenTheRichPresenceVisibilityPreferenceChanges {
            
                @Nested @DisplayName("and we're already connected to Discord")
                class AndWeAreAlreadyConnectedToDiscord {
                    
                    @BeforeEach
                    void we_are_connected_to_Discord() {
                        when(discord.isConnected()).thenReturn(true);
                        when(discord.isConnectedTo(DEFAULT_DISCORD_APPLICATION_ID)).thenReturn(true);
                    }
                
                    @Test @DisplayName("shutdowns the connection with Discord when the 'Rich Presence visibility' preference becomes false")
                    void shutdowns_the_connection_with_Discord_when_the_Rich_Presence_visibility_preference_becomes_false() {
                        listener.richPresenceVisibilityChanged(false);
                        verify(discord, times(1)).shutdown();
                    }
                    
                    @Test @DisplayName("does not show any Rich Presence on Discord when the 'Rich Presence visibility' preference becomes false")
                    void does_not_show_any_presence_on_Discord_when_the_Rich_Presence_visibility_preference_becomes_false() {
                        listener.richPresenceVisibilityChanged(false);
                        verify(discord, never()).initialize(any(String.class));
                        verify(discord, never()).show(any(RichPresence.class));
                    }
                    
                    @Test @DisplayName("does not initialize a new connection with Discord when the 'Rich Presence visibility' preference becomes true")
                    void does_not_initialize_a_new_connection_with_Discord_when_the_Rich_Presence_visibility_preference_becomes_true() {
                        listener.richPresenceVisibilityChanged(true);
                        verify(discord, never()).initialize(any(String.class));
                    }
                    
                    @Test @DisplayName("does not show anything on Discord when the 'Rich Presence visibility' preference becomes true")
                    void does_not_show_anything_on_Discord_when_the_Rich_Presence_visibility_preference_becomes_true() {
                        listener.richPresenceVisibilityChanged(true);
                        verify(discord, never()).show(any(RichPresence.class));
                        verify(discord, never()).showNothing();
                    }
                    
                }
                
                @Nested @DisplayName("and we're not connected to Discord yet")
                class AndWeAreNotConnectedToDiscordYet {
                    
                    @BeforeEach
                    void we_are_not_connected_to_Discord_yet() {
                        when(discord.isConnected()).thenReturn(false);
                        when(discord.isConnectedTo(any(String.class))).thenReturn(false);
                    }
                    
                    @Nested @DisplayName("and we should use default Discord application")
                    class AndWeShouldUseDefaultApp {
                    
                        @BeforeEach
                        void we_should_use_default_Discord_application() {
                            when(preferences.usesCustomDiscordApplication()).thenReturn(false);
                        }
                    
                        @Test @DisplayName("shutdowns the connection with Discord when the 'Rich Presence visibility' preference becomes false")
                        void shutdowns_the_connection_with_Discord_when_the_Rich_Presence_visibility_preference_becomes_false() {
                            listener.richPresenceVisibilityChanged(false);
                            verify(discord, times(1)).shutdown();
                        }
                        
                        @Test @DisplayName("does not show any Rich Presence on Discord when the 'Rich Presence visibility' preference becomes false")
                        void does_not_show_any_presence_on_Discord_when_the_Rich_Presence_visibility_preference_becomes_false() {
                            listener.richPresenceVisibilityChanged(false);
                            verify(discord, never()).initialize(any(String.class));
                            verify(discord, never()).show(any(RichPresence.class));
                        }
                        
                        @Test @DisplayName("initializes a new connection with Discord when the 'Rich Presence visibility' preference becomes true")
                        void initializes_a_new_connection_with_Discord_when_the_Rich_Presence_visibility_preference_becomes_true() {
                            listener.richPresenceVisibilityChanged(true);
                            verify(discord, times(1)).initialize(DEFAULT_DISCORD_APPLICATION_ID);
                        }
                        
                        @Test @DisplayName("shows nothing on Discord when the 'Rich Presence visibility' preference becomes true")
                        void shows_nothing_on_Discord_when_the_Rich_Presence_visibility_preference_becomes_true() {
                            listener.richPresenceVisibilityChanged(true);
                            verify(discord, never()).show(any(RichPresence.class));
                            verify(discord, atLeastOnce()).showNothing();
                        }
                 
                    }
                    
                    @Nested @DisplayName("and we should use a custom Discord application")
                    class AndWeShouldUseCustomApp {
                    
                        @BeforeEach
                        void we_should_use_a_custom_Discord_application() {
                            when(preferences.usesCustomDiscordApplication()).thenReturn(true);
                            when(preferences.getDiscordApplicationId()).thenReturn(Optional.of(CUSTOM_DISCORD_APP_ID));
                        }
                    
                        @Test @DisplayName("shutdowns the connection with Discord when the 'Rich Presence visibility' preference becomes false")
                        void shutdowns_the_connection_with_Discord_when_the_Rich_Presence_visibility_preference_becomes_false() {
                            listener.richPresenceVisibilityChanged(false);
                            verify(discord, times(1)).shutdown();
                        }
                        
                        @Test @DisplayName("does not show any Rich Presence on Discord when the 'Rich Presence visibility' preference becomes false")
                        void does_not_show_any_presence_on_Discord_when_the_Rich_Presence_visibility_preference_becomes_false() {
                            listener.richPresenceVisibilityChanged(false);
                            verify(discord, never()).initialize(any(String.class));
                            verify(discord, never()).show(any(RichPresence.class));
                        }
                        
                        @Test @DisplayName("initializes a new connection with Discord when the 'Rich Presence visibility' preference becomes true")
                        void initializes_a_new_connection_with_Discord_when_the_Rich_Presence_visibility_preference_becomes_true() {
                            listener.richPresenceVisibilityChanged(true);
                            verify(discord, times(1)).initialize(CUSTOM_DISCORD_APP_ID);
                        }
                        
                        @Test @DisplayName("shows nothing on Discord when the 'Rich Presence visibility' preference becomes true")
                        void shows_nothing_on_Discord_when_the_Rich_Presence_visibility_preference_becomes_true() {
                            listener.richPresenceVisibilityChanged(true);
                            verify(discord, never()).show(any(RichPresence.class));
                            verify(discord, atLeastOnce()).showNothing();
                        }
                 
                    }
                }
                
            }
            
            @Nested @DisplayName("when the 'custom Discord application visibility' preference becomes true")
            class WhenTheCustomAppVisibilityPreferenceBecomesTrue {
                
                @Nested @DisplayName("and a custom Discord application ID is available")
                class AndACustomAppIdIsAvailable {
                    
                    @BeforeEach
                    void a_custom_Discord_application_id_is_available() {
                        when(preferences.getDiscordApplicationId()).thenReturn(Optional.of(CUSTOM_DISCORD_APP_ID));
                    }
                    
                    @Nested @DisplayName("and we should not use it")
                    class ShouldNotUseIt {
                        
                        @BeforeEach
                        void we_should_not_use_the_custom_Discord_application() {
                            when(preferences.usesCustomDiscordApplication()).thenReturn(false);
                        }
                    
                        @Nested @DisplayName("but we are still connected to this Discord application")
                        class ButWeAreConnectedToThisApp {
                            
                            @BeforeEach
                            void we_are_still_connected_to_this_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(true);
                            }
                            
                            @Test @DisplayName("shutdowns the current connection")
                            void shutdowns_the_current_connection_if_the_preference_becomes_true() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, atLeastOnce()).shutdown();
                            }
                            
                            @Test @DisplayName("initializes a connection with the default Discord application")
                            void initializes_a_connection_with_default_Discord_application() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, times(1)).initialize(DEFAULT_DISCORD_APPLICATION_ID);
                            }
                            
                            @Test @DisplayName("shows nothing on Discord")
                            void shows_nothing_on_Discord() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, times(1)).showNothing();
                            }
                            
                            @Test @DisplayName("initializes the connection before showing nothing")
                            void initializes_the_connection_before_showing_nothing() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                
                                InOrder inOrder = Mockito.inOrder(discord);
                                inOrder.verify(discord, times(1)).initialize(any(String.class));
                                inOrder.verify(discord, times(1)).showNothing();
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are connected to the default Discord application")
                        class AndWeAreConnectedToTheDefaultApp {
                            
                            @BeforeEach
                            void we_are_connected_to_the_default_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(false);
                                when(discord.isConnectedTo(DEFAULT_DISCORD_APPLICATION_ID)).thenReturn(true);
                            }
                            
                            @Test @DisplayName("doesn't do anything")
                            void does_not_do_anything() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, never()).shutdown();
                                verify(discord, never()).show(any(RichPresence.class));
                                verify(discord, never()).showNothing();
                                verify(discord, never()).initialize(any(String.class));
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are not connected to any Discord application")
                        class AndWeAreNotConnectedToAnyApp {
                            
                            @BeforeEach
                            void we_are_not_connected_to_any_Discord_application() {
                                when(discord.isConnected()).thenReturn(false);
                                when(discord.isConnectedTo(any(String.class))).thenReturn(false);
                            }
                            
                            @Test @DisplayName("initializes a connection with the default Discord application")
                            void initializes_a_connection_with_default_Discord_application() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, times(1)).initialize(DEFAULT_DISCORD_APPLICATION_ID);
                            }
                            
                            @Test @DisplayName("shows nothing on Discord")
                            void shows_nothing_on_Discord() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, times(1)).showNothing();
                            }
                            
                            @Test @DisplayName("initializes the connection before showing nothing")
                            void initializes_the_connection_before_showing_nothing() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                
                                InOrder inOrder = Mockito.inOrder(discord);
                                inOrder.verify(discord, times(1)).initialize(any(String.class));
                                inOrder.verify(discord, times(1)).showNothing();
                            }
                            
                        }
                        
                    }
                    
                    @Nested @DisplayName("and we should use it")
                    class ShouldUseIt {
                        
                        @BeforeEach
                        void we_should_use_the_custom_Discord_application() {
                            when(preferences.usesCustomDiscordApplication()).thenReturn(true);
                        }
                    
                        @Nested @DisplayName("and we are already connected to this Discord application")
                        class AndWeAreConnectedToThisDiscordApp {
                            
                            @BeforeEach
                            void we_are_already_connected_to_this_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(true);
                            }
                            
                            @Test @DisplayName("doesn't do anything")
                            void does_not_do_anything() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, never()).shutdown();
                                verify(discord, never()).show(any(RichPresence.class));
                                verify(discord, never()).showNothing();
                                verify(discord, never()).initialize(any(String.class));
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are connected to the default Discord application")
                        class AndWeAreConnectedToTheDefaultApp {
                            
                            @BeforeEach
                            void we_are_connected_to_the_default_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(false);
                                when(discord.isConnectedTo(DEFAULT_DISCORD_APPLICATION_ID)).thenReturn(false);
                            }
                            
                            @Test @DisplayName("shutdowns the current connection")
                            void shutdowns_the_current_connection_if_the_preference_becomes_true() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, atLeastOnce()).shutdown();
                            }
                            
                            @Test @DisplayName("initializes a connection with the custom Discord application")
                            void initializes_a_connection_with_default_Discord_application() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, times(1)).initialize(CUSTOM_DISCORD_APP_ID);
                            }
                            
                            @Test @DisplayName("shows nothing on Discord")
                            void shows_nothing_on_Discord() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, times(1)).showNothing();
                            }
                            
                            @Test @DisplayName("initializes the connection before showing nothing")
                            void initializes_the_connection_before_showing_nothing() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                
                                InOrder inOrder = Mockito.inOrder(discord);
                                inOrder.verify(discord, times(1)).initialize(any(String.class));
                                inOrder.verify(discord, times(1)).showNothing();
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are not connected to any Discord application")
                        class AndWeAreNotConnectedToAnyApp {
                            
                            @BeforeEach
                            void we_are_not_connected_to_any_Discord_application() {
                                when(discord.isConnected()).thenReturn(false);
                                when(discord.isConnectedTo(any(String.class))).thenReturn(false);
                            }
                            
                            @Test @DisplayName("initializes a connection with the custom Discord application")
                            void initializes_a_connection_with_the_custom_Discord_application() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, times(1)).initialize(CUSTOM_DISCORD_APP_ID);
                            }
                            
                            @Test @DisplayName("shows nothing on Discord")
                            void shows_nothing_on_Discord() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                verify(discord, times(1)).showNothing();
                            }
                            
                            @Test @DisplayName("initializes the connection before showing nothing")
                            void initializes_the_connection_before_showing_nothing() {
                                listener.customDiscordApplicationVisibilityChanged(true);
                                
                                InOrder inOrder = Mockito.inOrder(discord);
                                inOrder.verify(discord, times(1)).initialize(any(String.class));
                                inOrder.verify(discord, times(1)).showNothing();
                            }
                            
                        }
                        
                    }
                }
                
            }
        
            @Nested @DisplayName("when the 'custom Discord application visibility' preference becomes false")
            class WhenTheCustomAppVisibilityPreferenceBecomesFalse {
                
                @Nested @DisplayName("and a custom Discord application ID is available")
                class AndACustomAppIdIsAvailable {
                    
                    @BeforeEach
                    void a_custom_Discord_application_id_is_available() {
                        when(preferences.getDiscordApplicationId()).thenReturn(Optional.of(CUSTOM_DISCORD_APP_ID));
                    }
                    
                    @Nested @DisplayName("and we should not use it")
                    class ShouldNotUseIt {
                        
                        @BeforeEach
                        void we_should_not_use_the_custom_Discord_application() {
                            when(preferences.usesCustomDiscordApplication()).thenReturn(false);
                        }
                    
                        @Nested @DisplayName("but we are still connected to this Discord application")
                        class ButWeAreConnectedToThisApp {
                            
                            @BeforeEach
                            void we_are_still_connected_to_this_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(true);
                            }
                            
                            @Test @DisplayName("shutdowns the current connection")
                            void shutdowns_the_current_connection_if_the_preference_becomes_true() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, atLeastOnce()).shutdown();
                            }
                            
                            @Test @DisplayName("initializes a connection with the default Discord application")
                            void initializes_a_connection_with_default_Discord_application() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).initialize(DEFAULT_DISCORD_APPLICATION_ID);
                            }
                            
                            @Test @DisplayName("shows nothing on Discord")
                            void shows_nothing_on_Discord() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).showNothing();
                            }
                            
                            @Test @DisplayName("initializes the connection before showing nothing")
                            void initializes_the_connection_before_showing_nothing() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                
                                InOrder inOrder = Mockito.inOrder(discord);
                                inOrder.verify(discord, times(1)).initialize(any(String.class));
                                inOrder.verify(discord, times(1)).showNothing();
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are connected to the default Discord application")
                        class AndWeAreConnectedToTheDefaultApp {
                            
                            @BeforeEach
                            void we_are_connected_to_the_default_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(false);
                                when(discord.isConnectedTo(DEFAULT_DISCORD_APPLICATION_ID)).thenReturn(true);
                            }
                            
                            @Test @DisplayName("doesn't do anything")
                            void does_not_do_anything() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, never()).shutdown();
                                verify(discord, never()).show(any(RichPresence.class));
                                verify(discord, never()).showNothing();
                                verify(discord, never()).initialize(any(String.class));
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are not connected to any Discord application")
                        class AndWeAreNotConnectedToAnyApp {
                            
                            @BeforeEach
                            void we_are_not_connected_to_any_Discord_application() {
                                when(discord.isConnected()).thenReturn(false);
                                when(discord.isConnectedTo(any(String.class))).thenReturn(false);
                            }
                            
                            @Test @DisplayName("initializes a connection with the default Discord application")
                            void initializes_a_connection_with_default_Discord_application() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).initialize(DEFAULT_DISCORD_APPLICATION_ID);
                            }
                            
                            @Test @DisplayName("shows nothing on Discord")
                            void shows_nothing_on_Discord() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).showNothing();
                            }
                            
                            @Test @DisplayName("initializes the connection before showing nothing")
                            void initializes_the_connection_before_showing_nothing() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                
                                InOrder inOrder = Mockito.inOrder(discord);
                                inOrder.verify(discord, times(1)).initialize(any(String.class));
                                inOrder.verify(discord, times(1)).showNothing();
                            }
                            
                        }
                        
                    }
                    
                    @Nested @DisplayName("and we should use it")
                    class ShouldUseIt {
                        
                        @BeforeEach
                        void we_should_use_the_custom_Discord_application() {
                            when(preferences.usesCustomDiscordApplication()).thenReturn(true);
                        }
                    
                        @Nested @DisplayName("and we are already connected to this Discord application")
                        class AndWeAreConnectedToThisDiscordApp {
                            
                            @BeforeEach
                            void we_are_already_connected_to_this_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(true);
                            }
                            
                            @Test @DisplayName("doesn't do anything")
                            void does_not_do_anything() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, never()).shutdown();
                                verify(discord, never()).show(any(RichPresence.class));
                                verify(discord, never()).showNothing();
                                verify(discord, never()).initialize(any(String.class));
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are connected to the default Discord application")
                        class AndWeAreConnectedToTheDefaultApp {
                            
                            @BeforeEach
                            void we_are_connected_to_the_default_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(false);
                                when(discord.isConnectedTo(DEFAULT_DISCORD_APPLICATION_ID)).thenReturn(false);
                            }
                            
                            @Test @DisplayName("shutdowns the current connection")
                            void shutdowns_the_current_connection_if_the_preference_becomes_true() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, atLeastOnce()).shutdown();
                            }
                            
                            @Test @DisplayName("initializes a connection with the custom Discord application")
                            void initializes_a_connection_with_default_Discord_application() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).initialize(CUSTOM_DISCORD_APP_ID);
                            }
                            
                            @Test @DisplayName("shows nothing on Discord")
                            void shows_nothing_on_Discord() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).showNothing();
                            }
                            
                            @Test @DisplayName("initializes the connection before showing nothing")
                            void initializes_the_connection_before_showing_nothing() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                
                                InOrder inOrder = Mockito.inOrder(discord);
                                inOrder.verify(discord, times(1)).initialize(any(String.class));
                                inOrder.verify(discord, times(1)).showNothing();
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are not connected to any Discord application")
                        class AndWeAreNotConnectedToAnyApp {
                            
                            @BeforeEach
                            void we_are_not_connected_to_any_Discord_application() {
                                when(discord.isConnected()).thenReturn(false);
                                when(discord.isConnectedTo(any(String.class))).thenReturn(false);
                            }
                            
                            @Test @DisplayName("initializes a connection with the custom Discord application")
                            void initializes_a_connection_with_the_custom_Discord_application() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).initialize(CUSTOM_DISCORD_APP_ID);
                            }
                            
                            @Test @DisplayName("shows nothing on Discord")
                            void shows_nothing_on_Discord() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).showNothing();
                            }
                            
                            @Test @DisplayName("initializes the connection before showing nothing")
                            void initializes_the_connection_before_showing_nothing() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                
                                InOrder inOrder = Mockito.inOrder(discord);
                                inOrder.verify(discord, times(1)).initialize(any(String.class));
                                inOrder.verify(discord, times(1)).showNothing();
                            }
                            
                        }
                        
                    }
                }
                
                @Nested @DisplayName("and no custom Discord application ID is available")
                class AndNoCustomAppIdIsAvailable {
                    
                    @BeforeEach
                    void no_custom_Discord_application_id_is_available() {
                        when(preferences.getDiscordApplicationId()).thenReturn(Optional.empty());
                    }
                    
                    @Nested @DisplayName("and we should not use it")
                    class ShouldNotUseIt {
                        
                        @BeforeEach
                        void we_should_not_use_the_custom_Discord_application() {
                            when(preferences.usesCustomDiscordApplication()).thenReturn(false);
                        }
                    
                        @Nested @DisplayName("but we are still connected to a custom Discord application")
                        class ButWeAreConnectedToACustomApp {
                            
                            @BeforeEach
                            void we_are_still_connected_to_a_custom_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(any(String.class))).thenReturn(true);
                                when(discord.isConnectedTo(DEFAULT_DISCORD_APPLICATION_ID)).thenReturn(false);
                            }
                            
                            @Test @DisplayName("shutdowns the current connection")
                            void shutdowns_the_current_connection_if_the_preference_becomes_true() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, atLeastOnce()).shutdown();
                            }
                            
                            @Test @DisplayName("initializes a connection with the default Discord application")
                            void initializes_a_connection_with_default_Discord_application() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).initialize(DEFAULT_DISCORD_APPLICATION_ID);
                            }
                            
                            @Test @DisplayName("shows nothing on Discord")
                            void shows_nothing_on_Discord() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).showNothing();
                            }
                            
                            @Test @DisplayName("initializes the connection before showing nothing")
                            void initializes_the_connection_before_showing_nothing() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                
                                InOrder inOrder = Mockito.inOrder(discord);
                                inOrder.verify(discord, times(1)).initialize(any(String.class));
                                inOrder.verify(discord, times(1)).showNothing();
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are connected to the default Discord application")
                        class AndWeAreConnectedToTheDefaultApp {
                            
                            @BeforeEach
                            void we_are_connected_to_the_default_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(false);
                                when(discord.isConnectedTo(DEFAULT_DISCORD_APPLICATION_ID)).thenReturn(true);
                            }
                            
                            @Test @DisplayName("doesn't do anything")
                            void does_not_do_anything() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, never()).shutdown();
                                verify(discord, never()).show(any(RichPresence.class));
                                verify(discord, never()).showNothing();
                                verify(discord, never()).initialize(any(String.class));
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are not connected to any Discord application")
                        class AndWeAreNotConnectedToAnyApp {
                            
                            @BeforeEach
                            void we_are_not_connected_to_any_Discord_application() {
                                when(discord.isConnected()).thenReturn(false);
                                when(discord.isConnectedTo(any(String.class))).thenReturn(false);
                            }
                            
                            @Test @DisplayName("initializes a connection with the default Discord application")
                            void initializes_a_connection_with_default_Discord_application() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).initialize(DEFAULT_DISCORD_APPLICATION_ID);
                            }
                            
                            @Test @DisplayName("shows nothing on Discord")
                            void shows_nothing_on_Discord() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, times(1)).showNothing();
                            }
                            
                            @Test @DisplayName("initializes the connection before showing nothing")
                            void initializes_the_connection_before_showing_nothing() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                
                                InOrder inOrder = Mockito.inOrder(discord);
                                inOrder.verify(discord, times(1)).initialize(any(String.class));
                                inOrder.verify(discord, times(1)).showNothing();
                            }
                            
                        }
                        
                    }
                    
                    @Nested @DisplayName("and we should use it")
                    class ShouldUseIt {
                        
                        @BeforeEach
                        void we_should_use_the_custom_Discord_application() {
                            when(preferences.usesCustomDiscordApplication()).thenReturn(true);
                        }
                    
                        @Nested @DisplayName("and we are already connected to this Discord application")
                        class AndWeAreConnectedToThisDiscordApp {
                            
                            @BeforeEach
                            void we_are_already_connected_to_this_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(true);
                            }
                            
                            @Test @DisplayName("doesn't do anything")
                            void does_not_do_anything() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, never()).shutdown();
                                verify(discord, never()).show(any(RichPresence.class));
                                verify(discord, never()).showNothing();
                                verify(discord, never()).initialize(any(String.class));
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are connected to the default Discord application")
                        class AndWeAreConnectedToTheDefaultApp {
                            
                            @BeforeEach
                            void we_are_connected_to_the_default_Discord_application() {
                                when(discord.isConnected()).thenReturn(true);
                                when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(false);
                                when(discord.isConnectedTo(DEFAULT_DISCORD_APPLICATION_ID)).thenReturn(false);
                            }
                            
                            @Test @DisplayName("doesn't do anything")
                            void does_not_do_anything() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, never()).shutdown();
                                verify(discord, never()).show(any(RichPresence.class));
                                verify(discord, never()).showNothing();
                                verify(discord, never()).initialize(any(String.class));
                            }
                            
                        }
                    
                        @Nested @DisplayName("and we are not connected to any Discord application")
                        class AndWeAreNotConnectedToAnyApp {
                            
                            @BeforeEach
                            void we_are_not_connected_to_any_Discord_application() {
                                when(discord.isConnected()).thenReturn(false);
                                when(discord.isConnectedTo(any(String.class))).thenReturn(false);
                            }
                            
                            @Test @DisplayName("doesn't do anything")
                            void does_not_do_anything() {
                                listener.customDiscordApplicationVisibilityChanged(false);
                                verify(discord, never()).shutdown();
                                verify(discord, never()).show(any(RichPresence.class));
                                verify(discord, never()).showNothing();
                                verify(discord, never()).initialize(any(String.class));
                            }
                            
                        }
                        
                    }
                }
                
            }
            
            @Nested @DisplayName("when a new custom Discord application ID is specified")
            class WhenACustomAppIdIsSet {
                
                @BeforeEach
                void a_custom_Discord_application_ID_is_specified() {
                    when(preferences.getDiscordApplicationId()).thenReturn(Optional.of(CUSTOM_DISCORD_APP_ID));
                }
                
                @Nested @DisplayName("and we should use it") 
                class AndWeShouldUseIt {
                    
                    @BeforeEach
                    void we_should_use_it() {
                        when(preferences.usesCustomDiscordApplication()).thenReturn(true);
                    }
                    
                    @Nested @DisplayName("and we are already connected to this Discord application")
                    class AndWeAreConnectedToThisApp {
                        
                        @BeforeEach
                        void we_are_connected_to_this_app() {
                            when(discord.isConnected()).thenReturn(true);
                            when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(true);
                        }
                        
                        @Test @DisplayName("doesn't do anything")
                        void does_not_do_anything() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            verify(discord, never()).shutdown();
                            verify(discord, never()).show(any(RichPresence.class));
                            verify(discord, never()).showNothing();
                            verify(discord, never()).initialize(any(String.class));
                        }
                        
                    }
                    
                    @Nested @DisplayName("and we are not connected to any Discord application")
                    class AndWeAreNotConnectedToAnyApp {
                        
                        @BeforeEach
                        void we_are_not_connected_to_any_app() {
                            when(discord.isConnected()).thenReturn(false);
                            when(discord.isConnectedTo(any(String.class))).thenReturn(false);
                        }
                        
                        @Test @DisplayName("initializes a new connection with the custom Discord application")
                        void initializes_a_new_connection_with_the_custom_Discord_application() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            verify(discord, times(1)).initialize(CUSTOM_DISCORD_APP_ID);
                        }
                        
                        @Test @DisplayName("shows nothing on Discord")
                        void shows_nothing_on_Discord() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            verify(discord, times(1)).showNothing();
                        }
                        
                        @Test @DisplayName("initializes the connection before showing nothing")
                        void initializes_the_connection_before_showing_nothing() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            
                            InOrder inOrder = Mockito.inOrder(discord);
                            inOrder.verify(discord, times(1)).initialize(any(String.class));
                            inOrder.verify(discord, times(1)).showNothing();
                        }
                        
                    }
                    
                    @Nested @DisplayName("and we are connected to the default Discord application")
                    class AndWeAreConnectedToTheDefaultApp {
                        
                        @BeforeEach
                        void we_are_connected_to_the_default__app() {
                            when(discord.isConnected()).thenReturn(true);
                            when(discord.isConnectedTo(DEFAULT_DISCORD_APPLICATION_ID)).thenReturn(true);
                        }
                        
                        @Test @DisplayName("shutdowns the existing connection")
                        void shutdowns_the_existing_connection() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            verify(discord, atLeastOnce()).shutdown();
                        }
                        
                        @Test @DisplayName("initializes a new connection with the custom Discord application")
                        void initializes_a_new_connection_with_the_custom_Discord_application() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            verify(discord, times(1)).initialize(CUSTOM_DISCORD_APP_ID);
                        }
                        
                        @Test @DisplayName("shows nothing on Discord")
                        void shows_nothing_on_Discord() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            verify(discord, times(1)).showNothing();
                        }
                        
                        @Test @DisplayName("initializes the connection before showing nothing")
                        void initializes_the_connection_before_showing_nothing() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            
                            InOrder inOrder = Mockito.inOrder(discord);
                            inOrder.verify(discord, times(1)).initialize(any(String.class));
                            inOrder.verify(discord, times(1)).showNothing();
                        }
                        
                        @Test @DisplayName("shutdowns the connection before initializing a new one")
                        void shutdowns_the_connection_before_initializing_the_new_one() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            
                            InOrder inOrder = Mockito.inOrder(discord);
                            inOrder.verify(discord, atLeastOnce()).shutdown();
                            inOrder.verify(discord, times(1)).initialize(any(String.class));
                        }
                        
                    }
                    
                }
                
                @Nested @DisplayName("and we should not use it") 
                class AndWeShouldNotUseIt {
                    
                    @BeforeEach
                    void we_should_use_it() {
                        when(preferences.usesCustomDiscordApplication()).thenReturn(false);
                    }
                    
                    @Nested @DisplayName("and we are already connected to this Discord application")
                    class AndWeAreConnectedToThisApp {
                        
                        @BeforeEach
                        void we_are_connected_to_this_app() {
                            when(discord.isConnected()).thenReturn(true);
                            when(discord.isConnectedTo(CUSTOM_DISCORD_APP_ID)).thenReturn(true);
                        }
                        
                        @Test @DisplayName("doesn't do anything")
                        void does_not_do_anything() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            verify(discord, never()).shutdown();
                            verify(discord, never()).show(any(RichPresence.class));
                            verify(discord, never()).showNothing();
                            verify(discord, never()).initialize(any(String.class));
                        }
                        
                    }
                    
                    @Nested @DisplayName("and we are not connected to any Discord application")
                    class AndWeAreNotConnectedToAnyApp {
                        
                        @BeforeEach
                        void we_are_not_connected_to_any_app() {
                            when(discord.isConnected()).thenReturn(false);
                            when(discord.isConnectedTo(any(String.class))).thenReturn(false);
                        }
                        
                        @Test @DisplayName("doesn't do anything")
                        void does_not_do_anything() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            verify(discord, never()).shutdown();
                            verify(discord, never()).show(any(RichPresence.class));
                            verify(discord, never()).showNothing();
                            verify(discord, never()).initialize(any(String.class));
                        }
                        
                    }
                    
                    @Nested @DisplayName("and we are connected to the default Discord application")
                    class AndWeAreConnectedToTheDefaultApp {
                        
                        @BeforeEach
                        void we_are_connected_to_the_default__app() {
                            when(discord.isConnected()).thenReturn(true);
                            when(discord.isConnectedTo(DEFAULT_DISCORD_APPLICATION_ID)).thenReturn(true);
                        }
                        
                        @Test @DisplayName("doesn't do anything")
                        void does_not_do_anything() {
                            listener.discordApplicationIdChanged(CUSTOM_DISCORD_APP_ID);
                            verify(discord, never()).shutdown();
                            verify(discord, never()).show(any(RichPresence.class));
                            verify(discord, never()).showNothing();
                            verify(discord, never()).initialize(any(String.class));
                        }
                        
                    }
                    
                }
                
            }
            
            @ParameterizedTest(name = "when preferences becomes ''{0}''") 
            @ValueSource(strings = {"true", "false"})
            @DisplayName("does not show any RichPresence on 'use project properties'")
            void does_not_show_any_presence_on_use_project_properties(boolean shouldUseProjectProperties) {
                listener.useProjectProperties(shouldUseProjectProperties);
                verify(discord, never()).show(presence);
                verify(discord, times(1)).showNothing();
            }
            
            @ParameterizedTest(name = "when preferences becomes ''{0}''") 
            @ValueSource(strings = {"true", "false"})
            @DisplayName("does not show any RichPresence on 'file name visibility change'")
            void does_not_show_any_presence_on_file_name_visibility_change(boolean shouldShowFileName) {
                listener.fileNameVisibilityChanged(shouldShowFileName);
                verify(discord, never()).show(presence);
                verify(discord, times(1)).showNothing();
            }
            
            @ParameterizedTest(name = "when preferences becomes ''{0}''") 
            @ValueSource(strings = {"true", "false"})
            @DisplayName("does not show any RichPresence on 'project name visibility change'")
            void does_not_show_any_presence_on_project_name_visibility_change(boolean shouldShowProjectName) {
                listener.projectNameVisibilityChanged(shouldShowProjectName);
                verify(discord, never()).show(presence);
                verify(discord, times(1)).showNothing();
            }
            
            @ParameterizedTest(name = "when preferences becomes ''{0}''") 
            @ValueSource(strings = {"true", "false"})
            @DisplayName("does not show any RichPresence on 'language icon visibility change'")
            void does_not_show_any_presence_on_language_icon_visibility_change(boolean shouldShowLanguageIcon) {
                listener.languageIconVisibilityChanged(shouldShowLanguageIcon);
                verify(discord, never()).show(presence);
                verify(discord, times(1)).showNothing();
            }
            
            @ParameterizedTest(name = "when preferences becomes ''{0}''") 
            @ValueSource(strings = {"true", "false"})
            @DisplayName("does not show any RichPresence on 'elapsed time visibility change'")
            void does_not_show_any_presence_on_elapsed_time_visibility_change(boolean shouldShowElapsedTime) {
                listener.elapsedTimeVisibilityChanged(shouldShowElapsedTime);
                verify(discord, never()).show(presence);
                verify(discord, times(1)).showNothing();
            }
            
            @ParameterizedTest(name = "when preferences becomes ''{0}''")
            @ArgumentsSource(CombinationOfMomentsArgumentProvider.class)
            @DisplayName("does not show any RichPresence on 'elapsed time's reset moment change'")
            void does_not_show_any_presence_on_elapsed_time_reset_moment_change(Moment oldMoment, Moment newMoment) {
                listener.elapsedTimeResetMomentChanged(oldMoment, newMoment);
                verify(discord, never()).show(presence);
                verify(discord, times(1)).showNothing();
            }
            
            @ParameterizedTest(name = "when project name becomes ''{0}''")
            @CsvSource({"project,name with space", "  project, a", "project,123,", "same,same"})
            @DisplayName("does not show any Rich Presence on 'project name change'")
            void does_not_show_any_presence_on_project_name_change(String oldProjectName, String newProjectName) {
                listener.projectNameChanged(oldProjectName, newProjectName);
                verify(discord, never()).show(presence);
                verify(discord, times(1)).showNothing();
            }
        }
        
    }
    
    static class CombinationOfMomentsArgumentProvider implements ArgumentsProvider {
        
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return Stream.of(
                Arguments.of(Moment.ON_NEW_FILE, Moment.ON_NEW_PROJECT),
                Arguments.of(Moment.ON_NEW_FILE, Moment.ON_STARTUP),
                Arguments.of(Moment.ON_NEW_FILE, Moment.ON_NEW_PROJECT),
                Arguments.of(Moment.ON_NEW_PROJECT, Moment.ON_NEW_FILE),
                Arguments.of(Moment.ON_NEW_PROJECT, Moment.ON_STARTUP),
                Arguments.of(Moment.ON_STARTUP, Moment.ON_NEW_PROJECT),
                Arguments.of(Moment.ON_STARTUP, Moment.ON_NEW_FILE)
            );
        }
    
    }

}
