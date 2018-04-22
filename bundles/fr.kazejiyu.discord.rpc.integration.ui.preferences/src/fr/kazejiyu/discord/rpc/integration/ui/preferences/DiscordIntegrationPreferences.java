package fr.kazejiyu.discord.rpc.integration.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class DiscordIntegrationPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public DiscordIntegrationPreferences() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, "fr.kazejiyu.discord.rpc.integration.preferences.store"));
		setDescription("Customize the way informations are shown in Discord");
	}

	@Override
    public void createFieldEditors() {
        addField(new BooleanFieldEditor("SHOW_FILE_NAME", "Show &file's name", getFieldEditorParent()));
        addField(new BooleanFieldEditor("SHOW_PROJECT_NAME", "Show &project's name", getFieldEditorParent()));
        
        addField(new RadioGroupFieldEditor("RESET_ELAPSED_TIME", "&Reset elapsed time:", 3,
                new String[][] { { "On startup", "RESET_ELAPSED_TIME_ON_STARTUP" }, 
        						 { "On new project", "RESET_ELAPSED_TIME_ON_NEW_PROJECT" },
                				 { "On new file", "RESET_ELAPSED_TIME_ON_NEW_FILE" } },
				getFieldEditorParent()
		));
    }
    
}
