package fr.kazejiyu.discord.rpc.integration.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.core.resources.IProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.core.ImmutableRichPresence;
import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link ImmutableRichPresence} class. 
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("An ImmutableRichPresence")
public class ImmutableRichPresenceTest {

    @Nested
    @DisplayName("when instanciated")
    class WhenInstanciated {
        
        private ImmutableRichPresence presence;
        
        @BeforeEach
        void createPresence() {
            presence = new ImmutableRichPresence();
        }
        
        @Test @DisplayName("has no state")
        void has_no_state() {
            assertThat(presence.getState()).isEmpty();
        }
        
        @Test @DisplayName("has no details")
        void has_no_details() {
            assertThat(presence.getDetails()).isEmpty();
        }
        
        @Test @DisplayName("has no start timestamp")
        void has_no_start_timestamp() {
            assertThat(presence.getStartTimestamp()).isEmpty();
        }
        
        @Test @DisplayName("has no language")
        void has_no__language() {
            assertThat(presence.getLanguage()).isEmpty();
        }
        
        @Test @DisplayName("has no large image text")
        void has_no_large_image_text() {
            assertThat(presence.getLargeImageText()).isEmpty();
        }
        
        @Test @DisplayName("has no project")
        void has_no_project() {
            assertThat(presence.getProject()).isEmpty();
        }
        
        @Test @DisplayName("has the same hash code as a new presence")
        void has_the_same_hash_code_as_a_new_presence() {
            assertThat(presence.hashCode()).isEqualTo(new ImmutableRichPresence().hashCode());
        }
        
        @Test @DisplayName("is equal to a new presence")
        void is_equal_to_a_new_presence() {
            assertThat(presence).isEqualTo(new ImmutableRichPresence());
        }
        
        @Test @DisplayName("is equal to itself")
        void is_equal_to_itself() {
            assertThat(presence).isEqualTo(presence);
        }
        
        @Test @DisplayName("is not equal to null")
        void is_not_equal_to_null() {
            assertThat(presence).isNotEqualTo(null);
        }
    }
    
    @Nested
    @DisplayName("when already set")
    class WhenAlreadySet {
        
        ImmutableRichPresence presence;
        
        private final String DETAILS = "some details";
        private final String STATE = "some state";
        private final String LARGE_IMAGE_TEXT = "some large image text";
        private final long START_TIMESTAMP = 431;
        private final Language LANGUAGE = Language.BOO;
        @Mock private IProject project;
        
        @BeforeEach
        void createPresence() {
            presence = new ImmutableRichPresence()
                    .withDetails(DETAILS)
                    .withState(STATE)
                    .withStartTimestamp(START_TIMESTAMP)
                    .withLanguage(LANGUAGE)
                    .withLargeImageText(LARGE_IMAGE_TEXT)
                    .withProject(project);
        }
        
        @Test @DisplayName("has the expected details")
        void has_the_expected_details() {
            assertThat(presence.getDetails()).contains(DETAILS);
        }
        
        @Test @DisplayName("has the expected state")
        void has_the_expected_state() {
            assertThat(presence.getState()).contains(STATE);
        }
        
        @Test @DisplayName("has the expected start timestamp")
        void has_the_expected_start_timestamp() {
            assertThat(presence.getStartTimestamp()).contains(START_TIMESTAMP);
        }
        
        @Test @DisplayName("has the expected language")
        void has_the_expected_language() {
            assertThat(presence.getLanguage()).contains(LANGUAGE);
        }
        
        @Test @DisplayName("has the expected large image text")
        void has_the_expected_large_image_text() {
            assertThat(presence.getLargeImageText()).contains(LARGE_IMAGE_TEXT);
        }
        
        @Test @DisplayName("has the expected project")
        void has_the_expected_project() {
            assertThat(presence.getProject()).containsSame(project);
        }
        
        @Test @DisplayName("creates a new instance when configured with new details")
        void creates_a_new_instance_when_configured_with_new_details() {
            assertThat(presence.withDetails("new details")).isNotEqualTo(presence);
        }
        
        @Test @DisplayName("creates a new instance when configured with new state")
        void creates_a_new_instance_when_configured_with_new_state() {
            assertThat(presence.withState("new state")).isNotEqualTo(presence);
        }
        
        @Test @DisplayName("creates a new instance when configured with new start timestamp")
        void creates_a_new_instance_when_configured_with_new_start_timestamp() {
            assertThat(presence.withStartTimestamp(4534)).isNotEqualTo(presence);
        }
        
        @Test @DisplayName("creates a new instance when configured with new language")
        void creates_a_new_instance_when_configured_with_new_language() {
            assertThat(presence.withLanguage(Language.FORTRAN03)).isNotEqualTo(presence);
        }
        
        @Test @DisplayName("creates a new instance when configured with new large image text")
        void creates_a_new_instance_when_configured_with_new_large_image_text() {
            assertThat(presence.withLargeImageText("new large image text")).isNotEqualTo(presence);
        }
        
        @Test @DisplayName("can be configured with new details")
        void can_be_configured_with_new_details() {
            assertThat(presence.withDetails("new details").getDetails()).contains("new details");
        }
        
        @Test @DisplayName("can be configured with new state")
        void can_be_configured_with_new_state() {
            assertThat(presence.withState("new state").getState()).contains("new state");
        }
        
        @Test @DisplayName("can be configured with new start timestamp")
        void can_be_configured_with_new_start_timestamp() {
            assertThat(presence.withStartTimestamp(4534).getStartTimestamp()).contains(4534L);
        }
        
        @Test @DisplayName("can be configured with new start timestamp at current time")
        void can_be_configured_with_new_start_timestamp_at_current_time() {
            long before = System.currentTimeMillis() / 1000;
            assertThat(presence.withCurrentTimestamp().getStartTimestamp().get())
                .isBetween(before, System.currentTimeMillis() / 1000);
        }
        
        @Test @DisplayName("can be configured with new language")
        void can_be_configured_with_new_language() {
            assertThat(presence.withLanguage(Language.FORTRAN03).getLanguage()).contains(Language.FORTRAN03);
        }
        
        @Test @DisplayName("can be configured with new large image text")
        void can_be_configured_with_new_large_image_text() {
            assertThat(presence.withLargeImageText("new large image text").getLargeImageText()).contains("new large image text");
        }
        
        @Test @DisplayName("can be configured without details")
        void can_be_configured_without_details() {
            assertThat(presence.withDetails(null).getDetails()).isEmpty();
        }
        
        @Test @DisplayName("can be configured without state")
        void can_be_configured_without_state() {
            assertThat(presence.withState(null).getState()).isEmpty();
        }
        
        @Test @DisplayName("can be configured without start timestamp")
        void can_be_configured_without_start_timestamp() {
            assertThat(presence.withStartTimestamp(-1).getStartTimestamp()).isEmpty();
        }
        
        @Test @DisplayName("can be configured without language")
        void can_be_configured_without_language() {
            assertThat(presence.withLanguage(null).getLanguage()).isEmpty();
        }
        
        @Test @DisplayName("can be configured without large image text")
        void can_be_configured_without_large_image_text() {
            assertThat(presence.withLargeImageText(null).getLargeImageText()).isEmpty();
        }
        
        @Test @DisplayName("can be configured without project")
        void can_be_configured_without_project() {
            assertThat(presence.withProject(null).getProject()).isEmpty();
        }
    }
    
}
