/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.ui.preferences;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;


public class DiscordIntegrationPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String PREFERENCES_STORE_ID = "fr.kazejiyu.discord.rpc.integration";
			
	public DiscordIntegrationPreferencesPage() {
		super(FLAT);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, PREFERENCES_STORE_ID));
		setDescription("Customize the way informations are shown in Discord");
	}

	@Override
    public void createFieldEditors() {
		GroupFieldEditor group = new GroupFieldEditor("Privacy", getFieldEditorParent());
		
        BooleanFieldEditor showFileName = new BooleanFieldEditor(SHOW_FILE_NAME.property(), "Show &file name", group.getFieldEditorParent());
        BooleanFieldEditor showProjectName = new BooleanFieldEditor(SHOW_PROJECT_NAME.property(), "Show &project name", group.getFieldEditorParent());
        BooleanFieldEditor showElapsedTime = new BooleanFieldEditor(SHOW_ELAPSED_TIME.property(), "Show &elapsed time", group.getFieldEditorParent());
        
        group.addFieldEditor(showFileName);
        group.addFieldEditor(showProjectName);
        group.addFieldEditor(showElapsedTime);
        
        addField(group);
        
        addField(new RadioGroupFieldEditor(RESET_ELAPSED_TIME.property(), "&Reset elapsed time:", 3,
                new String[][] { { "On startup", "RESET_ELAPSED_TIME_ON_STARTUP" }, 
        						 { "On new project", "RESET_ELAPSED_TIME_ON_NEW_PROJECT" },
                				 { "On new file", "RESET_ELAPSED_TIME_ON_NEW_FILE" } },
				getFieldEditorParent()
		));
        
    }
    
}
