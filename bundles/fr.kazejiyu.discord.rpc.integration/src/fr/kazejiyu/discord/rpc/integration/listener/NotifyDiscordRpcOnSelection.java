package fr.kazejiyu.discord.rpc.integration.listener;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.EditorPart;

import fr.kazejiyu.discord.rpc.integration.core.DiscordIntegrationPreferences;
import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcProxy;
import fr.kazejiyu.discord.rpc.integration.extensions.DiscordIntegrationExtensions;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.impl.UnknownInputRichPresence;

/**
 * Notifies {@link DiscordRpcProxy} each time Eclipse's current selection changes.
 * 
 * @author Emmanuel CHEBBI
 */
public class NotifyDiscordRpcOnSelection implements ISelectionListener, IPartListener2 {
	
	/** Communicates informations to Discord */
	private final DiscordRpcProxy rpc = new DiscordRpcProxy();
	
	private IWorkbenchPart lastSelectedPart = null;
	
	private IProject lastSelectedProject = null;
	
	private long timeOnNewProject = 0;
	
	private DiscordIntegrationExtensions extensions = new DiscordIntegrationExtensions();

	/** User's preferences */
	private static final DiscordIntegrationPreferences preferences = DiscordIntegrationPreferences.INSTANCE;
	
	public NotifyDiscordRpcOnSelection() {
		rpc.initialize();
		rpc.setDefault();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(part instanceof EditorPart) || part.equals(lastSelectedPart))
			return;
		
		lastSelectedPart = part;
		EditorPart editor = (EditorPart) part;
		
		Optional<EditorInputRichPresence> maybeUserAdapter = extensions.findAdapterFor(editor.getEditorInput());
		
		EditorInputRichPresence adapter = maybeUserAdapter.orElseGet(defaultAdapterFor(editor.getEditorInput()));
		
		Optional<RichPresence> maybePresence = adapter.createRichPresence(preferences, editor.getEditorInput());
		maybePresence.ifPresent(forwardToDiscord());
	}

	/** @return a built-in adapter handling {@code input} */
	private Supplier<EditorInputRichPresence> defaultAdapterFor(IEditorInput input) {
		return () -> extensions.findDefaultAdapterFor(input).orElse(new UnknownInputRichPresence());
	}
	
	/** @return a consumer that takes a {@code RichPresence} and sends its information to Discord */
	private Consumer<RichPresence> forwardToDiscord() {
		return presence -> rpc.setInformations(presence.getDetails(), presence.getState(), computeElapsedTime(presence)); 
	}
	
	private long computeElapsedTime(RichPresence presence) {
		if (preferences.resetsElapsedTimeOnNewFile())
			return System.currentTimeMillis() / 1000;
		
		if (preferences.resetsElapsedTimeOnNewProject()) {
			if (! Objects.equals(presence.getProject(), lastSelectedProject)) {
				timeOnNewProject = System.currentTimeMillis() / 1000;
				lastSelectedProject = presence.getProject();
			}
			return timeOnNewProject;
		}
		
		return 0;
	}
	
	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		if (Objects.equals(partRef.getPart(false), lastSelectedPart)) {
			rpc.setDefault();
		}
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		// Helps to select automatically a new part when the last one has been closed
		selectionChanged(partRef.getPart(false), null);
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// irrelevant event
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		// irrelevant event
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		// irrelevant event
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		// irrelevant event
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		// irrelevant event
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		// irrelevant event
	}
	
}
