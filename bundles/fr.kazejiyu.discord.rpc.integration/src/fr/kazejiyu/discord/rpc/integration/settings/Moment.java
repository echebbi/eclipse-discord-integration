package fr.kazejiyu.discord.rpc.integration.settings;

/**
 * Represents an instant in plug-in's lifecycle.
 * 
 * @author Emmanuel CHEBBI
 */
public enum Moment {

	/** Eclipse startup */
	ON_STARTUP,
	
	/** Each time a new file is selected */
	ON_NEW_FILE,
	
	/** Each time the current project changes */
	ON_NEW_PROJECT;
	
}
