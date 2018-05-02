/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.settings;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_FILE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_STARTUP;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
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

	private List<SettingChangeListener> listeners = new ArrayList<>();
	
	private final IPropertyChangeListener listener;
	
	private final IPreferenceStore store;
	
	/**
	 * Creates a new instance aimed to check Discord preferences.
	 */
	public GlobalPreferences() {
		this.store = new ScopedPreferenceStore(InstanceScope.INSTANCE, PREFERENCES_STORE_ID);
		this.listener = new DiscordIntegrationPreferencesListener(listeners);
		
		this.store.addPropertyChangeListener(this.listener);
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
	
	/**
	 * Returns the user preferences that should be applied for {@code project}.<br>
	 * <br>
	 * If {@code project} uses global preferences, the method returns {@code this}.<br>
	 * <br>
	 * Otherwise, it returns a new {@link ProjectPreferences} describing the specific preferences
	 * of this project.
	 * 
	 * @param project
	 * 			The project to check.
	 * 
	 * @return the preferences that should apply to {@code project}.
	 */
	public UserPreferences getApplicablePreferencesFor(IProject project) {
		try {
			ProjectPreferences projectPreferences = new ProjectPreferences(project);
			
			if (projectPreferences.useProjectSettings())
				return projectPreferences;
		
		} catch (IllegalArgumentException e) {
			// The project is not valid, return default preferences
		}
		
		return this;
	}

	/**
	 * Registers a new listener that will be called each time a property change.
	 * 
	 * @param listener
	 * 			The listener to register.
	 */
	public void addSettingChangeListener(SettingChangeListener listener) {
		listeners.add(requireNonNull(listener, "Cannot register a null listener"));
	}

	/**
	 * Unregisters a listener so that it will no longer being notified of events.
	 * 
	 * @param listener
	 * 			The listener to unregister.
	 */
	public void removeSettingChangeListener(SettingChangeListener listener) {
		listeners.remove(listener);
	}
}
