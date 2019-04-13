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

import java.util.Optional;

/**
 * User preferences regarding the way information are shown in Discord.
 * 
 * @author Emmanuel CHEBBI
 */
public interface UserPreferences {
    
    /** 
     * Returns whether the name of the edited file should be shown in Discord.
     * @return true if the name of the edited file should be shown, false otherwise 
     */
    boolean showsFileName();
    
    /** 
     * Returns whether the name of the current project should be shown in Discord.
     * @return true if the name of the current project should be shown, false otherwise
     */
    boolean showsProjectName();
    
    /** 
     * Returns whether the elapsed time should be shown in Discord.
     * @return true if the elapsed time should be shown, false otherwise 
     */
    boolean showsElapsedTime();
    
    /** 
     * Returns whether the programming language's icon should be shown in Discord.
     * @return true of the programming language's icon should be shown, false otherwise
     */
    boolean showsLanguageIcon();
    
    /** 
     * Returns whether the Rich Presence should be shown in Discord.
     * @return true if the Rich Presence should be shown, false otherwise
     */
    boolean showsRichPresence();
    
    /** 
     * Returns whether the name of the current project should be shown in Discord.
     * @return true if the name of the current project should be shown, false otherwise
     */
    boolean resetsElapsedTimeOnStartup();
    
    /** 
     * Returns whether the name of the current project should be shown in Discord.
     * @return true if the name of the current project should be shown, false otherwise
     */
    boolean resetsElapsedTimeOnNewProject();
    
    /**
     * Returns whether the name of the current project should be shown in Discord.
     * @return true if the name of the current project should be shown, false otherwise
     */
    boolean resetsElapsedTimeOnNewFile();
    
    /** 
     * Returns the name of the current project, if the user specified one.
     * @return the name of the current project, if the user specified one
     */
    Optional<String> getProjectName();

}
