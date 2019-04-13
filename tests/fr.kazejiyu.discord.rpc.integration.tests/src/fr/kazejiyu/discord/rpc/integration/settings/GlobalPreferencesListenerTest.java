package fr.kazejiyu.discord.rpc.integration.settings;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import org.assertj.core.api.WithAssertions;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link GlobalPreferencesListener} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("A GlobalPreferencesListener")
public class GlobalPreferencesListenerTest implements WithAssertions {
    
    private GlobalPreferencesListener listener;
    
    @Mock
    private SettingChangeListener settingChangeListener;
    
    @Mock
    private PropertyChangeEvent event;
    
    @BeforeEach
    void instanciateListenerUnderTest() {
        listener = new GlobalPreferencesListener(asList(settingChangeListener));
    }
    
    @Test @DisplayName("throws if instanciated with null listeners")
    void throws_if_instanciated_with_null_listeners() {
        assertThatNullPointerException().isThrownBy(() ->
            new GlobalPreferencesListener(null)
        );
    }
    
    @Test @DisplayName("does nothing when project name changes (should not happen)")
    void does_nothing_when_project_name_changes() {
        when(event.getProperty()).thenReturn(Settings.PROJECT_NAME.property());
        listener.propertyChange(event);
        
        verifyZeroInteractions(settingChangeListener);
    }
    
    @ParameterizedTest(name = "when old value={0} and new value={1}") 
    @MethodSource("pairsOfBooleans")
    @DisplayName("notifies its listeners when showFileName property changes")
    void notifies_its_listeners_when_showFileName_property_changes(boolean oldValue, boolean newValue) {
        when(event.getProperty()).thenReturn(Settings.SHOW_FILE_NAME.property());
        when(event.getOldValue()).thenReturn(String.valueOf(oldValue));
        when(event.getNewValue()).thenReturn(String.valueOf(newValue));
        
        listener.propertyChange(event);
        
        verify(settingChangeListener).fileNameVisibilityChanged(newValue);
        verify(settingChangeListener, only()).fileNameVisibilityChanged(newValue);
    }
    
    @ParameterizedTest(name = "when old value={0} and new value={1}") 
    @MethodSource("pairsOfBooleans")
    @DisplayName("notifies its listeners when showProjectName property changes")
    void notifies_its_listeners_when_showProjectName_property_changes(boolean oldValue, boolean newValue) {
        when(event.getProperty()).thenReturn(Settings.SHOW_PROJECT_NAME.property());
        when(event.getOldValue()).thenReturn(String.valueOf(oldValue));
        when(event.getNewValue()).thenReturn(String.valueOf(newValue));
        
        listener.propertyChange(event);
        
        verify(settingChangeListener).projectNameVisibilityChanged(newValue);
        verify(settingChangeListener, only()).projectNameVisibilityChanged(newValue);
    }
    
    @ParameterizedTest(name = "when old value={0} and new value={1}") 
    @MethodSource("pairsOfBooleans")
    @DisplayName("notifies its listeners when showElapsedTime property changes")
    void notifies_its_listeners_when_showElapsedTime_property_changes(boolean oldValue, boolean newValue) {
        when(event.getProperty()).thenReturn(Settings.SHOW_ELAPSED_TIME.property());
        when(event.getOldValue()).thenReturn(String.valueOf(oldValue));
        when(event.getNewValue()).thenReturn(String.valueOf(newValue));
        
        listener.propertyChange(event);
        
        verify(settingChangeListener).elapsedTimeVisibilityChanged(newValue);
        verify(settingChangeListener, only()).elapsedTimeVisibilityChanged(newValue);
    }
    
