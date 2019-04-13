/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.listener;

import static java.util.Objects.requireNonNull;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcLifecycle;
import fr.kazejiyu.discord.rpc.integration.settings.Moment;
import fr.kazejiyu.discord.rpc.integration.settings.SettingChangeListener;
import fr.kazejiyu.discord.rpc.integration.settings.Settings;

/**
 * <p>Ensures that a {@link DiscordRpcLifecycle}'s connection with Discord
 * is in accordance with user's preferences.</p>
 * <p>
 * More specifically, an instance of this class listens for changes 
 * in the {@link Settings#SHOW_RICH_PRESENCE} property. 
 * When this property is set to true, then:
 * <ol>
 *     <li>given proxy is {@link DiscordRpcLifecycle#initialize() initialized} if disconnected
 *     <li>given runnable is executed
 * </ol>
 * When this property is set to false, then:
 * <ul>
 *     <li>given proxy is {@link DiscordRpcLifecycle#shutdown() disconnected}
 * </ul>
 * </p>
 */
public class SynchronizeConnection implements SettingChangeListener {
    
    private final DiscordRpcLifecycle discord;
    
    private final Runnable runnable;

    /**
     * Creates a new instance to manage {@code discord}'s connection.
     * 
     * @param discord
     *          The proxy to manage. 
     *          Must not be {@code null}.
     * @param runnable
     *          Will be executed each time the proxy is initialized.
     *          Must not be {@code null}.
     */
    public SynchronizeConnection(DiscordRpcLifecycle discord, Runnable runnable) {
        this.discord = requireNonNull(discord, "Cannot synchronize user preferences with a null Discord proxy");
        this.runnable = requireNonNull(runnable, "Cannot execute a null runnable");
    }

    @Override
    public void richPresenceVisibilityChanged(boolean isVisible) {
        if (isVisible && ! discord.isConnected()) {
            discord.initialize();
            runnable.run();
        }
        else if (! isVisible) {
            discord.shutdown();
        }
    }

    @Override
    public void useProjectProperties(boolean use) {
        // irrelevant event
    }

    @Override
    public void fileNameVisibilityChanged(boolean isVisible) {
        // irrelevant event
    }

    @Override
    public void projectNameVisibilityChanged(boolean isVisible) {
        // irrelevant event
    }

    @Override
    public void languageIconVisibilityChanged(boolean isVisible) {
        // irrelevant event
    }

    @Override
    public void elapsedTimeVisibilityChanged(boolean isVisible) {
        // irrelevant event
    }

    @Override
    public void elapsedTimeResetMomentChanged(Moment oldMoment, Moment newMoment) {
        // irrelevant event
    }

    @Override
    public void projectNameChanged(String oldName, String newName) {
        // irrelevant event
    }

}
