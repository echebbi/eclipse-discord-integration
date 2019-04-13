package fr.kazejiyu.discord.rpc.integration.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.WithAssertions;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test of the {@link DefaultFileEditorInputRichPresence} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("A DefaultFileEditorInputRichPresence")
public class DefaultFileEditorInputRichPresenceTest implements WithAssertions {

    private DefaultFileEditorInputRichPresence adapter;
    
    @Mock
    private IFileEditorInput input;
    
    @Mock
    private GlobalPreferences preferences;
    
    @Mock
    private IFile file;
    
    @Mock
    private IProject project;
    
    @BeforeEach
    void instanciateSoftwareUnderTest() {
        adapter = new DefaultFileEditorInputRichPresence();
        
        when(input.getFile()).thenReturn(file);
        when(file.getProject()).thenReturn(project);
        when(preferences.getApplicablePreferencesFor(any())).thenReturn(preferences);
    }
    
    @Test @DisplayName("has a priority of 0")
    void has_a_priority_of_0() {
        assertThat(adapter.getPriority()).isZero();
    }
    
    @Test @DisplayName("expects an IEditorInput of type IFileEditorInput")
    void expects_an_IEditorInput_of_type_IFileEditorInput() {
        assertThat(adapter.getExpectedEditorInputClass()).isEqualTo(IFileEditorInput.class);
    }
    
    @Test @DisplayName("throws when asked to create a RichPresence with a wrong IEditorInput")
    void throws_when_asked_to_create_a_RichPresence_with_a_wront_IEditorInput(@Mock IPathEditorInput wrongInput) {
        assertThatIllegalArgumentException().isThrownBy(() -> 
            adapter.createRichPresence(preferences, wrongInput)
        );
    }
    
