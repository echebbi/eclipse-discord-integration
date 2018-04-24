package fr.kazejiyu.discord.rpc.integration.settings;

/**
 * Listens for changes in plug-in's settings.
 * 
 * @author Emmanuel CHEBBI
 */
public interface SettingChangeListener {

	/**
	 * Called when the user changes file name's visibility.
	 * 
	 * @param isVisible
	 * 			{@code true} if the user wants to show the file name,
	 * 			{@code false} otherwise.
	 */
	void fileNameVisibilityChanged(boolean isVisible);

	/**
	 * Called when the user changes project name's visibility.
	 * 
	 * @param isVisible
	 * 			{@code true} if the user wants to show the project name,
	 * 			{@code false} otherwise.
	 */
	void projectNameVisibilityChanged(boolean isVisible);

	/**
	 * Called when the user changes elapsed time's visibility.
	 * 
	 * @param isVisible
	 * 			{@code true} if the user wants to show the elapsed time,
	 * 			{@code false} otherwise.
	 */
	void elapsedTimeVisibilityChanged(boolean isVisible);
	
	/**
	 * Called when the user changes the moment where the elapsed time should be reset.
	 * 
	 * @param oldMoment
	 * 			The previous value.
	 * @param newMoment
	 * 			The new value.
	 */
	void elapsedTimeResetMomentChanged(Moment oldMoment, Moment newMoment);
	
}
