package fr.kazejiyu.discord.rpc.integration.extensions.internal;

import java.util.Optional;

import org.eclipse.ui.IEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;

public class FakeEditorInputRichPresence implements EditorInputRichPresence {
	
	private final int priority;
	
	private final Class<? extends IEditorInput> inputClass;
	
	public FakeEditorInputRichPresence(int priority, Class<? extends IEditorInput> inputClass) {
		this.priority = priority;
		this.inputClass = inputClass;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public Class<? extends IEditorInput> getExpectedEditorInputClass() {
		return inputClass;
	}

	@Override
	public Optional<RichPresence> createRichPresence(GlobalPreferences preferences, IEditorInput input) {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return "FakeEditorInputRichPresence [priority=" + priority + ", inputClass=" + inputClass + "]";
	}
	
}