    @Test @DisplayName("creates a RichPresence with the expected project")
    void creates_a_RichPresence_with_the_expected_project() {
        IProject presenceProject = adapter.createRichPresence(preferences, input)
                                          .flatMap(RichPresence::getProject)
                                          .orElse(null);
        
        assertThat(presenceProject).isEqualTo(project);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"Foo.java", "pom.xml", "Dockerfile", "data.txt", "script"})
    @DisplayName("creates a RichPresence with the expected details")
    void creates_a_RichPresence_with_the_expected_details(String filename) {
        when(file.getName()).thenReturn(filename);
        when(preferences.showsFileName()).thenReturn(true);
        
        Optional<String> details = adapter.createRichPresence(preferences, input)
                                          .flatMap(RichPresence::getDetails);
        
        assertThat(details).contains("Editing " + filename);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"Foo.java", "pom.xml", "Dockerfile", "data.txt", "script"})
    @DisplayName("hides the name of the file if asked by the user")
    void hides_the_name_of_the_file_if_asked_by_the_user(String filename) {
        when(file.getName()).thenReturn(filename);
        
        Optional<String> details = adapter.createRichPresence(preferences, input)
                                          .flatMap(RichPresence::getDetails);
        
        assertThat(details).isEmpty();
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"foo", "my.great.project"})
    @DisplayName("creates a RichPresence with the expected state")
    void creates_a_RichPresence_with_the_expected_state(String projectName) {
        when(project.getName()).thenReturn(projectName);
        when(preferences.showsProjectName()).thenReturn(true);
        
        Optional<String> details = adapter.createRichPresence(preferences, input)
                                          .flatMap(RichPresence::getState);
        
        assertThat(details).contains("Working on " + projectName);
    }
    
    @Test @DisplayName("handles the case where the file is not in a project")
    void handles_the_case_where_the_file_is_not_in_a_project() {
        when(file.getProject()).thenReturn(null);
        when(preferences.showsProjectName()).thenReturn(true);
        
        Optional<String> details = adapter.createRichPresence(preferences, input)
                .flatMap(RichPresence::getState);
        
        assertThat(details).contains("Working on an unknown project");
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"foo", "my.great.project"})
    @DisplayName("hides the project's name if asked by the user")
    void hides_the_project_name_if_asked_by_the_user(String projectName) {
        when(project.getName()).thenReturn(projectName);
        
        Optional<String> details = adapter.createRichPresence(preferences, input)
                                          .flatMap(RichPresence::getState);
        
        assertThat(details).isEmpty();
    }
    
    @ParameterizedTest
    @MethodSource("fileNamesForAllExtensions")
    @DisplayName("hides the language icon if asked by the user")
    void hides_the_language_icon_if_asked_by_the_user(String filename, Language expectedLanguage) {
        when(file.getName()).thenReturn(filename);
        
        Optional<Language> language = adapter.createRichPresence(preferences, input)
                                             .flatMap(RichPresence::getLanguage);
        
        assertThat(language).isEmpty();
    }
    
    @ParameterizedTest
    @MethodSource("fileNamesForAllExtensions")
    @DisplayName("creates a RichPresence with the expected language icon")
    void creates_a_RichPresence_with_the_expected_language_icon(String filename, Language expectedLanguage) {
        when(file.getName()).thenReturn(filename);
        when(preferences.showsLanguageIcon()).thenReturn(true);
        
        Optional<Language> language = adapter.createRichPresence(preferences, input)
                                             .flatMap(RichPresence::getLanguage);
        
        assertThat(language).contains(expectedLanguage);
    }
    
    @Test @DisplayName("creates a RichPresence with no icon when the language is unknown")
    void creates_a_RichPresence_with_no_icon_when_the_language_is_unknown() {
        when(file.getName()).thenReturn("do-not-have-language");
        when(preferences.showsLanguageIcon()).thenReturn(true);
        
        Optional<Language> language = adapter.createRichPresence(preferences, input)
                                             .flatMap(RichPresence::getLanguage);
        
        assertThat(language).isEmpty();
    }
    
    @ParameterizedTest
    @MethodSource("fileNamesForAllExtensions")
    @DisplayName("hides the language name if asked by the user")
    void hides_the_language_name_if_asked_by_the_user(String filename, Language expectedLanguage) {
        when(file.getName()).thenReturn(filename);
        
        Optional<String> largeImageText = adapter.createRichPresence(preferences, input)
                                                 .flatMap(RichPresence::getLargeImageText);
        
        assertThat(largeImageText).isEmpty();
    }
    
    @ParameterizedTest
    @MethodSource("fileNamesForAllExtensions")
    @DisplayName("creates a RichPresence with the expected language name")
    void creates_a_RichPresence_with_the_expected_language_name(String filename, Language expectedLanguage) {
        when(file.getName()).thenReturn(filename);
        when(preferences.showsLanguageIcon()).thenReturn(true);
        
        Optional<String> largeImageText = adapter.createRichPresence(preferences, input)
                                                 .flatMap(RichPresence::getLargeImageText);
        
        assertThat(largeImageText).contains(LanguageLabel.labelOf(expectedLanguage, filename));
    }
    
    @Test @DisplayName("creates a RichPresence with no icon tooltip when the language is unknown")
    void creates_a_RichPresence_with_no_icon_tooltip_when_the_language_is_unknown() {
        when(file.getName()).thenReturn("do_not_have_a_language");
        when(preferences.showsLanguageIcon()).thenReturn(true);
        
        Optional<String> largeImageText = adapter.createRichPresence(preferences, input)
                                                 .flatMap(RichPresence::getLargeImageText);
        
        assertThat(largeImageText).isEmpty();
    }
    
    static Stream<Arguments> fileNamesForAllExtensions() {
        Stream.Builder<Arguments> fileNames = Stream.builder();
        
        for (Language language : Language.values()) {
            for (String extension : language.getFileExtensions()) {
                fileNames.add(Arguments.of("a." + extension, language));
            }
        }
        for (Language language : Language.values()) {
            for (String fileName : language.getFileNames()) {
                fileNames.add(Arguments.of(fileName, language));
            }
        }
        return fileNames.build();
    }
    
}
