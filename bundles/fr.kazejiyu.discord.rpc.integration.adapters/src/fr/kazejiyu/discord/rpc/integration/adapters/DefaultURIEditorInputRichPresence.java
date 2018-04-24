package fr.kazejiyu.discord.rpc.integration.adapters;

import java.io.File;
import java.net.URI;
import java.util.Optional;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;

import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.RichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.DiscordIntegrationPreferences;

/**
 * Default implementation of {@link EditorInputRichPresence}.<br>
 * <br>
 * This implementation only operates on {@link IURIEditorInput} instances and set Rich Presence as follows:
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
public class DefaultURIEditorInputRichPresence implements EditorInputRichPresence {
	
	@Override
	public Class<FileStoreEditorInput> getExpectedEditorInputClass() {
		return FileStoreEditorInput.class;
		
		// TODO Handle inheritance so that returning IURIEditorInput.class here does not prevent
		// DefaultFileEditorInputRichPresence to be taken into account at runtime
//		return IURIEditorInput.class;
	}
	
	@Override
	public Optional<RichPresence> createRichPresence(DiscordIntegrationPreferences preferences, IEditorInput input) {
		if (!(input instanceof IURIEditorInput))
			throw new IllegalArgumentException("input must be an instance of " + IURIEditorInput.class);
		
		IURIEditorInput uriInput = (IURIEditorInput) input;
		
		RichPresence presence = new RichPresence();
		
		presence.withDetails(detailsOf(preferences, uriInput));
		presence.withState(stateOf(preferences, uriInput));
		
		return Optional.of(presence);
	}

	private String detailsOf(DiscordIntegrationPreferences preferences, IURIEditorInput input) {
		if (! preferences.showsFileName())
			return "";
		
		URI inEdition = input.getURI();
		File editedFile = new File(inEdition.getPath());
		
		return "Editing " + editedFile.getName();
	}

	private String stateOf(DiscordIntegrationPreferences preferences, IURIEditorInput input) {
		if (! preferences.showsProjectName())
			return "";
		
		return "Unknown project";
	}

}
