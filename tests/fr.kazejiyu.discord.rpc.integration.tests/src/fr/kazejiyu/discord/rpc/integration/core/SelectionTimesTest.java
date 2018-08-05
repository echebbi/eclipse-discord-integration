package fr.kazejiyu.discord.rpc.integration.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import fr.kazejiyu.discord.rpc.integration.core.SelectionTimes;

@DisplayName("A SelectionTimes")
public class SelectionTimesTest {
	
	private long before;
	private SelectionTimes times;
	private long after;

	@BeforeEach
	void createSelectionTimes() {
		before = System.currentTimeMillis() / 1000;
		times = new SelectionTimes();
		after = System.currentTimeMillis() / 1000;
	}
	
	@Test @DisplayName("is initialized with current timestamp")
	void is_initialized_with_current_timestamp() {
		SoftAssertions softly = new SoftAssertions();
		softly.assertThat(times.onStartup()).isEqualTo(times.onNewProject())
											.isEqualTo(times.onSelection())
											.isBetween(before, after);
		softly.assertAll();
	}
	
	@ParameterizedTest
	@CsvSource({"true", "false"})
	@DisplayName("creates a new instance when configured with a new selection")
	void creates_a_new_instance_when_configured_with_a_new_selection(boolean isNewProject) {
		assertThat(times).isNotSameAs(times.withNewSelection(isNewProject));
	}
	
	@ParameterizedTest
	@CsvSource({"true", "false"})
	@DisplayName("does not reset time on startup on new selection")
	void does_not_reset_time_on_new_project_on_new_selection(boolean isNewProject) {
		assertThat(times.withNewSelection(isNewProject).onStartup()).isEqualTo(times.onStartup());
	}
	
	@Test @DisplayName("does not reset time on new project if selection is in the same project")
	void does_not_reset_time_on_new_project_if_selection_is_in_the_same_project() {
		assertThat(times.withNewSelection(false).onNewProject()).isEqualTo(times.onNewProject());
	}
	
	@Test @DisplayName("resets time on new project if selection is in the same project")
	void resets_time_on_new_project_if_selection_is_in_the_same_project() {
		long beforeNewSelection = System.currentTimeMillis() / 1000;
		SelectionTimes timesAtNewSelection = times.withNewSelection(true);
		long afterNewSelection = System.currentTimeMillis() / 1000;
		
		assertThat(timesAtNewSelection.onNewProject()).isBetween(beforeNewSelection, afterNewSelection);
	}
	
	@ParameterizedTest
	@CsvSource({"true", "false"})
	@DisplayName("resets time on selection if selection changes")
	void resets_time_on__selection_if_selection_changes(boolean isNewProject) {
		long beforeNewSelection = System.currentTimeMillis() / 1000;
		SelectionTimes timesAtNewSelection = times.withNewSelection(isNewProject);
		long afterNewSelection = System.currentTimeMillis() / 1000;
		
		assertThat(timesAtNewSelection.onSelection()).isBetween(beforeNewSelection, afterNewSelection);
	}
	
}
