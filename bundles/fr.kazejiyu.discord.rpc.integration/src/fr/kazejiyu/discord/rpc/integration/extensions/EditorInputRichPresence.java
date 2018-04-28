/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.extensions;

import java.util.Optional;

import org.eclipse.ui.IEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.DiscordIntegrationPreferences;

/**
 * Extracts Rich Presence from an {@link IEditorInput}.<br>
 * <br>
 * This interface should be implemented by clients who aim to define
 * the information to show in Discord for their own editor.
 * 
 * @author Emmanuel CHEBBI
 */
public interface EditorInputRichPresence {
	
	/**
	 * Returns the class of the input expected as an argument of {@link #createRichPresence(DiscordIntegrationPreferences, IEditorInput)}.
	 * @return the class of the input expected as an argument of {@link #createRichPresence(DiscordIntegrationPreferences, IEditorInput)}
	 */
	Class<? extends IEditorInput> getExpectedEditorInputClass();
	
	/**
	 * Creates the Rich Presence information to send to Discord.
	 * 
	 * @param preferences
	 * 			User's preferences regarding the information to show in Discord.
	 * 			Must not be {@code null}.
	 * @param input
	 * 			The input of the active editor. 
	 * 			Must satisfy {@code getExpectedEditorInputClass().isInstance(input) == true}.
	 * 
	 * @return the information to show in Discord
	 */
	Optional<RichPresence> createRichPresence(DiscordIntegrationPreferences preferences, IEditorInput input);

}
