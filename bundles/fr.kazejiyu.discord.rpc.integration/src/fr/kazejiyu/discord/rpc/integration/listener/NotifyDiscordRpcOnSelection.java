package fr.kazejiyu.discord.rpc.integration.listener;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;

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
		if (!(part instanceof EditorPart))
				return;
		
		EditorPart editor = (EditorPart) part;
		
		if (editor.getEditorInput() instanceof IFileEditorInput) {
			IFile inEdition = ((IFileEditorInput) editor.getEditorInput()).getFile();
			IProject project = inEdition.getProject();
			
			String details = "Editing " + inEdition.getName();
			String state = (project == null) ? "" : "Working on " + project.getName();
				
			rpc.setInformations(details, state);
		}
	}
	
}
