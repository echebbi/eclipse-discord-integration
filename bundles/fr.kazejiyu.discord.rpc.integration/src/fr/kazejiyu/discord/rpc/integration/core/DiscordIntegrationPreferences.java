package fr.kazejiyu.discord.rpc.integration.core;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Provides easy access to the settings defined by the user for Discord Integration.
 * 
 * @author Emmanuel CHEBBI
 */
public enum DiscordIntegrationPreferences {
	
	INSTANCE;
	
	/** @return whether the name of the edited file should be shown in Discord */
	public boolean showsFileName() {
		IPreferenceStore s = new ScopedPreferenceStore(InstanceScope.INSTANCE, "fr.kazejiyu.discord.rpc.integration.preferences.store");
		return s.getBoolean("SHOW_FILE_NAME");
	}
	
	/** @return whether the name of the current project should be shown in Discord */
	public boolean showsProjectName() {
		IPreferenceStore s = new ScopedPreferenceStore(InstanceScope.INSTANCE, "fr.kazejiyu.discord.rpc.integration.preferences.store");
		return s.getBoolean("SHOW_PROJECT_NAME");
	}

}
