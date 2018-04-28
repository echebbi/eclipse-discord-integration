/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.listener;

import fr.kazejiyu.discord.rpc.integration.settings.Moment;
import fr.kazejiyu.discord.rpc.integration.settings.SettingChangeListener;

/**
 * Runs a {@link Runnable} each time a plug-in's setting is modified.
 * 
 * @author Emmanuel CHEBBI
 */
class RunOnSettingChange implements SettingChangeListener {

	private final Runnable updateDiscord;
	
	public RunOnSettingChange(Runnable updateDiscord) {
		this.updateDiscord = updateDiscord;
	}

	@Override
	public void fileNameVisibilityChanged(boolean isVisible) {
		updateDiscord.run();
	}

	@Override
	public void projectNameVisibilityChanged(boolean isVisible) {
		updateDiscord.run();
	}

	@Override
	public void elapsedTimeVisibilityChanged(boolean isVisible) {
		updateDiscord.run();
	}

	@Override
	public void elapsedTimeResetMomentChanged(Moment oldMoment, Moment newMoment) {
		updateDiscord.run();
	}

}
