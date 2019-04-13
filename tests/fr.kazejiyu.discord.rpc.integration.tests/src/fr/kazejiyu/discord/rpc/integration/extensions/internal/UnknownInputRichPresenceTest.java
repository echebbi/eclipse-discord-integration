package fr.kazejiyu.discord.rpc.integration.extensions.internal;

import java.util.Optional;

import org.assertj.core.api.WithAssertions;
import org.eclipse.ui.IEditorInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.internal.UnknownInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link UnknownInputRichPresence} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("An UnkownInputRichPresence")
public class UnknownInputRichPresenceTest implements WithAssertions {

    private UnknownInputRichPresence adapter;
    
    @BeforeEach
    void instanciate() {
        adapter = new UnknownInputRichPresence();
    }
    
    @Test @DisplayName("has the lower possible priority")
    void has_the_lower_priority() {
        assertThat(adapter.getPriority()).isEqualTo(Integer.MIN_VALUE);
    }
    
    @Test @DisplayName("expects any instance of IEditorInput")
    void expects_any_instance_of_IEditorInput() {
        assertThat(adapter.getExpectedEditorInputClass()).isEqualTo(IEditorInput.class);
    }
    
    @Test @DisplayName("can create presences")
    void can_create_presences() {
        assertThat(adapter.createRichPresence(null, null)).isNotEmpty();
    }
    
    @Test @DisplayName("creates presences holding no state")
    void creates_presences_holding_no_state() {
        Optional<RichPresence> presence = adapter.createRichPresence(null, null);
        assertThat(presence.get().getState()).isEmpty();
    }
    
    @Test @DisplayName("creates presences holding no details")
    void creates_presences_holding_no_details() {
        Optional<RichPresence> presence = adapter.createRichPresence(null, null);
        assertThat(presence.get().getDetails()).isEmpty();
    }
    
    @Test @DisplayName("creates presences holding no language")
    void creates_presences_holding_no_language() {
        Optional<RichPresence> presence = adapter.createRichPresence(null, null);
        assertThat(presence.get().getLanguage()).isEmpty();
    }
    
    @Test @DisplayName("creates presences holding no text for the large image")
    void creates_presences_holding_no_text_for_the_large_image() {
        Optional<RichPresence> presence = adapter.createRichPresence(null, null);
        assertThat(presence.get().getLargeImageText()).isEmpty();
    }
    
    @Test @DisplayName("creates presences holding no start timestamp")
    void creates_presences_holding_no_start_timestamp() {
        Optional<RichPresence> presence = adapter.createRichPresence(null, null);
        assertThat(presence.get().getStartTimestamp()).isEmpty();
    }
    
    @Test @DisplayName("creates presences holding no project")
    void creates_presences_holding_no_project() {
        Optional<RichPresence> presence = adapter.createRichPresence(null, null);
        assertThat(presence.get().getProject()).isEmpty();
    }
    
}
