/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.ui.preferences;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class DiscordIntegrationPreferencesInitializer extends AbstractPreferenceInitializer {
	
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
        store.setDefault(SHOW_FILE_NAME.property(), true);
        store.setDefault(SHOW_PROJECT_NAME.property(), true);
        store.setDefault(SHOW_ELAPSED_TIME.property(), true);
        store.setDefault(SHOW_LANGUAGE_ICON.property(), true);
        store.setDefault(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
	}

}
