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

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.CUSTOM_APP_ID;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_FILE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_STARTUP;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_LANGUAGE_ICON;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_RICH_PRESENCE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_CUSTOM_APP;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import fr.kazejiyu.discord.rpc.integration.Activator;

/**
 * User preferences regarding the way information are shown in Discord
 * by default. 
 * 
 * @author Emmanuel CHEBBI
 */
public class GlobalPreferences implements UserPreferences {
    
    private static final String PREFERENCES_STORE_ID = Activator.PLUGIN_ID;

    private Collection<SettingChangeListener> listeners = new ArrayList<>();
    
    private final IPreferenceStore store;
    
    /**
     * Creates a new instance aimed to check Discord preferences.
     */
    public GlobalPreferences() {
        this.store = new ScopedPreferenceStore(InstanceScope.INSTANCE, PREFERENCES_STORE_ID);
        this.store.addPropertyChangeListener(new GlobalPreferencesListener(listeners));
    }
    
    @Override
    public boolean showsFileName() {
        return store.getBoolean(SHOW_FILE_NAME.property());
    }
    
    @Override
    public boolean showsProjectName() {
        return store.getBoolean(SHOW_PROJECT_NAME.property());
    }
    
    @Override
    public boolean showsElapsedTime() {
        return store.getBoolean(SHOW_ELAPSED_TIME.property());
    }
    
    @Override
    public boolean showsLanguageIcon() {
        return store.getBoolean(SHOW_LANGUAGE_ICON.property());
    }
    
    @Override
    public boolean showsRichPresence() {
        return store.getBoolean(SHOW_RICH_PRESENCE.property());
    }
    
    @Override
    public boolean resetsElapsedTimeOnStartup() {
        return store.getString(RESET_ELAPSED_TIME.property()).equals(RESET_ELAPSED_TIME_ON_STARTUP.property());
    }
    
    @Override
    public boolean resetsElapsedTimeOnNewProject() {
        return store.getString(RESET_ELAPSED_TIME.property()).equals(RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
    }
    
    @Override
    public boolean resetsElapsedTimeOnNewFile() {
        return store.getString(RESET_ELAPSED_TIME.property()).equals(RESET_ELAPSED_TIME_ON_NEW_FILE.property());
    }
    
    @Override
    public Optional<String> getProjectName() {
        return Optional.empty();
    }
    
    @Override
    public boolean usesCustomDiscordApplication() {
        return store.getBoolean(USE_CUSTOM_APP.property());
    }
    
    @Override
    public Optional<String> getDiscordApplicationId() {
        String discordApplicationId = store.getString(CUSTOM_APP_ID.property());
        
        if (discordApplicationId.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(discordApplicationId);
    }
    
    /**
     * <p>Returns the user preferences that should be applied for {@code project}.</p>
     * 
     * <p>If {@code project} uses global preferences, the method returns {@code this}.</p>>
     * 
     * <p>Otherwise, it returns a new {@link ProjectPreferences} describing the specific preferences
     * of this project.</p>
     * 
     * @param project
     *             The project to check.
     * 
     * @return the preferences that should apply to {@code project}.
     */
    public UserPreferences getApplicablePreferencesFor(IProject project) {
        try {
            final ProjectPreferences projectPreferences = new ProjectPreferences(project);
            
            if (projectPreferences.useProjectSettings()) {
                return projectPreferences;
            }
            // This anonymous class aims at managing the preferences that are set
            // specifically for an IProject but that apply independently of whether
            // the 'Use Project Settings' property is true or false.
            // TODO [Refactor] Create a dedicated class ?
            return new GlobalPreferences() {
                @Override
                public Optional<String> getProjectName() {
                    return projectPreferences.getProjectName();
                }

                @Override
                public boolean usesCustomDiscordApplication() {
                    if (projectPreferences.usesCustomDiscordApplication()) {
                        return true;
                    }
                    return super.usesCustomDiscordApplication();
                }

                @Override
                public Optional<String> getDiscordApplicationId() {
                    Optional<String> projectScopeCustomApp = projectPreferences.getDiscordApplicationId();
                            
                    if (projectScopeCustomApp.isPresent()) {
                        return projectScopeCustomApp;
                    }
                    return super.getDiscordApplicationId();
                }
            };
        } 
        catch (IllegalArgumentException e) {
            // The project is not valid (e.g. is null), return default preferences
        }
        return this;
    }

    @Override
    public void addSettingChangeListener(SettingChangeListener listener) {
        listeners.add(requireNonNull(listener, "Cannot register a null listener"));
    }

    @Override
    public void removeSettingChangeListener(SettingChangeListener listener) {
        listeners.remove(listener);
    }
}
