package fr.kazejiyu.discord.rpc.integration.listener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRPC;

/**
 * Notifies {@link DiscordRPC} each time Eclipse's current selection changes.
 * 
 * @author Emmanuel CHEBBI
 */
public class NotifyDiscordRpcOnSelection implements ISelectionListener {
	
	private final DiscordRPC rpc = new DiscordRPC();
	
	public NotifyDiscordRpcOnSelection() {
		rpc.initialize();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		rpc.setDetails(part.getTitle());
	}
	
}
