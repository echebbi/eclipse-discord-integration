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

/**
 * Runs a {@link Runnable} each time a plug-in's setting is modified.
 * 
 * @author Emmanuel CHEBBI
 */
class RunOnSettingChange implements SettingChangeListener {
    
    private final DiscordRpcLifecycle discord;

    private final Runnable runnable;
    
    /**
     * Creates a new listener.
     * 
     * @param discord
     *          The proxy used to send messages to Discord.
     * @param runnable
     *          The instance to run each time a setting changes.
     */
    RunOnSettingChange(DiscordRpcLifecycle discord, Runnable runnable) {
        this.discord = requireNonNull(discord, "Cannot use a null Discord proxy");
        this.runnable = requireNonNull(runnable, "Cannot run a null runnable");
    }
    
    @Override
    public void useProjectProperties(boolean use) {
        runnable.run();
    }

    @Override
    public void fileNameVisibilityChanged(boolean isVisible) {
        runnable.run();
    }

    @Override
    public void projectNameVisibilityChanged(boolean isVisible) {
        runnable.run();
    }
    
    @Override
    public void languageIconVisibilityChanged(boolean isVisible) {
        runnable.run();
    }

    @Override
    public void elapsedTimeVisibilityChanged(boolean isVisible) {
        runnable.run();
    }
    
    @Override
    public void elapsedTimeResetMomentChanged(Moment oldMoment, Moment newMoment) {
        runnable.run();
    }
    
    @Override
    public void projectNameChanged(String oldName, String newName) {
        runnable.run();
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

}
