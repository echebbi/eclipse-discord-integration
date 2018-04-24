package fr.kazejiyu.discord.rpc.integration.settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Constants used to idendify the different available settings.
 * 
 * @author Emmanuel CHEBBI
 */
public enum Settings {
	
	SHOW_FILE_NAME("SHOW_FILE_NAME"),
	
	SHOW_PROJECT_NAME("SHOW_PROJECT_NAME"),
	
	SHOW_ELAPSED_TIME("SHOW_ELAPSED_TIME"),
	
	RESET_ELAPSED_TIME("RESET_ELAPSED_TIME"),
	
	RESET_ELAPSED_TIME_ON_STARTUP("RESET_ELAPSED_TIME_ON_STARTUP"),

	RESET_ELAPSED_TIME_ON_NEW_PROJECT("RESET_ELAPSED_TIME_ON_NEW_PROJECT"),
	
	RESET_ELAPSED_TIME_ON_NEW_FILE("RESET_ELAPSED_TIME_ON_NEW_FILE");
	
	/** A key identifying the setting */
	private final String property;
	
	private Settings(String property) {
		this.property = property;
	}
	
	public String property() {
		return property;
	}
	
	/** @return the setting corresponding to the given key, or null no one match */
	public static Settings fromProperty(String property) {
		return propertyToSetting.get(property);
	}
	
	private static final Map<String, Settings> propertyToSetting = new HashMap<>();

	static
    {
        for (Settings setting : Settings.values()) {
        	propertyToSetting.put(setting.property, setting);
        }
    }

}
