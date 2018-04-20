package fr.kazejiyu.discord.rpc.integration.listener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcProxy;

/**
 * Notifies {@link DiscordRpcProxy} each time Eclipse's current selection changes.
 * 
 * @author Emmanuel CHEBBI
 */
public class NotifyDiscordRpcOnSelection implements ISelectionListener {
	
	private final DiscordRpcProxy rpc = new DiscordRpcProxy();
	
	public NotifyDiscordRpcOnSelection() {
		rpc.initialize();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		rpc.setDetails(part.getTitle());
	}
	
}
