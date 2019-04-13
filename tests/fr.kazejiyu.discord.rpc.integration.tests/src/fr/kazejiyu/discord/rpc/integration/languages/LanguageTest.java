package fr.kazejiyu.discord.rpc.integration.languages;

import java.util.stream.Stream;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import fr.kazejiyu.discord.rpc.integration.languages.Language;

/**
 * Unit test the {@link Language} enumeration.
 */
@DisplayName("A Language")
public class LanguageTest implements WithAssertions {

    @ParameterizedTest 
    @MethodSource("specialFileNames")
    @DisplayName("can be found from the exact name of a file")
    void according_to_the_exact_name_of_the_file(String fileName, Language expectedLanguage) {
        assertThat(Language.fromFileName(fileName)).isEqualTo(expectedLanguage);
    }
    
    @ParameterizedTest 
    @ValueSource(strings= {"", "some file", "anotherone.", "pom", "xml", "md", ".cpp", ".classpath"})
    @DisplayName("cannot be found when the file has no extension")
    void but_not_when_the_file_has_no_extension(String fileName) {
        assertThat(Language.fromFileName(fileName)).isEqualTo(Language.UNKNOWN);
    }
    
    @ParameterizedTest 
    @ValueSource(strings= {"a.blo"})
    @DisplayName("cannot be found when the extension is unknown")
    void but_not_when_the_extension_is_unkown(String fileName) {
        assertThat(Language.fromFileName(fileName)).isEqualTo(Language.UNKNOWN);
    }
    
    @ParameterizedTest
    @MethodSource("fileNamesForAllExtensions")
    @DisplayName("can be found according to the extension of the file")
    void according_to_the_extension_of_the_file(String fileName, Language expectedLanguage) {
        assertThat(Language.fromFileName(fileName)).isEqualTo(expectedLanguage);
    }
    
    static Stream<Arguments> fileNamesForAllExtensions() {
        Stream.Builder<Arguments> fileNames = Stream.builder();
        
        for (Language language : Language.values()) {
            for (String extension : language.getFileExtensions()) {
                fileNames.add(Arguments.of("a." + extension, language));
            }
        }
        return fileNames.build();
    }
    
    static Stream<Arguments> specialFileNames() {
        Stream.Builder<Arguments> fileNames = Stream.builder();
        
        for (Language language : Language.values()) {
            for (String fileName : language.getFileNames()) {
                fileNames.add(Arguments.of(fileName, language));
            }
        }
        return fileNames.build();
    }
}
