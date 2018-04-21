package fr.kazejiyu.discord.rpc.integration.listener;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.jface.viewers.ISelection;
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

/**
 * Notifies {@link DiscordRpcProxy} each time Eclipse's current selection changes.
 * 
 * @author Emmanuel CHEBBI
 */
public class NotifyDiscordRpcOnSelection implements ISelectionListener, IPartListener2 {
	
	/** Communicates informations to Discord */
	private final DiscordRpcProxy rpc = new DiscordRpcProxy();
	
	private IWorkbenchPart lastSelectedPart = null;
	
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
		
		if (maybeUserAdapter.isPresent()) {
			Optional<RichPresence> maybePresence = maybeUserAdapter.get().createRichPresence(preferences, editor.getEditorInput());
			maybePresence.ifPresent(forwardToDiscord());
		}
	}
	
	/** @return a consumer that takes a {@code RichPresence} and sends its information to Discord */
	private Consumer<RichPresence> forwardToDiscord() {
		return presence -> rpc.setInformations(presence.getDetails(), presence.getState()); 
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
