/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.adapters;

import java.io.File;
import java.net.URI;
import java.util.Optional;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * Default implementation of {@link EditorInputRichPresence}.<br>
 * <br>
 * This implementation only operates on {@link IURIEditorInput} instances and set Rich Presence as follows:
 * 
 * <table style="border: 1px solid black ; border-collapse: collapse">
 * 	<tr style="border: 1px solid black">
 * 		<th style="border: 1px solid black">Property</th>
 * 		<th style="border: 1px solid black">Shown in Discord</th>
 * 	</tr>
 * 	<tr>
 * 		<td style="border: 1px solid black"><b>Details</b></td>
 * 		<td style="border: 1px solid black">Editing <i>&lt;file.name&gt;</i></td>
 * 	</tr>
 * 	<tr>
 * 		<td style="border: 1px solid black"><b>State</b></td>
 * 		<td style="border: 1px solid black">Working on <i>&lt;project.name&gt;</i></td>
 * 	</tr>
 * </table>
 * 
 * @author Emmanuel CHEBBI
 */
public class DefaultURIEditorInputRichPresence implements EditorInputRichPresence {
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public Class<FileStoreEditorInput> getExpectedEditorInputClass() {
		return FileStoreEditorInput.class;
		
		// TODO Handle inheritance so that returning IURIEditorInput.class here does not prevent
		// DefaultFileEditorInputRichPresence to be taken into account at runtime
//		return IURIEditorInput.class;
	}
	
	@Override
	public Optional<RichPresence> createRichPresence(GlobalPreferences preferences, IEditorInput input) {
		if (!(input instanceof IURIEditorInput))
			throw new IllegalArgumentException("input must be an instance of " + IURIEditorInput.class);
		
		IURIEditorInput uriInput = (IURIEditorInput) input;
		URI fileURI = uriInput.getURI();
		File file = new File(fileURI.getPath());
		
		RichPresence presence = new RichPresence();
		
		presence.withDetails(detailsOf(preferences, file));
		presence.withState(stateOf(preferences));
		presence.withLanguage(languageOf(preferences, file));
		presence.withLargeImageText(largeImageTextOf(preferences, file));
		
		return Optional.of(presence);
	}

	private String detailsOf(GlobalPreferences preferences, File file) {
		if (! preferences.showsFileName())
			return "";
		
		return "Editing " + file.getName();
	}

	private String stateOf(GlobalPreferences preferences) {
		if (! preferences.showsProjectName())
			return "";
		
		return "Unknown project";
	}

	private Language languageOf(UserPreferences preferences, File file) {
		if (! preferences.showsFileName())
			return Language.UNKNOWN;
		
		return Language.fromFileExtension(file.getName());
	}

	private String largeImageTextOf(UserPreferences preferences, File file) {
		if (! preferences.showsFileName())
			return "";
		
		Language language = Language.fromFileExtension(file.getName());
		
		if (language == Language.UNKNOWN)
			return "";
		
		return "Programming in " + language.getName();
	}

}
