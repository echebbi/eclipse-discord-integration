package fr.kazejiyu.discord.rpc.integration.extensions.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.assertj.core.api.WithAssertions;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import fr.kazejiyu.discord.rpc.integration.Plugin;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputToRichPresenceAdapter;
import fr.kazejiyu.discord.rpc.integration.tests.mock.MockitoExtension;

/**
 * Unit test the {@link EditorRichPresenceFromExtensions} class.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("An EditorRichPresenceFromExtensions")
public class EditorRichPresenceFromExtensionsTest implements WithAssertions {
    
    EditorRichPresenceFromExtensions adapters;
    
    @Mock
    IExtensionRegistry registry;
    
    EditorInputToRichPresenceAdapter expectedAdapter;
    
    @BeforeEach
    void setup() throws CoreException {
        adapters = new EditorRichPresenceFromExtensions(registry);
        
        expectedAdapter = new FakeEditorInputRichPresence(50, GrandchildOfIEditorInput.class);
        
        // Create different elements to test all cases
        // Test cases should be smaller, but...
        
        IConfigurationElement notAnEditorInputRichPresence = elementThatCreates(this);
        IConfigurationElement nullEditorInputRichPresence = elementThatCreates(null);
        IConfigurationElement highPriorityButWrongInput = 
                elementThatCreates(new FakeEditorInputRichPresence(100, FileEditorInput.class));
        IConfigurationElement highPriorityButLessAccurate = 
                elementThatCreates(new FakeEditorInputRichPresence(80, ChildOfIEditorInput.class));
        IConfigurationElement accurateAndHighPriority = 
                elementThatCreates(expectedAdapter);
        IConfigurationElement accurateButLowPriority = 
                elementThatCreates(new FakeEditorInputRichPresence(30, GrandchildOfIEditorInput.class));
        
        IConfigurationElement[] elements = new IConfigurationElement[] {
            notAnEditorInputRichPresence, nullEditorInputRichPresence,
            highPriorityButWrongInput, highPriorityButLessAccurate,
            accurateAndHighPriority, accurateButLowPriority
        };
        
        when(registry.getConfigurationElementsFor(Plugin.EDITOR_INPUT_ADAPTER_EXTENSION_ID))
            .thenReturn(elements);
    }
    
    @Nested @DisplayName("during instanciation")
    class DuringInstanciation {
        
        @Test @DisplayName("throws if registry is null")
        void throws_if_registry_is_null() {
            assertThatNullPointerException().isThrownBy(() ->
                new EditorRichPresenceFromExtensions(null)
            );
        }
        
    }
    
    @Nested @DisplayName("when no adapter can handle the input")
    class WhenNoAdapterCanHandleTheInput {
        
        @Test @DisplayName("returns nothing")
        void returns_nothing(@Mock IEditorInput input) {
            assertThat(adapters.findAdapterFor(input)).isEmpty();
        }
        
    }
    
    @Test @DisplayName("finds the right adapter")
    void finds_the_right_adapter() {
        assertThat(adapters.findAdapterFor(new GrandchildOfIEditorInput()))
            .contains(expectedAdapter);
    }
    
    private static IConfigurationElement elementThatCreates(Object editorInputRichPresence) throws CoreException {
        IConfigurationElement element = mock(IConfigurationElement.class);
        when(element.createExecutableExtension("class")).thenReturn(editorInputRichPresence);
        return element;
    }
    
}
