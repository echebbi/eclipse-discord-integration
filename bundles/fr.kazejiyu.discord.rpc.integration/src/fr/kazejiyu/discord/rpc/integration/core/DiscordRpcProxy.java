/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.core;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.kazejiyu.discord.rpc.integration.languages.Language;

/**
 * Sends {@link RichPresence}s to Discord.<br>
 * <br>
 * The information detailed by the presence (state, image, etc.) are then shown in Discord's UI.
 * 
 * @author Emmanuel CHEBBI
 */
public class DiscordRpcProxy implements DiscordRpcLifecycle {
    
    /** Identifies the Discord Rich Presence for Eclipse IDE application. */
    private static final String APPLICATION_ID = "413038514616139786";
    
    /** Whether the proxy is connected to a Discord client. */
    private boolean isConnected = false;
    
    @Override
    public void initialize() {
        DiscordRPC.INSTANCE.Discord_Initialize(APPLICATION_ID, createHandlers(), true, "");
        this.isConnected = true;
    }
    
    /** Returns the handlers handling Discord events. */
    private DiscordEventHandlers createHandlers() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = user -> isConnected = true;
        handlers.disconnected = (status, message) -> isConnected = false;
        
        return handlers;
    }
    
    @Override
    public boolean isConnected() {
        return this.isConnected;
    }
    
    @Override
    public void show(RichPresence rp) {
        if (isConnected) {
            DiscordRichPresence presence = new DiscordRichPresence();
            
            rp.getState().ifPresent(state -> presence.state = state);
            rp.getDetails().ifPresent(details -> presence.details = details);
            rp.getStartTimestamp().ifPresent(start -> presence.startTimestamp = start);
            rp.getLargeImageText().ifPresent(text -> presence.largeImageText = text);
            rp.getLanguage().map(Language::getKey).ifPresent(key -> presence.largeImageKey = key);
            
            DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
            DiscordRPC.INSTANCE.Discord_RunCallbacks();
        }
    }
    
    @Override
    public void showNothing() {
        show(new ImmutableRichPresence());
    }
    
    @Override
    public void shutdown() {
        DiscordRPC.INSTANCE.Discord_Shutdown();
        this.isConnected = false;
    }
}
