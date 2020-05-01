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

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.kazejiyu.discord.rpc.integration.Plugin;
import fr.kazejiyu.discord.rpc.integration.languages.Language;

/**
 * Sends {@link RichPresence}s to Discord.<br>
 * <br>
 * The information detailed by the presence (state, image, etc.) are then shown in Discord's UI.
 * 
 * @author Emmanuel CHEBBI
 */
public class DiscordRpcProxy implements DiscordRpcLifecycle {
    
    /** Whether the proxy is connected to a Discord client. */
    private boolean isConnected = false;
    
    /** The ID of the Discord application we're currently connected to. */
    private String discordApplicationId;
    
    @Override
    public void initialize(String applicationId) {
        DiscordRPC.INSTANCE.Discord_Initialize(applicationId, createHandlers(), true, "");

        // Following attributes should be set in the 'ready' handlers
        // but for some reason it does not work very well.
        // Maybe just temporal issues? May investigate if I find the time to.
        
        this.isConnected = true;
        this.discordApplicationId = applicationId;
    }
    
    /** Returns the handlers handling Discord events. */
    private DiscordEventHandlers createHandlers() {
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = user -> isConnected = true;
        handlers.errored = (status, message) -> Plugin.log(message);
        handlers.disconnected = (status, message) -> isConnected = false;
        
        return handlers;
    }
    
    @Override
    public boolean isConnected() {
        return this.isConnected;
    }
    
    @Override
    public Optional<String> discordApplicationId() {
        if (! isConnected()) {
            return Optional.empty();
        }
        return Optional.of(discordApplicationId);
    }
    
    @Override
    public void show(RichPresence rp) {
        if (isConnected) {
            DiscordRichPresence presence = new DiscordRichPresence();
            
            presence.smallImageKey = "eclipse-ide-logo";
            presence.smallImageText = "Eclipse IDE";
            
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
