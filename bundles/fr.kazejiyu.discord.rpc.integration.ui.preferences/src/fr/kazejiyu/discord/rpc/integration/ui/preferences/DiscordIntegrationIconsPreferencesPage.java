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

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.CUSTOM_APP_ID;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_CUSTOM_APP;
import static fr.kazejiyu.discord.rpc.integration.ui.preferences.DiscordIntegrationPreferencesPage.PREFERENCES_STORE_ID;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * A page allowing users to tailor icons shown in Discord.
 */
public class DiscordIntegrationIconsPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    /**
     * Creates a new page.
     */
    public DiscordIntegrationIconsPreferencesPage() {
        super(FLAT);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, PREFERENCES_STORE_ID));
        setDescription("Customize icons shown in Discord by using your own Discord application (https://discordapp.com/developers):");
    }

    @Override
    public void createFieldEditors() {
        BooleanFieldEditor useCustomApplication = new BooleanFieldEditor(USE_CUSTOM_APP.property(), "Use &custom application", getFieldEditorParent());
        StringFieldEditor customApplicationId = new StringFieldEditor(CUSTOM_APP_ID.property(), "ID of the Discord application to use:", getFieldEditorParent());
        
        addField(useCustomApplication);
        addField(customApplicationId);
    }   

}
