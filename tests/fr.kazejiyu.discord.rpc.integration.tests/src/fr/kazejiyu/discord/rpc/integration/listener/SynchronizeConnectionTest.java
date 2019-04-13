package fr.kazejiyu.discord.rpc.integration.listener;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcLifecycle;
import fr.kazejiyu.discord.rpc.integration.settings.Moment;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link SynchronizeConnection} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("A SynchronizeConnection")
public class SynchronizeConnectionTest implements WithAssertions {

    /** Software Under Test. */
    private SynchronizeConnection sut;
    
    @Mock
    private DiscordRpcLifecycle discord;
    
    @Mock
    private Runnable runnable;

    @BeforeEach
    void instanciateClassUnderTest() {
        sut = new SynchronizeConnection(discord, runnable);
    }
    
    @Test @DisplayName("throws when instantiated with a null Discord proxy")
    void throws_when_instanciated_with_a_null_discord_proxy() {
        assertThatNullPointerException().isThrownBy(() -> 
            new SynchronizeConnection(null, runnable)
        );
    }
    
    @Test @DisplayName("throws when instantiated with a null runnable")
    void throws_when_instanciated_with_a_null_runnable() {
        assertThatNullPointerException().isThrownBy(() -> 
            new SynchronizeConnection(discord, null)
        );
    }
    
    @ParameterizedTest(name = "useProjectProperties={0}")
    @ValueSource(strings = {"true", "false"})
    @DisplayName("does nothing on useProjectProperties")
    void does_nothing_on_useProjectProperties(boolean use) {
        sut.useProjectProperties(use);
        verifyZeroInteractions(discord);
        verifyZeroInteractions(runnable);
    }
    
    @ParameterizedTest(name = "isVisible={0}")
    @ValueSource(strings = {"true", "false"})
    @DisplayName("does nothing on fileNameVisibilityChanged")
    void does_nothing_on_fileNameVisibilityChanged(boolean use) {
        sut.fileNameVisibilityChanged(use);
        verifyZeroInteractions(discord);
        verifyZeroInteractions(runnable);
    }
    
    @ParameterizedTest(name = "isVisible={0}")
    @ValueSource(strings = {"true", "false"})
    @DisplayName("does nothing on projectNameVisibilityChanged")
    void does_nothing_on_projectNameVisibilityChanged(boolean use) {
        sut.projectNameVisibilityChanged(use);
        verifyZeroInteractions(discord);
        verifyZeroInteractions(runnable);
    }
    
    @ParameterizedTest(name = "isVisible={0}")
    @ValueSource(strings = {"true", "false"})
    @DisplayName("does nothing on languageIconVisibilityChanged")
    void does_nothing_on_languageIconVisibilityChanged(boolean use) {
        sut.languageIconVisibilityChanged(use);
        verifyZeroInteractions(discord);
        verifyZeroInteractions(runnable);
    }
    
    @ParameterizedTest(name = "isVisible={0}")
    @ValueSource(strings = {"true", "false"})
    @DisplayName("does nothing on elapsedTimeVisibilityChanged")
    void does_nothing_on_elapsedTimeVisibilityChanged(boolean use) {
        sut.elapsedTimeVisibilityChanged(use);
        verifyZeroInteractions(discord);
        verifyZeroInteractions(runnable);
    }
    
    @ParameterizedTest(name = "old moment: {0}, new moment: {1}")
    @MethodSource("momentCombinations")
    @DisplayName("does nothing on elapsedTimeResetMomentChanged")
    void does_nothing_on_elapsedTimeResetMomentChanged(Moment oldMoment, Moment newMoment) {
        sut.elapsedTimeResetMomentChanged(oldMoment, newMoment);
        verifyZeroInteractions(discord);
        verifyZeroInteractions(runnable);
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
    @DisplayName("does nothing on projectNameChange")
    void does_nothing_on_projectNameChange(String oldName, String newName) {
        sut.projectNameChanged(oldName, newName);
        verifyZeroInteractions(discord);
        verifyZeroInteractions(runnable);
    }
    
    @Test @DisplayName("shutdowns Discord connection when RichPresence visibility is set to false")
    void shutdowns_discord_connection_when_RichPresence_visibility_set_to_false() {
        when(discord.isConnected()).thenReturn(true);
        
        sut.richPresenceVisibilityChanged(false);
        
        verify(discord).shutdown();
    }
    
    @Test @DisplayName("initializes Discord connection when RichPresence visibility is set to true")
    void initializes_discord_connection_when_RichPresence_visibility_set_to_true() {
        when(discord.isConnected()).thenReturn(false);
        
        sut.richPresenceVisibilityChanged(true);
        
        verify(discord).initialize();
    }
    
    @Test @DisplayName("runs runnable when RichPresence visibility is set to true")
    void runs_runnable_when_RichPresence_visibility_is_set_to_true() {
        when(discord.isConnected()).thenReturn(false);
        
        sut.richPresenceVisibilityChanged(true);
        
        verify(runnable).run();
    }
    
    @Test @DisplayName("does not re-initialize Discord connection when RichPresence visibility is set to true")
    void does_not_initialize_connection_on_richPresenceVisibilityChanged_when_already_connected() {
        when(discord.isConnected()).thenReturn(true);
        
        sut.richPresenceVisibilityChanged(true);
        
        verify(discord, never()).initialize();
    }
    
    @Test @DisplayName("does not run runnable on richPresenceVisibilityChanged when already connected to discord")
    void does_not_run_runnable_on_richPresenceVisibilityChanged_when_already_connected_to_discord() {
        when(discord.isConnected()).thenReturn(true);
        
        sut.richPresenceVisibilityChanged(true);
        
        verify(runnable, never()).run();
    }
    
}
