/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.settings;

import java.util.Optional;

/**
 * User preferences regarding the way information are shown in Discord.
 * 
 * @author Emmanuel CHEBBI
 */
public interface UserPreferences {
	
	/** @return whether the name of the edited file should be shown in Discord */
	boolean showsFileName();
	
	/** @return whether the name of the current project should be shown in Discord */
	boolean showsProjectName();
	
	/** @return whether the time elapsed should be shown in Discord */
	boolean showsElapsedTime();
	
	/** @return whether the programming language's icon should be shown in Discord */
	boolean showsLanguageIcon();
	
	/** @return whether the Rich Presence should be shown in Discord */
	boolean showsRichPresence();
	
	/** @return whether the name of the current project should be shown in Discord */
	boolean resetsElapsedTimeOnStartup();
	
	/** @return whether the name of the current project should be shown in Discord */
	boolean resetsElapsedTimeOnNewProject();
	
	/** @return whether the name of the current project should be shown in Discord */
	boolean resetsElapsedTimeOnNewFile();
	
	/** @return the name of a given project, if the user specified one */
	Optional<String> getProjectName();

}
