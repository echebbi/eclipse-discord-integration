/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.settings;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.DEFAULT_DISCORD_APPLICATION_ID;
import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Function;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcLifecycle;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.files.EditionContext;

/**
 * Updates the information shown in Discord each time a preference changes.
 * <p>
 * In order to work, an instance of this class has to be registered as a listener
 * thanks to {@link UserPreferences#addSettingChangeListener(SettingChangeListener)}.
 * 
 * @author Emmanuel CHEBBI
 */
// TODO [Refactor] Use a State pattern to handle changing behavior.
//                 As the number of settings to deal with grows this class
//                 becomes more and more complex. A State pattern could mitigate
//                 this complexity by isolating the various "if-else" statements
//                 in dedicated classes, such as:
//                      - ConnectedToDefaultApplication
//                      - ConnectedToCustomApplication
//                      - DisconnectedFromDiscord
public class UpdateDiscordOnSettingChange implements SettingChangeListener {
    
    /** Used to retrieve the active editor. */
    private final EditionContext context;
    
    /** Used to create RPC corresponding to the active editor when Discord has to be updated. */
    private final Function<EditionContext, Optional<RichPresence>> toRichPresence;
    
    /** Used to update the information shown in Discord. */
    private final DiscordRpcLifecycle discord;
    
    /** Used to consult user's settings. */
    private final GlobalPreferences globalPreferences;
    
    /**
     * Creates a new listener charged of updating Discord when a preference changes.
     * 
     * @param context
     *          The context 
     * @param toRichPresence
     *          The adapter used to create a RPC based on the active editor.
     * @param discord
     *          The proxy used to send informations to Discord.
     * @param preferences
     *          The preferences used to check user's settings.
     */
    public UpdateDiscordOnSettingChange(EditionContext context, Function<EditionContext, Optional<RichPresence>> toRichPresence, DiscordRpcLifecycle discord, GlobalPreferences preferences) {
        this.context = requireNonNull(context, "The context must not be null");
        this.toRichPresence = requireNonNull(toRichPresence, "The RichPresence adapter must not be null");
        this.discord = requireNonNull(discord, "The Discord proxy must not be null");
        this.globalPreferences = requireNonNull(preferences, "The preferences must not be null");
    }

    /*
     * Notice that thanks to 'toRichPresence' this method is ensured
     * to update Discord using the appropriate preferences.
     */
    private void updateDiscord() {
        // Create the presence according to relevant preferences
        Optional<RichPresence> presence = toRichPresence.apply(context);
        
        if (presence.isPresent()) {
            discord.show(presence.get());
        }
        else {
            discord.showNothing();
        }
    }

    @Override
    public void richPresenceVisibilityChanged(boolean isVisible) {
        if (! isVisible) {
            // Just close the connection with Discord if nothing should be shown
            discord.shutdown();
            return;
        }
        if (discord.isConnected()) {
            // Nothing to do if RPC should be shown & we are already connected
            return;
        }
        // From there we assume that the user want to show RPC & that we are not connected yet
        discord.initialize(applicationId());
        updateDiscord();
    }
    
    @Override
    public void customDiscordApplicationVisibilityChanged(boolean shouldUse) {
        // The RichPresence is required to access the preferences that should be used
        // to process currently activated editor's underlying resource
        Optional<RichPresence> presence = toRichPresence.apply(context);
        UserPreferences preferences = applicablePreferencesFor(presence);
        
        if (! preferences.showsRichPresence()) {
            // We should not be connected to Discord at all
            return;
        }
        if (preferences.usesCustomDiscordApplication() && ! preferences.getDiscordApplicationId().isPresent()) {
            // The user wants to use its own application but did not specify any ID yet
            return;
        }
        String expectedDiscordAppId = preferences.usesCustomDiscordApplication() ? preferences.getDiscordApplicationId().orElse("")
                                                                                 : DEFAULT_DISCORD_APPLICATION_ID;
        if (discord.isConnectedTo(expectedDiscordAppId)) {
            // Nothing to do: we are already connected to the right Discord application
            return;
        }
        discord.shutdown();
        discord.initialize(expectedDiscordAppId);
        updateDiscord();
    }

    @Override
    public void discordApplicationIdChanged(String newApplicationId) {
        boolean weAreAlreadyConnectedToThisApp = discord.isConnectedTo(newApplicationId);
        if (weAreAlreadyConnectedToThisApp) {
            // Might happen if project-specific preferences of the active editor
            // were already targeting the new application id
            return;
        }
        // The RichPresence is required to access the preferences that should be used
        // for currently activated editor's underlying resource
        Optional<RichPresence> presence = toRichPresence.apply(context);
        UserPreferences preferences = applicablePreferencesFor(presence);
            
        if (! preferences.showsRichPresence()) {
            // We should not be connected to Discord at all
            return;
        }
        if (! preferences.usesCustomDiscordApplication()) {
            // Only the default Discord application should be used for the active editor 
            return;
        }
        if (! newApplicationId.equals(preferences.getDiscordApplicationId().orElse(null))) {
            // A custom Discord application should be used for the active editor, but not the given one.
            // Can happen if the active editor have project-specific preferences while we're notified
            // of a global-preference change.
            return;
        }
        
        // From there, we know that:
        //      - we are not connected to the custom Discord application
        //      - we should initialize a new connection to the given application
        
        discord.shutdown();
        discord.initialize(newApplicationId);
        updateDiscord();
    }
    
    private UserPreferences applicablePreferencesFor(Optional<RichPresence> presence) {
        if (! presence.isPresent()) {
            return globalPreferences;
        }
        return presence.flatMap(RichPresence::getProject)
                       .map(globalPreferences::getApplicablePreferencesFor)
                       .orElse(globalPreferences); // Without a project, we cannot access resource's specific preferences and hence assume that global preferences apply
    }
    
    /**
     * Returns the ID of the Discord application relevant to the active editor.
     * @return the ID of the Discord application relevant to the active editor
     */
    private String applicationId() {
        Optional<RichPresence> presence = toRichPresence.apply(context);
        UserPreferences applicablePreferences = applicablePreferencesFor(presence);
        
        if (applicablePreferences.usesCustomDiscordApplication()) {
            return applicablePreferences.getDiscordApplicationId().orElse("");
        }
        else {
            return DEFAULT_DISCORD_APPLICATION_ID;
        }
    }
    
    @Override
    public void useProjectProperties(boolean use) {
        updateDiscord();
    }
    

    @Override
    public void fileNameVisibilityChanged(boolean isVisible) {
        updateDiscord();
    }

    @Override
    public void projectNameVisibilityChanged(boolean isVisible) {
        updateDiscord();
    }

    @Override
    public void languageIconVisibilityChanged(boolean isVisible) {
        updateDiscord();
    }

    @Override
    public void elapsedTimeVisibilityChanged(boolean isVisible) {
        updateDiscord();
    }

    @Override
    public void elapsedTimeResetMomentChanged(Moment oldMoment, Moment newMoment) {
        updateDiscord();
    }

    @Override
    public void projectNameChanged(String oldName, String newName) {
        updateDiscord();
    }

    @Override
    public void useCustomWording(boolean shouldUse) {
        updateDiscord();
    }

    @Override
    public void detailsWordingChanged(String oldWording, String newWording) {
        updateDiscord();
    }

    @Override
    public void stateWordingChanged(String oldWording, String newWording) {
        updateDiscord();
    }

}
