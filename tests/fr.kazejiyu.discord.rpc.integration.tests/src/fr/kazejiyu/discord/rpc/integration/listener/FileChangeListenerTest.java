package fr.kazejiyu.discord.rpc.integration.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.function.Function;

import org.assertj.core.api.WithAssertions;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.EditorPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcLifecycle;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorRichPresenceFromInput;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * <p>Unit test the {@link FileChangeListener} class.</p>
 * 
 * <p><b>Important</b>: this class must be tested as a JUnit plug-in, otherwise some tests will fail.</p> 
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("A FileChangeListener")
public class FileChangeListenerTest implements WithAssertions {
    
    private FileChangeListener listener;
    
    @Mock
    private DiscordRpcLifecycle discord;
    
    @Mock(extraInterfaces = {IEditorPart.class})
    private EditorPart activePart;
    
    @Mock
    private EditorRichPresenceFromInput adapters;
    
    @Mock
    private Function<EditionContext, Optional<RichPresence>> toRichPresence;
    
    @BeforeEach
    void instantiateObjectUnderTest() {
        listener = new FileChangeListener(discord, toRichPresence);
        when(activePart.getEditorInput()).thenReturn(mock(IEditorInput.class));
        
        // Discord is considered connected by default
        when(discord.isConnected()).thenReturn(true);
        
        when(toRichPresence.apply(any(EditionContext.class))).thenReturn(Optional.of(mock(RichPresence.class)));
    }
    
    @Nested
    @DisplayName("during instanciation")
    class DuringInstanciation {
        
        @Test @DisplayName("throws if the given Discord proxy is null")
        void throws_if_the_given_Discord_proxy_is_null() {    
            assertThatNullPointerException().isThrownBy(() ->
                new FileChangeListener(null, toRichPresence) 
            );
        }
        
        @Test @DisplayName("throws if the given EditionContext to RichPresence adapter is null")
        void throws_if_the_given_IEditorInput_adapters_is_null() {    
            assertThatNullPointerException().isThrownBy(() ->
                new FileChangeListener(discord, null) 
            );
        }
        
    }
    
    @Test @DisplayName("does nothing when another part is closed")
    void does_nothing_when_another_part_is_closed(@Mock IWorkbenchPartReference closedPartRef, @Mock IWorkbenchPart closedPart) {
        // Given: an active part
        listener.selectionChanged(activePart, null);
        reset(discord);
        
        // When: another part is closed
        when(closedPartRef.getPart(anyBoolean())).thenReturn(closedPart);
        listener.partClosed(closedPartRef);
        
        // Then: nothing happens
        verifyZeroInteractions(discord);
    }
    
    @Test @DisplayName("resets Discord's presence when the active part is closed")
    void does_nothing_when_another_part_is_closed(@Mock IWorkbenchPartReference closedPartRef) {
        // Given: an active part
        listener.selectionChanged(activePart, null);
        reset(discord);
        
        // When: another part is closed
        when(closedPartRef.getPart(anyBoolean())).thenReturn(activePart);
        listener.partClosed(closedPartRef);
        
        // Then: discord shows nothing
        verify(discord).showNothing();
    }
    
    @Test @DisplayName("sends a presence to Discord when a new selection occurs")
    void sends_a_presence_to_discord_when_a_new_selection_occurs() {
        listener.selectionChanged(activePart, null);
        verify(discord).show(any(RichPresence.class));
    }
    
    @Test @DisplayName("does nothing when a new selection occurs on the current active part")
    void does_nothing_when_a_selection_occurs_on_the_current_active_part() {
        listener.selectionChanged(activePart, null);
        listener.selectionChanged(activePart, null);
        
        verify(discord).show(any(RichPresence.class));
    }
    
    @Test @DisplayName("does nothing when a new selection occurs on a non editor part")
    void does_nothing_when_a_selection_occurs_on_a_non_editor_part(@Mock IWorkbenchPart view) {
        listener.selectionChanged(view, null);
        verifyZeroInteractions(discord);
    }
    
    @Test @DisplayName("does nothing when a new selection occurs but Discord is disconnected")
    void does_nothing_when_a_selection_occurs_but_discord_is_disconnected(@Mock IWorkbenchPart view) {
        when(discord.isConnected()).thenReturn(false);
        
        listener.selectionChanged(view, null);
        
        verifyZeroInteractions(discord);
    }
    
    @Test @DisplayName("sends a presence to Discord when a new part is activated")
    void sends_a_presence_to_discord_when_a_new_part_is_activated(@Mock IWorkbenchPartReference activatedPartRef) {
        when(activatedPartRef.getPart(anyBoolean())).thenReturn(activePart);
        
        listener.partActivated(activatedPartRef);
        
        verify(discord).show(any(RichPresence.class));
    }
    
    @Test @DisplayName("does nothing when part brought to top")
    void does_nothing_when_part_brought_to_top(@Mock IWorkbenchPartReference partRef) {
        listener.partBroughtToTop(partRef);
        verifyZeroInteractions(discord);
    }
    
    @Test @DisplayName("does nothing when part activated")
    void does_nothing_when_part_activated(@Mock IWorkbenchPartReference partRef) {
        listener.partActivated(partRef);
        verifyZeroInteractions(discord);
    }
    
    @Test @DisplayName("does nothing when part deactivated")
    void does_nothing_when_part_deactivated(@Mock IWorkbenchPartReference partRef) {
        listener.partDeactivated(partRef);
        verifyZeroInteractions(discord);
    }
    
    @Test @DisplayName("does nothing when part opened")
    void does_nothing_when_part_opened(@Mock IWorkbenchPartReference partRef) {
        listener.partOpened(partRef);
        verifyZeroInteractions(discord);
    }
    
    @Test @DisplayName("does nothing when part hidden")
    void does_nothing_when_part_hidden(@Mock IWorkbenchPartReference partRef) {
        listener.partHidden(partRef);
        verifyZeroInteractions(discord);
    }
    
    @Test @DisplayName("does nothing when part visible")
    void does_nothing_when_part_visible(@Mock IWorkbenchPartReference partRef) {
        listener.partVisible(partRef);
        verifyZeroInteractions(discord);
    }
    
    @Test @DisplayName("does nothing when part's input changed")
    void does_nothing_when_part_input_changed(@Mock IWorkbenchPartReference partRef) {
        listener.partInputChanged(partRef);
        verifyZeroInteractions(discord);
    }

}
