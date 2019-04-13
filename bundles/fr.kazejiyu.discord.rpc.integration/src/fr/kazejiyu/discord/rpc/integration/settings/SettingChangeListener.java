/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.settings;

/**
 * Listens for changes in plug-in's settings.
 * 
 * @author Emmanuel CHEBBI
 */
public interface SettingChangeListener {
    
    /**
     * Called when the user whether global preferences or project-specific ones.
     * 
     * @param use
     *          {@code true} if the user wants to use project preferences, 
     *          {@code false} otherwise.
     */
    void useProjectProperties(boolean use);

    /**
     * Called when the user changes file name's visibility.
     * 
     * @param isVisible
     *          {@code true} if the user wants to show the file name,
     *          {@code false} otherwise.
     */
    void fileNameVisibilityChanged(boolean isVisible);

    /**
     * Called when the user changes project name's visibility.
     * 
     * @param isVisible
     *          {@code true} if the user wants to show the project name,
     *          {@code false} otherwise.
     */
    void projectNameVisibilityChanged(boolean isVisible);

    /**
     * Called when the user changes language icon's visibility.
     * 
     * @param isVisible
     *          {@code true} if the user wants to show the language icon,
     *          {@code false} otherwise.
     */
    void languageIconVisibilityChanged(boolean isVisible);

    /**
     * Called when the user changes elapsed time's visibility.
     * 
     * @param isVisible
     *          {@code true} if the user wants to show the elapsed time,
     *          {@code false} otherwise.
     */
    void elapsedTimeVisibilityChanged(boolean isVisible);
    
    /**
     * Called when the user changes the moment where the elapsed time should be reset.
     * 
     * @param oldMoment
     *          The previous value.
     * @param newMoment
     *          The new value.
     */
    void elapsedTimeResetMomentChanged(Moment oldMoment, Moment newMoment);
    
    /**
     * Called when the user choose to display a new name for a given project.
     * 
     * @param oldName
     *          The previous name. May be {@code null} or empty.
     * @param newName
     *          The new name to display
     */
    void projectNameChanged(String oldName, String newName);
    
    /**
     * Called when the user choose to whether show or hide rich presence in Discord.
     * 
     * @param isVisible
     *          {@code true} if the user wants to show rich presence in Discord,
     *          {@code false} otherwise.
     */
    void richPresenceVisibilityChanged(boolean isVisible);
    
}
