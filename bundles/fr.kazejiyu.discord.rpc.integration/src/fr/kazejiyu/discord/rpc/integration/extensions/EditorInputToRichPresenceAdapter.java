/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.extensions;

import java.util.Optional;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;

/**
 * Extracts {@link RichPresence} from an {@link IEditorInput}.
 * <p>
 * This interface should be implemented by clients who aim to define
 * the information shown in Discord for their own editor.
 * 
 * @author Emmanuel CHEBBI
 */
public interface EditorInputRichPresence extends Comparable<EditorInputRichPresence> {
    
    /**
     * Helps to choose an adapter over another when several ones
     * are registered for the same {@code IEditorInput}.
     * <p>
     * The higher the priority, the more the adapter will be favored.
     * <p>
     * For instance, given two adapters registering themselves for inputs of type
     * {@link FileEditorInput} and which priorities are 0 and 1, then the adapter
     * of priority 1 will be chosen to handle the input.
     * <p>
     * Built-in adapters have a priority of 0. Hence, giving a higher priority
     * ensures that the adapter will be preferred over default ones. This allows
     * to dynamically override other adapters if needed.
     * <p>
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
     * <p>
     * <b>Important</b>: this method may be called several times in a row with the same editor input. 
     * 
     * @param preferences
     *             User's preferences regarding the information to show in Discord.
     *             Must not be {@code null}.
     * @param input
     *             The input of the active editor. 
     *             Must satisfy {@code getExpectedEditorInputClass().isInstance(input) == true}.
     * 
     * @return the information to show in Discord if the input can be handled
     */
    Optional<RichPresence> createRichPresence(GlobalPreferences preferences, IEditorInput input);

    /**
     * Compares this with an other {@code EditorInputRichPresence} based on their priority.
     * 
     * @return a negative integer, zero, or a positive integer as this object's priority is less than, 
     *            equal to, or greater than the specified object's priority.
     */
    @Override
    default int compareTo(EditorInputRichPresence rhs) {
        return Integer.compare(this.getPriority(), rhs.getPriority());
    }
    
}
