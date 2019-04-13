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

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_FILE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_STARTUP;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_LANGUAGE_ICON;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_RICH_PRESENCE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_PROJECT_SETTINGS;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

import fr.kazejiyu.discord.rpc.integration.Activator;

/**
 * User preferences regarding the way information are shown in Discord
 * for a specific {@link IProject}. 
 * 
 * @author Emmanuel CHEBBI
 */
public class ProjectPreferences implements UserPreferences {
    
    /** Used to retrieve the preferences. */
    private final IEclipsePreferences preferences;
    
    private final Collection<SettingChangeListener> listeners = new ArrayList<>();

    /**
     * Creates a new instance aimed to check Discord preferences for {@code project}.
     * 
     * @param project
     *             The project to check. Must not be {@code null}.
     * 
     * @throws IllegalArgumentException if the preferences of the given project cannot
     *                                     be determined
     */
    public ProjectPreferences(IProject project) {
        IScopeContext context = new ProjectScope(project);
        this.preferences = context.getNode(Activator.PLUGIN_ID);
        
        if (preferences == null) {
            throw new IllegalArgumentException("Cannot find preferences for plug-in " + Activator.PLUGIN_ID + " in project " + project);
        }
        this.preferences.addPreferenceChangeListener(new ProjectPreferencesListener(listeners));
    }
    
    /** 
     * Returns whether the project should use global or project preferences.
     * @return true if the project should use project preferences, false otherwise 
     */
    public boolean useProjectSettings() {
        return preferences.getBoolean(USE_PROJECT_SETTINGS.property(), false);
    }
    
    @Override
    public boolean showsFileName() {
        return preferences.getBoolean(SHOW_FILE_NAME.property(), true);
    }
    
    @Override
    public boolean showsProjectName() {
        return preferences.getBoolean(SHOW_PROJECT_NAME.property(), true);
    }
    
    @Override
    public boolean showsElapsedTime() {
        return preferences.getBoolean(SHOW_ELAPSED_TIME.property(), true);
    }
    
    @Override
    public boolean showsLanguageIcon() {
        return preferences.getBoolean(SHOW_LANGUAGE_ICON.property(), true);
    }
    
    @Override
    public boolean showsRichPresence() {
        return preferences.getBoolean(SHOW_RICH_PRESENCE.property(), true);
    }
    
    @Override
    public boolean resetsElapsedTimeOnStartup() {
        return preferences.get(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property())
                          .equals(RESET_ELAPSED_TIME_ON_STARTUP.property());
    }
    
    @Override
    public boolean resetsElapsedTimeOnNewProject() {
        return preferences.get(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property())
                          .equals(RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
    }
    
    @Override
    public boolean resetsElapsedTimeOnNewFile() {
        return preferences.get(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property())
                          .equals(RESET_ELAPSED_TIME_ON_NEW_FILE.property());
    }
    
    @Override
    public Optional<String> getProjectName() {
        String name = preferences.get(PROJECT_NAME.property(), "");
        
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(name);
    }

    /**
     * Registers a new listener that will be called each time a property change.
     * 
     * @param listener
     *             The listener to register.
     */
    public void addSettingChangeListener(SettingChangeListener listener) {
        listeners.add(requireNonNull(listener, "Cannot register a null listener"));
    }

    /**
     * Unregisters a listener so that it will no longer being notified of events.
     * 
     * @param listener
     *             The listener to unregister.
     */
    public void removeSettingChangeListener(SettingChangeListener listener) {
        listeners.remove(listener);
    }

}
