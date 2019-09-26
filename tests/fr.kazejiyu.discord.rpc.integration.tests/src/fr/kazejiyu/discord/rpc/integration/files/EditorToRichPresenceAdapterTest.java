package fr.kazejiyu.discord.rpc.integration.files;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.WithAssertions;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.core.ImmutableRichPresence;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.core.SelectionTimes;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorRichPresenceFromInput;
import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link EditorToRichPresenceAdapter} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("An IEditorPart to RichPresence Adapter")
public class EditorToRichPresenceAdapterTest implements WithAssertions {
    
    private EditorToRichPresenceAdapter adapter;

    // Constructor's arguments
    
    @Mock
    private GlobalPreferences preferences;
    @Mock
    private EditorRichPresenceFromInput extensions;
    
    // Methods' arguments
    
    @Mock
    private EditionContext context;
    
    @BeforeEach
    void createAdapter() {
        // Called by adapter when turning the presence into a preferred presence
        when(context.getElapsedTimes()).thenReturn(new SelectionTimes());
        when(preferences.getApplicablePreferencesFor(any())).thenReturn(preferences);
        
        adapter = new EditorToRichPresenceAdapter(preferences, extensions);
    }
    
    @Nested @DisplayName("when instantiated")
    class WhenInstantiated {
        
        @Test @DisplayName("throws if GlobalPreferences are null")
        void throws_if_preferences_are_null() {
            assertThatNullPointerException().isThrownBy(() ->
                new EditorToRichPresenceAdapter(null, extensions)
            );
        }
        
        @Test @DisplayName("throws if EditorRichPresenceFromInput is null")
        void throws_if_adapters_are_null() {
            assertThatNullPointerException().isThrownBy(() ->
                new EditorToRichPresenceAdapter(preferences, null)
            );
        }
        
        @BeforeEach
        void activate_editor() {
            doReturn(Optional.of(mock(IEditorPart.class))).when(context).lastSelectedEditor();
        }
        
    }
    
    @Nested @DisplayName("when no extension is available for the corresponding IEditorInput")
    class WhenNoExtensionIsAvailableForGivenIEditorInput {
        
        @BeforeEach
        void no_extension_is_available() {
            when(extensions.findAdapterFor(any(IEditorInput.class))).thenReturn(Optional.empty());
        }
        
        @Nested @DisplayName("and no editor has been activated")
        class AndNoEditorHasBeenActivated {
            
            @BeforeEach
            void no_editor_is_active() {
                when(context.lastSelectedEditor()).thenReturn(Optional.empty());
            }
        
            @Test @DisplayName("returns nothing")
            void creates_a_RichPresence() {
                Optional<RichPresence> presence = adapter.apply(context);
                assertThat(presence).isEmpty();
            }
            
        }
        
        @Nested @DisplayName("and an editor has been activated")
        class AndAnEditorHasBeenActivated {
            
            @BeforeEach
            void an_editor_is_active() {
                IEditorPart editor = mock(IEditorPart.class);
                when(context.lastSelectedEditor()).thenReturn(Optional.of(editor));
                when(editor.getEditorInput()).thenReturn(mock(IEditorInput.class));
            }
        
            @Test @DisplayName("creates a RichPresence")
            void creates_a_RichPresence() {
                Optional<RichPresence> presence = adapter.apply(context);
                assertThat(presence).isNotEmpty();
            }
            
            @Test @DisplayName("creates a RichPresence using its default adapter")
            void creates_a_RichPresence_using_its_default_adapter() {
                RichPresence presence = adapter.apply(context).get();
                
                SoftAssertions softly = new SoftAssertions();
                softly.assertThat(presence.getDetails()).as("should have no details").isEmpty();
                softly.assertThat(presence.getState()).as("should have no state").isEmpty();
                softly.assertThat(presence.getLanguage()).as("should have no language").isEmpty();
                softly.assertThat(presence.getLargeImageText()).as("should have no large image text").isEmpty();
                softly.assertThat(presence.getProject()).as("should have no project").isEmpty();
                softly.assertThat(presence.getStartTimestamp()).as("should have no start timestamp").isEmpty();
                softly.assertAll();
            }
            
        }
        
    }
    
    @Nested @DisplayName("when an extension is available for the corresponding IEditorInput")
    class WhenAnExtensionIsAvailableForTheCorrespondingIEditorInput {
        
        @Mock
        private EditorInputRichPresence extension;

        @BeforeEach
        void createExtension() {
            when(extensions.findAdapterFor(any(IEditorInput.class))).thenReturn(Optional.of(extension));
        }
        
        @Nested @DisplayName("if the extension is unable to create a RichPresence")
        class IfTheExtensionIsUnableToCreateARichPresence {
            
