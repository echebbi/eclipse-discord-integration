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
import org.eclipse.ui.part.FileEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;

/**
 * Extracts {@link RichPresence} from an {@link IEditorInput}.<br>
 * <br>
 * This interface should be implemented by clients who aim to define
 * the information shown in Discord for their own editor.
 * 
 * @author Emmanuel CHEBBI
 */
public interface EditorInputRichPresence extends Comparable<EditorInputRichPresence> {
	
	/**
	 * Helps to choose an adapter over another when several ones
	 * are registered for the same {@code IEditorInput}.<br>
	 * <br>
	 * The higher the priority, the more the adapter will be favored.<br>
	 * <br>
	 * For instance, given two adapters registering themselves for inputs of type
	 * {@link FileEditorInput} and which priorities are 0 and 1, then the adapter
	 * of priority 1 will be chosen to handle the input.<br>
	 * <br>
	 * Built-in adapters have a priority of 0. Hence, giving a higher priority
	 * ensure that the adapter will be preferred over default ones.<br>
	 * <br>
	 * It is advised to only choose tens, such as 10 or 20, instead of digits
	 * so that it is easier to add new adapters later if needed.
	 * 
	 * @return the priority associated with this adapter.
	 */
	int getPriority();
	
	/**
	 * Returns the class of the input expected as an argument of {@link #createRichPresence(GlobalPreferences, IEditorInput)}.
	 * @return the class of the input expected as an argument of {@link #createRichPresence(GlobalPreferences, IEditorInput)}
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
	Optional<RichPresence> createRichPresence(GlobalPreferences preferences, IEditorInput input);

	@Override
	default int compareTo(EditorInputRichPresence rhs) {
		if (this.getPriority() < rhs.getPriority())
			return -1;
		if (this.getPriority() > rhs.getPriority())
			return 1;
		return 0;
	}
	
}
