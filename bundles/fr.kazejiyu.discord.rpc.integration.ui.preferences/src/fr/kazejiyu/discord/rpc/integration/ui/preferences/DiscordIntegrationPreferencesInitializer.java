package fr.kazejiyu.discord.rpc.integration.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class DiscordIntegrationPreferencesInitializer extends AbstractPreferenceInitializer {
	
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		
        store.setDefault("SHOW_FILE_NAME", true);
        store.setDefault("SHOW_PROJECT_NAME", true);
        store.setDefault("RESET_ELAPSED_TIME", "RESET_ELAPSED_TIME_ON_PROJECT");
	}

}
