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

import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * Default implementation of {@link EditorInputRichPresence}.<br>
 * <br>
 * This implementation only operates on {@link IFileEditorInput} instances and set Rich Presence as follows:
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
public class DefaultFileEditorInputRichPresence implements EditorInputRichPresence {
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public Class<IFileEditorInput> getExpectedEditorInputClass() {
		return IFileEditorInput.class;
	}
	
	@Override
	public Optional<RichPresence> createRichPresence(GlobalPreferences preferences, IEditorInput input) {
		if (!(input instanceof IFileEditorInput))
			throw new IllegalArgumentException("input must be an instance of " + IFileEditorInput.class);
		
		RichPresence presence = new RichPresence();
		IFileEditorInput fileInput = (IFileEditorInput) input;
		IProject project = projectOf(preferences, fileInput);
		presence.withProject(project);
		
		UserPreferences applicablePreferences = preferences.getApplicablePreferencesFor(project);
		
		presence.withDetails(detailsOf(applicablePreferences, fileInput));
		presence.withState(stateOf(applicablePreferences, fileInput));
		
		return Optional.of(presence);
	}

	private String detailsOf(UserPreferences preferences, IFileEditorInput input) {
		if (! preferences.showsFileName())
			return "";
		
		IFile inEdition = input.getFile();
		
		return "Editing " + inEdition.getName();
	}

	private String stateOf(UserPreferences preferences, IFileEditorInput input) {
		if (! preferences.showsProjectName())
			return "";
		
		IFile inEdition = input.getFile();
		IProject project = inEdition.getProject();
		
		return "Working on " + ((project != null) ? project.getName() : "an unknown project");
	}

	private IProject projectOf(GlobalPreferences preferences, IFileEditorInput input) {
		IFile inEdition = input.getFile();
		return inEdition.getProject();
	}

}
