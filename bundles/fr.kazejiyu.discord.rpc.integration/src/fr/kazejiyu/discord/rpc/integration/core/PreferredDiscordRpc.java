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

import java.util.Objects;

import org.eclipse.core.resources.IProject;

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
	
	private IProject lastSelectedProject = null;
	
	private SelectionTimes times = new SelectionTimes();
	
	/**
	 * Creates a new instance using {@link DiscordRpcProxy} to communicate with Discord and
	 * {@link GlobalPreferences} to check user's preferences.
	 */
	public PreferredDiscordRpc() {
		this(new DiscordRpcProxy(), new GlobalPreferences());
	}
	
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
	public PreferredDiscordRpc(DiscordRpcLifecycle discord, GlobalPreferences preferences) {
		this.discord = requireNonNull(discord, "Discord proxy must not be null");
		this.preferences = requireNonNull(preferences, "Preferences must not be null");
		
		this.discord.initialize();
		this.showNothing();
	}
	
	@Override
	public void initialize() {
		discord.initialize();
	}
	
	@Override
	public void shutdown() {
		discord.shutdown();
	}

	@Override
	public void show(RichPresence presence) {
		updateTimesOnSelection(presence);

		presence = new PreferredRichPresence(
				preferences.getApplicablePreferencesFor(presence.getProject().orElse(null)), presence, times
		);
		
		discord.show(presence);

		updateLastSelectedProject(presence);
	}
	
	@Override
	public void showNothing() {
		discord.showNothing();
	}
	
	private void updateLastSelectedProject(RichPresence presence) {
		lastSelectedProject = presence.getProject().orElse(null);
	}

	/** Updates times so that we know the time on the last selection */
	private void updateTimesOnSelection(RichPresence presence) {
		times = times.withNewSelection(isANewProject(presence));
	}
	
	/** @return whether the selection associated with the presence is in a new project */
	private boolean isANewProject(RichPresence presence) {
		return ! Objects.equals(presence.getProject().orElse(null), lastSelectedProject);
	}
}
