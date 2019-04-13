package fr.kazejiyu.discord.rpc.integration.listener;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcLifecycle;
import fr.kazejiyu.discord.rpc.integration.settings.Moment;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link RunOnSettingChange} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("A RunOnSettingChange")
public class RunOnSettingChangeTest implements WithAssertions {

    private RunOnSettingChange listener;
    
    @Mock
    private Runnable runnable;
    
    @Mock
    private DiscordRpcLifecycle discord;
    
    @BeforeEach()
    void createInstance() {
        listener = new RunOnSettingChange(discord, runnable);
    }
    
    @Nested
    @DisplayName("when instanciated")
    class WhenInstanciated {
        
        @Test @DisplayName("throws when runnable is null")
        void throws_when_runnable_is_null() {
            assertThatNullPointerException().isThrownBy(() -> 
                new RunOnSettingChange(discord, null)
            );
        }
            
        @Test @DisplayName("throws when discord proxy is null")
        void throws_when_discord_proxy_is_null() {
            assertThatNullPointerException().isThrownBy(() -> 
                new RunOnSettingChange(null, runnable)
            );
        }
        
    }
    
    @ParameterizedTest(name = "when useProjectProperties set to {0}")
    @CsvSource({"true", "false"})
    @DisplayName("executes the runnable on useProjectProperties")
    void executes_the_runnable_on_useProjectProperties(boolean use) {
        listener.useProjectProperties(use);
        verify(runnable, times(1)).run();
    }
    
    @ParameterizedTest(name = "when isVisible set to {0}")
    @CsvSource({"true", "false"})
    @DisplayName("executes the runnable on fileNameVisibilityChanged")
    void executes_the_runnable_on_fileNameVisibilityChanged(boolean isVisible) {
        listener.fileNameVisibilityChanged(isVisible);
        verify(runnable, times(1)).run();
    }
    
    @ParameterizedTest(name = "when isVisible set to {0}")
    @CsvSource({"true", "false"})
    @DisplayName("executes the runnable on projectNameVisibilityChanged")
    void executes_the_runnable_on_projectNameVisibilityChanged(boolean isVisible) {
        listener.projectNameVisibilityChanged(isVisible);
        verify(runnable, times(1)).run();
    }
    
    @ParameterizedTest(name = "when isVisible set to {0}")
    @CsvSource({"true", "false"})
    @DisplayName("executes the runnable on languageIconVisibilityChanged")
    void executes_the_runnable_on_languageIconVisibilityChanged(boolean isVisible) {
        listener.languageIconVisibilityChanged(isVisible);
        verify(runnable, times(1)).run();
    }
    
    @ParameterizedTest(name = "when isVisible set to {0}")
    @CsvSource({"true", "false"})
    @DisplayName("executes the runnable on elapsedTimeVisibilityChanged")
    void executes_the_runnable_on_elapsedTimeVisibilityChanged(boolean isVisible) {
        listener.elapsedTimeVisibilityChanged(isVisible);
        verify(runnable, times(1)).run();
    }
    
    @ParameterizedTest(name = "old moment: {0}, new moment: {1}")
    @MethodSource("momentCombinations")
    @DisplayName("executes the runnable on elapsedTimeResetMomentChanged")
    void executes_the_runnable_on_elapsedTimeResetMomentChanged(Moment oldMoment, Moment newMoment) {
        listener.elapsedTimeResetMomentChanged(oldMoment, newMoment);
        verify(runnable, times(1)).run();
    }
    
    static Stream<Arguments> momentCombinations() {
        Stream.Builder<Arguments> moments = Stream.builder();
        
        for (Moment oldMoment : Moment.values()) {
            for (Moment newMoment : Moment.values()) {
                moments.add(Arguments.of(oldMoment, newMoment));
            }
        }
        return moments.build();
    }
    
    @ParameterizedTest(name = "old name: {0}, new name: {1}")
    @CsvSource({"old, new", "'', ''", "'old', ''", "'', new", "same, same"})
    @DisplayName("executes the runnable on projectNameChange")
    void executes_the_runnable_on_projectNameChange(String oldName, String newName) {
        listener.projectNameChanged(oldName, newName);
        verify(runnable, times(1)).run();
    }
    
    @Test @DisplayName("shutdowns Discord connection when RichPresence visibility is set to false")
    void shutdowns_discord_connection_when_RichPresence_visibility_set_to_false() {
        when(discord.isConnected()).thenReturn(true);
        
        listener.richPresenceVisibilityChanged(false);
        
        verify(discord).shutdown();
    }
    
    @Test @DisplayName("initializes Discord connection when RichPresence visibility is set to true")
    void initializes_discord_connection_when_RichPresence_visibility_set_to_true() {
        when(discord.isConnected()).thenReturn(false);
        
        listener.richPresenceVisibilityChanged(true);
        
        verify(discord).initialize();
    }
    
    @Test @DisplayName("runs runnable when RichPresence visibility is set to true")
    void runs_runnable_when_RichPresence_visibility_is_set_to_true() {
        when(discord.isConnected()).thenReturn(false);
        
        listener.richPresenceVisibilityChanged(true);
        
        verify(runnable).run();
    }
    
    @Test @DisplayName("does not re-initialize Discord connection when RichPresence visibility is set to true")
    void does_not_initialize_connection_on_richPresenceVisibilityChanged_when_already_connected() {
        when(discord.isConnected()).thenReturn(true);
        
        listener.richPresenceVisibilityChanged(true);
        
        verify(discord, never()).initialize();
    }
    
    @Test @DisplayName("does not run runnable on richPresenceVisibilityChanged when already connected to discord")
    void does_not_run_runnable_on_richPresenceVisibilityChanged_when_already_connected_to_discord() {
        when(discord.isConnected()).thenReturn(true);
        
        listener.richPresenceVisibilityChanged(true);
        
        verify(runnable, never()).run();
    }
}
