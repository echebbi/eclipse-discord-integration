package fr.kazejiyu.discord.rpc.integration.extensions;

import java.util.Optional;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.ui.IEditorInput;

public class DiscordIntegrationExtensions {

	public static final String EDITOR_INPUT_ADAPTER_EXTENSION_ID = "fr.kazejiyu.discord.rpc.integration.editor_input_adapter";

	/**
	 * Returns an adapter able to turn {@code input} into a {@link RichPresence}
	 * instance.<br>
	 * <br>
	 * The adapter is one of the adapters registered to the
	 * {@value #EDITOR_INPUT_ADAPTER_EXTENSION_ID} extension point.
	 * 
	 * @param input
	 *            The input to turn into a {@code RichPresence} instance. Must not
	 *            be {@code null}.
	 * 
	 * @return an adapter able to handle {@code input}, if any.
	 */
	public Optional<EditorInputRichPresence> findAdapterFor(IEditorInput input) {
		IConfigurationElement[] elements = RegistryFactory.getRegistry()
				.getConfigurationElementsFor(EDITOR_INPUT_ADAPTER_EXTENSION_ID);
		
		for (IConfigurationElement element : elements) {
			Object extension = createExecutableExtension(element);
			
			if (extension instanceof EditorInputRichPresence) {
				EditorInputRichPresence adapter = (EditorInputRichPresence) extension;
				
				if (isAValidAdapter(adapter, input))
					return Optional.of(adapter);
			}
		}

		return Optional.empty();
	}

	/** @return a new instance of {@code element}'s class property if possible, {@code null} otherwise */
	private Object createExecutableExtension(IConfigurationElement element) {
		try {
			return element.createExecutableExtension("class");

		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** @return whether {@code adapter} can handle {@code input} */
	private boolean isAValidAdapter(EditorInputRichPresence adapter, IEditorInput input) {
		return adapter.getExpectedEditorInputClass().isInstance(input);
	}

}
