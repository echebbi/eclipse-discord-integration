package fr.kazejiyu.discord.rpc.integration.extensions;

import java.util.Optional;

import org.eclipse.ui.IEditorInput;

import fr.kazejiyu.discord.rpc.integration.settings.DiscordIntegrationPreferences;

/**
 * Extracts Rich Presence from an {@link IEditorInput}.
 * 
 * @author Emmanuel CHEBBI
 */
public interface EditorInputRichPresence {
	
	/**
	 * Returns the class of the input expected as an argument of {@link #createRichPresence(DiscordIntegrationPreferences, IEditorInput)}.
	 * @return the class of the input expected as an argument of {@link #createRichPresence(DiscordIntegrationPreferences, IEditorInput)}
	 */
	Class<? extends IEditorInput> getExpectedEditorInputClass();
	
	/**
	 * Creates the Rich Presence information to send to Discord.
	 * 
	 * @param preferences
	 * 			User's preferences regarding the information to show in Discord.
	 * 			Must not be {@code null}.
	 * @param input
	 * 			The input of the active editor. 
	 * 			Must satisfy {@code getExpectedEditorInputClass().isInstance(input) == true}.
	 * 
	 * @return the information to show in Discord
	 */
	Optional<RichPresence> createRichPresence(DiscordIntegrationPreferences preferences, IEditorInput input);

}
