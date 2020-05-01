/*******************************************************************************
 * Copyright (C) 2018-2020 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.core;

import java.util.Optional;

/**
 * Defines an object aimed to manage plug-in's Rich Presence by communicating with Discord. 
 */
public interface DiscordRpcLifecycle extends AutoCloseable {

    /**
     * Initializes the connection to Discord session.
     * 
     * @param applicationId
     *          The ID of the Discord application to connect.
     * 
     * @see #shutdown()
     */
    void initialize(String applicationId);
    
    /**
     * Returns whether the instance is currently connected to a Discord client.
     * @return {@code true} if the instance is connected to a Discord client,
     *            {@code false} otherwise
     */
    boolean isConnected();
    
    /**
     * Returns whether the proxy is connected to the given Discord application.
     * 
     * @param applicationId
     *          The ID of the Discord application to which the proxy may be connected.
     *          
     * @return true if the proxy is connected to the given Discord application, false otherwise
     */
    default boolean isConnectedTo(String applicationId) {
        return this.discordApplicationId()
                   .map(id -> id.equals(applicationId))
                   .orElse(false);
    }
    
    /**
     * Returns the ID of the Discord application to which the object is currently connected.
     * @return the ID of the Discord application if a connection has been made, nothing otherwise
     */
    Optional<String> discordApplicationId();
    
    /**
     * <p>Shows given presence on Discord.</p>
     * 
     * <p>Has no effect if {@link #isConnected()} is false.</p>
     * 
     * @param rp
     *             Contains the elements to show on Discord.
     *             Must not be {@code null}.
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
     * <p>Shutdowns the connection to Discord session.</p>
     * 
     * <p>If this method is called while the connection has already being closed, it has no effect.</p>
     * 
     * @see #initialize(String)
     */
    void shutdown();
    
    @Override
    default void close() {
        shutdown();
    }

}
