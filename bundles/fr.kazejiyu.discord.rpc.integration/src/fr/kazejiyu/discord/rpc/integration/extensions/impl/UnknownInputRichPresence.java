/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.extensions.impl;

import java.util.Optional;

import org.eclipse.ui.IEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.DiscordIntegrationPreferences;

/**
 * Used when no valid adapter can be found for a given {@link IEditorInput}.<br>
 * <br>
 * Clear information shown in Discord.
 * 
 * @author Emmanuel CHEBBI
 */
public class UnknownInputRichPresence implements EditorInputRichPresence {

	@Override
	public Class<? extends IEditorInput> getExpectedEditorInputClass() {
		return IEditorInput.class;
	}

	@Override
	public Optional<RichPresence> createRichPresence(DiscordIntegrationPreferences preferences, IEditorInput input) {
		return Optional.of(new RichPresence());
	}

}
