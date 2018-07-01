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

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.kazejiyu.discord.rpc.integration.languages.Language;

/**
 * Manages the communication with Discord.<br>
 * <br>
 * Instances of this class are aimed to send Rich Presence information
 * to a Discord client so that it can show it.<br>
 * 
 * @author Emmanuel CHEBBI
 */
public class DiscordRpcProxy {
	
	/** Identifies the Eclipse Integration Discord application */
	private static final String APPLICATION_ID = "413038514616139786";
	
	/** Cache last presence so that it's easier to update it later */
	private RichPresence lastPresence = null;
	
	/**
	 * Initialises the connection to Discord session.
	 */
	public void initialize() {
		DiscordRPC.INSTANCE.Discord_Initialize(APPLICATION_ID, createHandlers(), true, "");
	}
	
	/** @return the handlers handling Discord events */
	private DiscordEventHandlers createHandlers() {
		return new DiscordEventHandlers();
	}
	
	/**
	 * Shows given presence on Discord.
	 * 
	 * @param rp
	 * 			Contains the elements to show on Discord.
	 * 			Must not be {@code null}.
	 */
	public void show(RichPresence rp) {
		lastPresence = new RichPresence(rp);
		
		DiscordRichPresence presence = new DiscordRichPresence();
		
		rp.getState().ifPresent(state -> presence.state = state);
		rp.getDetails().ifPresent(details -> presence.details = details);
		rp.getStartTimestamp().ifPresent(start -> presence.startTimestamp = start);
		rp.getLargeImageText().ifPresent(text -> presence.largeImageText = text);
		rp.getLanguage().map(Language::getKey).ifPresent(key -> presence.largeImageKey = key);
		
		DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
		DiscordRPC.INSTANCE.Discord_RunCallbacks();
	}
	
	/**
	 * Changes the details shown in Discord, keeping the other information.<br>
	 * <br>
	 * Passing either an empty String or {@code null} will hide the details field. 
	 * 
	 * @param details
	 * 			The new details to show.
	 */
	public void updateDetails(String details) {
		show(lastPresence.withDetails(details));
	}
	
	/**
	 * Changes the state shown in Discord, keeping the other information.<br>
	 * <br>
	 * Passing either an empty String or {@code null} will hide the state field. 
	 * 
	 * @param state
	 * 			The new state to show.
	 */
	public void updateState(String state) {
		show(lastPresence.withState(state));
	}
	
	/**
	 * Changes the elapsed time shown in Discord, keeping the other information.<br>
	 * <br>
	 * Passing a negative timestamp will hide the elapsed time field. 
	 * 
	 * @param start
	 * 			The start timestamp.
	 */
	public void updateStartTimestamp(long start) {
		show(lastPresence.withStartTimestamp(start));
	}
	
	/**
	 * Shutdowns the connection to Discord session.<br>
	 * <br>
	 * If this method is called while the connection has already being closed, it has no effect.
	 */
	public void shutdown() {
		DiscordRPC.INSTANCE.Discord_Shutdown();
	}
}
