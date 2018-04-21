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

import fr.kazejiyu.discord.rpc.integration.core.DiscordIntegrationPreferences;
import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcProxy;

/**
 * Notifies {@link DiscordRpcProxy} each time Eclipse's current selection changes.
 * 
 * @author Emmanuel CHEBBI
 */
public class NotifyDiscordRpcOnSelection implements ISelectionListener, IPartListener2 {
	
	/** Communicates informations to Discord */
	private final DiscordRpcProxy rpc = new DiscordRpcProxy();
	
	private IWorkbenchPart lastSelectedPart = null;

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
		
		EditorPart editor = (EditorPart) part;
		
		// Generic file editor
		if (editor.getEditorInput() instanceof IFileEditorInput) {
			IFile inEdition = ((IFileEditorInput) editor.getEditorInput()).getFile();
			IProject project = inEdition.getProject();
			
			String details, state;
			
			if (preferences.showsFileName())
				details = "Editing " + inEdition.getName();
			else
				details = "Editing a mysterious file";
			
			if (preferences.showsProjectName())
				state = (project != null) ? "Working on " + project.getName() : "";
			else
				state = "Working on a mysterious project";	
				
			rpc.setInformations(details, state);
			lastSelectedPart = part;
		}
		// Also handles Files located outside of the workspace
		else if (editor.getEditorInput() instanceof IURIEditorInput) {
			URI inEdition = ((IURIEditorInput) editor.getEditorInput()).getURI();
			
			File editedFile = new File(inEdition.getPath());
			
			String details, state;
			
			if (preferences.showsFileName())
				details = editedFile.exists() ? "Editing " + editedFile.getName() : "";
			else
				details = "Editing a mysterious file";
			
			if (preferences.showsProjectName())
				state = "Unknown project";
			else
				state = "Working on a mysterious project";
				
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
