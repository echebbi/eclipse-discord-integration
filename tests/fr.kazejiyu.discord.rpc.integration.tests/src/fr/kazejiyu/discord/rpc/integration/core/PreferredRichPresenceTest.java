package fr.kazejiyu.discord.rpc.integration.core;

import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.WithAssertions;
import org.eclipse.core.resources.IProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link PreferredRichPresence} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("A PreferredRichPresence")
public class PreferredRichPresenceTest implements WithAssertions {

    @Mock
    private UserPreferences preferences;
    
    @Mock
    private RichPresence presence;
    
    @Mock 
    private SelectionTimes times;
    
    private PreferredRichPresence preferred;
    
    @BeforeEach
    private void instanciate() {
        preferred = new PreferredRichPresence(preferences, presence, times);
    }
    
    @Nested
    @DisplayName("during instanciation")
    class DuringInstanciation {
        
        @Test @DisplayName("throws if preferences are null")
        void throws_if_preferences_are_null() {
            assertThatNullPointerException().isThrownBy(() ->
                new PreferredRichPresence(null, presence, times)
            );
        }
        
        @Test @DisplayName("throws if presence is null")
        void throws_if_presence_is_null() {
            assertThatNullPointerException().isThrownBy(() ->
                new PreferredRichPresence(preferences, null, times)
            );
        }
        
        @Test @DisplayName("throws if times are null")
        void throws_if_times_are_null() {
            assertThatNullPointerException().isThrownBy(() ->
                new PreferredRichPresence(preferences, presence, null)
            );
        }
        
    }
    
    @Nested
    @DisplayName("when asked for the programming language")
    class WhenAskedForTheProgrammingLanguage {

        @ParameterizedTest
        @EnumSource(Language.class)
        @DisplayName("returns the language of the original presence by default")
        void returns_the_language_of_the_original_presence(Language language) {
            when(preferences.showsLanguageIcon()).thenReturn(true);
            when(presence.getLanguage()).thenReturn(Optional.of(language));
            
            assertThat(preferred.getLanguage()).contains(language);
        }
        
        @ParameterizedTest
        @EnumSource(Language.class)
        @DisplayName("returns nothing if the user wants to hide it")
        void returns_nothing_if_the_user_wants_to_hide_it(Language language) {
            when(preferences.showsLanguageIcon()).thenReturn(false);
            when(presence.getLanguage()).thenReturn(Optional.of(language));
            
            assertThat(preferred.getLanguage()).isEmpty();
        }
    }
    
    @Nested
    @DisplayName("when asked for the start timestamp")
    class WhenAskedForTheStartTimestamp {
        
        @Test @DisplayName("returns nothing if the user wants to hide it")
        void returns_nothing_if_the_user_wants_to_hide_it() {
            when(preferences.showsElapsedTime()).thenReturn(false);
            assertThat(preferred.getStartTimestamp()).isEmpty();
        }
        
        @ParameterizedTest(name="when start timestamp = {0}")
        @ValueSource(longs= {-1, 0, 49718})
        @DisplayName("returns last selection's timestamp if setup for")
        void returns_last_selection_timestamp_if_setup_for(long expectedTimestamp) {
            when(preferences.showsElapsedTime()).thenReturn(true);
            when(preferences.resetsElapsedTimeOnNewFile()).thenReturn(true);
            when(times.onSelection()).thenReturn(expectedTimestamp);
            
            assertThat(preferred.getStartTimestamp()).contains(expectedTimestamp);
        }
        
        @ParameterizedTest(name="when start timestamp = {0}")
        @ValueSource(longs= {-13, 0, 3461})
        @DisplayName("returns last project's timestamp if setup for")
        void returns_last_project_timestamp_if_setup_for(long expectedTimestamp) {
            when(preferences.showsElapsedTime()).thenReturn(true);
            when(preferences.resetsElapsedTimeOnNewProject()).thenReturn(true);
            when(times.onNewProject()).thenReturn(expectedTimestamp);
            
            assertThat(preferred.getStartTimestamp()).contains(expectedTimestamp);
        }
        
        @ParameterizedTest(name="when start timestamp = {0}")
        @ValueSource(longs= {-9467, 0, 715})
        @DisplayName("returns startup timestamp if setup for")
        void returns_startup_timestamp_if_setup_for(long expectedTimestamp) {
            when(preferences.showsElapsedTime()).thenReturn(true);
            when(preferences.resetsElapsedTimeOnStartup()).thenReturn(true);
            when(times.onStartup()).thenReturn(expectedTimestamp);
            
            assertThat(preferred.getStartTimestamp()).contains(expectedTimestamp);
        }
        
    }
    
    @ParameterizedTest
    @MethodSource("optionalStrings")
    @DisplayName("returns the details of the original presence")
    void returns_the_details_of_the_original_presence(Optional<String> originalDetails) {
        when(presence.getDetails()).thenReturn(originalDetails);
        assertThat(preferred.getDetails()).isEqualTo(originalDetails);
    }
    
    @ParameterizedTest
    @MethodSource("optionalStrings")
    @DisplayName("returns the state of the original presence")
    void returns_the_state_of_the_original_presence(Optional<String> originalState) {
        when(presence.getState()).thenReturn(originalState);
        assertThat(preferred.getState()).isEqualTo(originalState);
    }
    
    @ParameterizedTest
    @MethodSource("optionalStrings")
    @DisplayName("returns the large image key of the original presence")
    void returns_the_largeImageKey_of_the_original_presence(Optional<String> originalLargeImageKey) {
        when(presence.getLargeImageText()).thenReturn(originalLargeImageKey);
        assertThat(preferred.getLargeImageText()).isEqualTo(originalLargeImageKey);
    }
    
    @Test
    @DisplayName("returns nothing when the original presence has no project")
    void returns_nothing_when_the_original_presence_has_no_project() {
        when(presence.getProject()).thenReturn(Optional.empty());
        assertThat(preferred.getProject()).isEmpty();
    }
    
    @Test
    @DisplayName("returns the project of the original presence")
    void returns_the_project_of_the_original_presence(@Mock IProject project) {
        when(presence.getProject()).thenReturn(Optional.of(project));
        assertThat(preferred.getProject()).contains(project);
    }
    
    static Stream<Arguments> optionalStrings() {
        return Stream.of(
            Arguments.of(Optional.empty()),
            Arguments.of(Optional.of("")),
            Arguments.of(Optional.of("some value"))
        );
    }
}
