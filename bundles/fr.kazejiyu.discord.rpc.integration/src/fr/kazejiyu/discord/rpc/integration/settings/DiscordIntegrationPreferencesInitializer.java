/*******************************************************************************
 * Copyright (C) 2018-2020 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.settings;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.CUSTOM_DISCORD_DETAILS_WORDING;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.CUSTOM_DISCORD_STATE_WORDING;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_LANGUAGE_ICON;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_RICH_PRESENCE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_CUSTOM_APP;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_CUSTOM_WORDING;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import fr.kazejiyu.discord.rpc.integration.Activator;

/**
 * Initializes the preferences for the preferences page.
 * 
 * @author Emmanuel CHEBBI
 */
public class DiscordIntegrationPreferencesInitializer extends AbstractPreferenceInitializer {
    
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
        
        store.setDefault(SHOW_FILE_NAME.property(), true);
        store.setDefault(SHOW_PROJECT_NAME.property(), true);
        store.setDefault(SHOW_ELAPSED_TIME.property(), true);
        store.setDefault(SHOW_LANGUAGE_ICON.property(), true);
        store.setDefault(SHOW_RICH_PRESENCE.property(), true);
        store.setDefault(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
        
        store.setDefault(USE_CUSTOM_APP.property(), false);
        
        store.setDefault(USE_CUSTOM_WORDING.property(), false);
        store.setDefault(CUSTOM_DISCORD_DETAILS_WORDING.property(), "Editing ${file}");
        store.setDefault(CUSTOM_DISCORD_STATE_WORDING.property(), "Working on ${project}");
    }

}