    @ParameterizedTest(name = "when old value={0} and new value={1}")
    @MethodSource("pairsOfBooleans")
    @DisplayName("notifies its listeners when showLanguageIcon property changes")
    void notifies_its_listeners_when_showLanguageIcon_property_changes(boolean oldValue, boolean newValue) {
        when(event.getProperty()).thenReturn(Settings.SHOW_LANGUAGE_ICON.property());
        when(event.getOldValue()).thenReturn(String.valueOf(oldValue));
        when(event.getNewValue()).thenReturn(String.valueOf(newValue));
        
        listener.propertyChange(event);
        
        verify(settingChangeListener).languageIconVisibilityChanged(newValue);
        verify(settingChangeListener, only()).languageIconVisibilityChanged(newValue);
    }
    
    @ParameterizedTest(name = "when old value={0} and new value = {1}")
    @MethodSource("pairsOfBooleans")
    @DisplayName("notifies its listeners when showRichPresence property changes")
    void notifies_its_listeners_when_showRichPresence_property_changes(boolean oldValue, boolean newValue) {
        when(event.getProperty()).thenReturn(Settings.SHOW_RICH_PRESENCE.property());
        when(event.getOldValue()).thenReturn(String.valueOf(oldValue));
        when(event.getNewValue()).thenReturn(String.valueOf(newValue));
        
        listener.propertyChange(event);
        
        verify(settingChangeListener).richPresenceVisibilityChanged(newValue);
        verify(settingChangeListener, only()).richPresenceVisibilityChanged(newValue);
    }
    
    static Stream<Arguments> pairsOfBooleans() {
        return Stream.of(
            Arguments.of(true, true), Arguments.of(true, false),
            Arguments.of(false, false), Arguments.of(false, true)
        );
    }
    
    @ParameterizedTest(name = "when old value={0} and new value={1}") 
    @MethodSource("momentCombinations")
    @DisplayName("notifies its listeners when resetElapsedTime property changes")
    void notifies_its_listeners_when_resetElapsedTime_property_changes(Moment oldMoment, String oldProperty, Moment newMoment, String newProperty) {
        when(event.getProperty()).thenReturn(Settings.RESET_ELAPSED_TIME.property());
        when(event.getOldValue()).thenReturn(oldProperty);
        when(event.getNewValue()).thenReturn(newProperty);
        
        listener.propertyChange(event);
        
        verify(settingChangeListener).elapsedTimeResetMomentChanged(oldMoment, newMoment);
        verify(settingChangeListener, only()).elapsedTimeResetMomentChanged(oldMoment, newMoment);
    }
    
    static Stream<Arguments> momentCombinations() {
        Stream.Builder<Arguments> moments = Stream.builder();
        
        Map<Moment,Settings> momentToProperty = new EnumMap<>(Moment.class);
        momentToProperty.put(Moment.ON_NEW_FILE, Settings.RESET_ELAPSED_TIME_ON_NEW_FILE);
        momentToProperty.put(Moment.ON_NEW_PROJECT, Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT);
        momentToProperty.put(Moment.ON_STARTUP, Settings.RESET_ELAPSED_TIME_ON_STARTUP);
        
        for (Moment oldMoment : Moment.values()) {
            for (Moment newMoment : Moment.values()) {
                moments.add(Arguments.of(
                        oldMoment, momentToProperty.get(oldMoment).property(),
                        newMoment, momentToProperty.get(newMoment).property()
                ));
            }
        }
        return moments.build();
    }
    
    @Test @DisplayName("notifies its listeners with null when resetElapsedTime property changes incorrectly")
    void notifies_its_listeners_with_null_when_resetElapsedTime_property_changes_incorrectly() {
        when(event.getProperty()).thenReturn(Settings.RESET_ELAPSED_TIME.property());
        when(event.getOldValue()).thenReturn("incorrect");    // should never happen
        when(event.getNewValue()).thenReturn("incorrect");    // should never happen
        
        listener.propertyChange(event);
        
        verify(settingChangeListener).elapsedTimeResetMomentChanged(null, null);
        verify(settingChangeListener, only()).elapsedTimeResetMomentChanged(null, null);
    }

}
