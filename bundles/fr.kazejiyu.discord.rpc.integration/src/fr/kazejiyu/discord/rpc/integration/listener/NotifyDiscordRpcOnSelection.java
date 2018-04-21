package fr.kazejiyu.discord.rpc.integration.listener;

import java.io.File;
import java.net.URI;
import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.EditorPart;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcProxy;

/**
 * Notifies {@link DiscordRpcProxy} each time Eclipse's current selection changes.
 * 
 * @author Emmanuel CHEBBI
 */
public class NotifyDiscordRpcOnSelection implements ISelectionListener, IPartListener2 {
	
	private final DiscordRpcProxy rpc = new DiscordRpcProxy();
	
	private IWorkbenchPart lastSelectedPart = null;
	
	public NotifyDiscordRpcOnSelection() {
		rpc.initialize();
		rpc.setDefault();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(part instanceof EditorPart) || part.equals(lastSelectedPart))
				return;
		
		EditorPart editor = (EditorPart) part;
		
		// Generic file editor
		if (editor.getEditorInput() instanceof IFileEditorInput) {
			IFile inEdition = ((IFileEditorInput) editor.getEditorInput()).getFile();
			IProject project = inEdition.getProject();
			
			String details = "Editing " + inEdition.getName();
			String state = (project != null) ? "Working on " + project.getName() : "";
				
			rpc.setInformations(details, state);
			lastSelectedPart = part;
		}
		// Also handles Files located outside of the workspace
		else if (editor.getEditorInput() instanceof IURIEditorInput) {
			URI inEdition = ((IURIEditorInput) editor.getEditorInput()).getURI();
			
			File editedFile = new File(inEdition.getPath());
			
			String details = editedFile.exists() ? "Editing " + editedFile.getName() : "";
			String state = "Unknown project";
				
			rpc.setInformations(details, state);
			lastSelectedPart = part;
		}
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
	public void partBroughtToTop(IWorkbenchPartReference partRef) {}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {}
	
}
