/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.core;

import static java.util.Objects.requireNonNull;

import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * Sends {@link RichPresence}s to Discord ensuring that they follow {@link UserPreferences user's preferences}.<br>
 * <br>
 * If a given presence does not match user's preferences it is modified in order to hide private information.
 * For instance, if the user has specified that the name of the project should not be displayed in Discord then the presence's state is erased. 
 */
public class PreferredDiscordRpc implements DiscordRpcLifecycle {
	
	/** Communicates information to Discord */
	private final DiscordRpcLifecycle discord;

	/** User's preferences */
	private final GlobalPreferences preferences;

	// Used to manage elapsed time
	private SelectionTimes times;
	
	/**
	 * Creates a new instance.
	 * 
	 * @param discord
	 * 			The proxy used to send presences to Discord.
	 * 			Must not be null.
	 * @param preferences
	 * 			Allows to check user's preferences.
	 * 			Must not be null.
	 */
	public PreferredDiscordRpc(DiscordRpcLifecycle discord, GlobalPreferences preferences, SelectionTimes times) {
		this.discord = requireNonNull(discord, "Discord proxy must not be null");
		this.preferences = requireNonNull(preferences, "Preferences must not be null");
		this.times = requireNonNull(times, "The times of selection must not be null");
	}
	
	@Override
	public void initialize() {
		discord.initialize();
	}
	
	@Override
	public boolean isConnected() {
		return discord.isConnected();
	}
	
	@Override
	public void shutdown() {
		discord.shutdown();
	}

	@Override
	public void show(RichPresence presence) {
		RichPresence presenceFollowingPreferences = new PreferredRichPresence(
			preferences.getApplicablePreferencesFor(presence.getProject().orElse(null)), 
			presence, 
			times
		);
		
		discord.show(presenceFollowingPreferences);
	}
	
	@Override
	public void showNothing() {
		discord.showNothing();
	}
	
}
