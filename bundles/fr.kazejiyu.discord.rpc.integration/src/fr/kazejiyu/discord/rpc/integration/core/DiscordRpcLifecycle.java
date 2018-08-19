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

/**
 * Defines an object aimed to manage plug-in's Rich Presence by communicating with Discord. 
 */
public interface DiscordRpcLifecycle extends AutoCloseable {

	/**
	 * Initialises the connection to Discord session.
	 * 
	 * @see #shutdown()
	 */
	void initialize();
	
	/**
	 * Returns whether the instance is currently connected to a Discord client.
	 * @return {@code true} if the instance is connected to a Discord client,
	 * 		   {@code false} otherwise
	 */
	boolean isConnected();
	
	/**
	 * Shows given presence on Discord.<br>
	 * <br>
	 * Has no effect if {@link #isConnected()} is false.
	 * 
	 * @param rp
	 * 			Contains the elements to show on Discord.
	 * 			Must not be {@code null}.
	 * 
	 * @see #showNothing()
	 */
	void show(RichPresence rp);

	/**
	 * Clear Discord's rich presence so that only "Playing Eclipse IDE" is shown.
	 * 
	 * @see #show(RichPresence)
	 */
	void showNothing();

	/**
	 * Shutdowns the connection to Discord session.<br>
	 * <br>
	 * If this method is called while the connection has already being closed, it has no effect.
	 * 
	 * @see #initialize()
	 */
	void shutdown();
	
	@Override
	default void close() {
		shutdown();
	}

}