            @BeforeEach
            void the_extension_is_unable_to_create_a_RichPresence() {
                when(extension.createRichPresence(any(GlobalPreferences.class), any(IEditorInput.class))).thenReturn(Optional.empty());
            }
            
            @Nested @DisplayName("and no editor has been activated")
            class AndNoEditorHasBeenActivated {
                
                @BeforeEach
                void no_editor_is_active() {
                    when(context.lastSelectedEditor()).thenReturn(Optional.empty());
                }
            
                @Test @DisplayName("returns nothing")
                void returns_nothing() {
                    Optional<RichPresence> presence = adapter.apply(context);
                    assertThat(presence).isEmpty();
                }
                
            }
            
            @Nested @DisplayName("and an editor has been activated")
            class AndAnEditorHasBeenActivated {
                
                @BeforeEach
                void an_editor_is_active() {
                    IEditorPart editor = mock(IEditorPart.class);
                    when(context.lastSelectedEditor()).thenReturn(Optional.of(editor));
                    when(editor.getEditorInput()).thenReturn(mock(IEditorInput.class));
                }
            
                @Test @DisplayName("returns nothing")
                void returns_nothing() {
                    Optional<RichPresence> presence = adapter.apply(context);
                    assertThat(presence).isEmpty();
                }
                
            }
            
        }
        
        @Nested @DisplayName("if the extension is able to create a RichPresence")
        class IfTheExtensionIsAbleToCreateARichPresence {
            
            /** The presence generated from the IEditorInput */
            private RichPresence originalPresence;
            
            /** The presence that should be returned by the adapter */
            private RichPresence expectedPresence;
            
            @BeforeEach
            void the_extension_is_able_to_create_a_RichPresence() {
                originalPresence = new ImmutableRichPresence()
                                            .withDetails("details")
                                            .withCurrentTimestamp()
                                            .withLanguage(Language.BOO)
                                            .withState("state");

                expectedPresence = new ImmutableRichPresence()
                        .withDetails("details")
                        .withLanguage(Language.BOO)
                        .withState("state");
                
                // Setup user preferences
                
                when(preferences.showsRichPresence()).thenReturn(true);
                when(preferences.showsLanguageIcon()).thenReturn(true);
                when(preferences.showsElapsedTime()).thenReturn(false);
                
                doReturn(IEditorInput.class).when(extension).getExpectedEditorInputClass();
                when(extension.createRichPresence(any(GlobalPreferences.class), any(IEditorInput.class))).thenReturn(Optional.of(originalPresence));
            }
            
            @Nested @DisplayName("but no editor has been activated")
            class ButNoEditorHasBeenActivated {
                
                @BeforeEach
                void no_editor_is_active() {
                    when(context.lastSelectedEditor()).thenReturn(Optional.empty());
                }
                
                @Test @DisplayName("returns nothing")
                void returns_nothing() {
                    Optional<RichPresence> presence = adapter.apply(context);
                    assertThat(presence).isEmpty();
                }
            }
            
            @Nested @DisplayName("and an editor has been activated")
            class AndAnEditorHasBeenActivated {
                
                @BeforeEach
                void an_editor_is_active() {
                    IEditorPart editor = mock(IEditorPart.class);
                    when(context.lastSelectedEditor()).thenReturn(Optional.of(editor));
                    when(editor.getEditorInput()).thenReturn(mock(IEditorInput.class));
                }
                
                @Test @DisplayName("returns a RichPresence")
                void returns_a_RichPresence() {
                    Optional<RichPresence> presence = adapter.apply(context);
                    assertThat(presence).isNotEmpty();
                }
                
                @Test @DisplayName("returns a RichPresence tailored to user's preferences")
                void returns_a_RichPresence_tailored_to_user_preferences() {
                    RichPresence presence = adapter.apply(context).get();
                    
                    SoftAssertions softly = new SoftAssertions();
                    softly.assertThat(presence.getDetails()).as("details").isEqualTo(expectedPresence.getDetails());
                    softly.assertThat(presence.getState()).as("state").isEqualTo(expectedPresence.getState());
                    softly.assertThat(presence.getLanguage()).as("language").isEqualTo(expectedPresence.getLanguage());
                    softly.assertThat(presence.getLargeImageText()).as("large image text").isEqualTo(expectedPresence.getLargeImageText());
                    softly.assertThat(presence.getProject()).as("project").isEqualTo(expectedPresence.getProject());
                    softly.assertThat(presence.getStartTimestamp()).as("start timestamp").isEqualTo(expectedPresence.getStartTimestamp());
                    softly.assertAll();
                }
            }
            
        }
    }
}
