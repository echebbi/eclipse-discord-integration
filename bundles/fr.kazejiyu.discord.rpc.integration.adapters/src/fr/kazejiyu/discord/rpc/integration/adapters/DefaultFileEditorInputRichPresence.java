package fr.kazejiyu.discord.rpc.integration.adapters;

import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.DiscordIntegrationPreferences;

/**
 * Default implementation of {@link EditorInputRichPresence}.<br>
 * <br>
 * This implementation only operates on {@link IFileEditorInput} instances and set Rich Presence as follows:
 * 
 * <table style="border: 1px solid black ; border-collapse: collapse">
 * 	<tr style="border: 1px solid black">
 * 		<th style="border: 1px solid black">Property</th>
 * 		<th style="border: 1px solid black">If shown</th>
 * 		<th style="border: 1px solid black">If not shown</th>
 * 	</tr>
 * 	<tr>
 * 		<td style="border: 1px solid black"><b>Details</b></td>
 * 		<td style="border: 1px solid black">Editing <i>&lt;file.name&gt;</i></td>
 * 		<td style="border: 1px solid black">Working on a mysterious file</td>
 * 	</tr>
 * 	<tr>
 * 		<td style="border: 1px solid black"><b>State</b></td>
 * 		<td style="border: 1px solid black">Working on <i>&lt;project.name&gt;</i></td>
 * 		<td style="border: 1px solid black">Working on a mysterious project</td>
 * 	</tr>
 * </table>
 * 
 * @author Emmanuel CHEBBI
 */
public class DefaultFileEditorInputRichPresence implements EditorInputRichPresence {
	
	@Override
	public Class<IFileEditorInput> getExpectedEditorInputClass() {
		return IFileEditorInput.class;
	}
	
	@Override
	public Optional<RichPresence> createRichPresence(DiscordIntegrationPreferences preferences, IEditorInput input) {
		if (!(input instanceof IFileEditorInput))
			throw new IllegalArgumentException("input must be an instance of " + IFileEditorInput.class);
		
		RichPresence presence = new RichPresence();
		IFileEditorInput fileInput = (IFileEditorInput) input;
		
		presence.withDetails(detailsOf(preferences, fileInput));
		presence.withState(stateOf(preferences, fileInput));
		presence.withProject(projectOf(preferences, fileInput));
		
		return Optional.of(presence);
	}

	private String detailsOf(DiscordIntegrationPreferences preferences, IFileEditorInput input) {
		if (! preferences.showsFileName())
			return "";
		
		IFile inEdition = input.getFile();
		
		return "Editing " + inEdition.getName();
	}

	private String stateOf(DiscordIntegrationPreferences preferences, IFileEditorInput input) {
		if (! preferences.showsProjectName())
			return "";
		
		IFile inEdition = input.getFile();
		IProject project = inEdition.getProject();
		
		return "Working on " + ((project != null) ? project.getName() : "an unknown project");
	}

	private IProject projectOf(DiscordIntegrationPreferences preferences, IFileEditorInput input) {
		IFile inEdition = input.getFile();
		return inEdition.getProject();
	}

}
