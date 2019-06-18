package fr.kazejiyu.discord.rpc.integration.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.eclipse.core.resources.IProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link SelectionTimes} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("A SelectionTimes")
public class SelectionTimesTest {
    
    private long before;
    private long after;

    private SelectionTimes times;
    
    @Mock
    private IProject selectedProject;

    @BeforeEach
    void createSelectionTimes() {
        before = System.currentTimeMillis() / 1000;
        times = new SelectionTimes();
        after = System.currentTimeMillis() / 1000;
        
        times.updateWithNewSelectionIn(selectedProject);
    }
    
    @Test @DisplayName("is initialized with current timestamp")
    void is_initialized_with_current_timestamp() {
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(times.onStartup()).isEqualTo(times.onNewProject())
                                            .isEqualTo(times.onSelection())
                                            .isBetween(before, after);
        softly.assertAll();
    }
    
    @Test @DisplayName("returns the same instance when configured with a new selection")
    void returns_the_same_instance_when_configured_with_a_new_selection(@Mock IProject newProject) {
        assertThat(times).isSameAs(times.updateWithNewSelectionIn(newProject));
    }
    
    @Test @DisplayName("does not reset time on startup on new selection")
    void does_not_reset_time_on_new_project_on_new_selection(@Mock IProject newProject) {
        long expectedTimeOnStartup = times.onStartup();
        assertThat(times.updateWithNewSelectionIn(newProject).onStartup()).isEqualTo(expectedTimeOnStartup);
    }
    
    @Test @DisplayName("does not reset time on new project if selection is in the same project")
    void does_not_reset_time_on_new_project_if_selection_is_in_the_same_project() {
        long expectedTimeOnNewProject = times.onNewProject();
        assertThat(times.updateWithNewSelectionIn(selectedProject).onNewProject()).isEqualTo(expectedTimeOnNewProject);
    }
    
    @Test @DisplayName("resets time on new project if selection is in the same project")
    void resets_time_on_new_project_if_selection_is_in_the_same_project() {
        long beforeNewSelection = System.currentTimeMillis() / 1000;
        SelectionTimes timesAtNewSelection = times.updateWithNewSelectionIn(selectedProject);
        long afterNewSelection = System.currentTimeMillis() / 1000;
        
        assertThat(timesAtNewSelection.onNewProject()).isBetween(beforeNewSelection, afterNewSelection);
    }
    
    @Test @DisplayName("resets time on selection if selection changes")
    void resets_time_on__selection_if_selection_changes() {
        long beforeNewSelection = System.currentTimeMillis() / 1000;
        SelectionTimes timesAtNewSelection = times.updateWithNewSelectionIn(selectedProject);
        long afterNewSelection = System.currentTimeMillis() / 1000;
        
        assertThat(timesAtNewSelection.onSelection()).isBetween(beforeNewSelection, afterNewSelection);
    }
    
}
