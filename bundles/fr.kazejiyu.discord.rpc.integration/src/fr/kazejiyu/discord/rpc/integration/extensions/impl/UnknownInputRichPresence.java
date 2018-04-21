package fr.kazejiyu.discord.rpc.integration.extensions.impl;

import java.util.Optional;

import org.eclipse.ui.IEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.DiscordIntegrationPreferences;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.RichPresence;

/**
 * Used when no valid adapter can be found for a given {@link IEditorInput}.<br>
 * <br>
 * Clear information shown in Discord.
 * 
 * @author Emmanuel CHEBBI
 */
public class UnknownInputRichPresence implements EditorInputRichPresence {

	@Override
	public Class<? extends IEditorInput> getExpectedEditorInputClass() {
		return IEditorInput.class;
	}

	@Override
	public Optional<RichPresence> createRichPresence(DiscordIntegrationPreferences preferences, IEditorInput input) {
		RichPresence presence = new RichPresence();
		
		// TODO Set details & state to empty String as soon as Discord RPC API is fixed
		presence.setDetails("Unknown file");
		presence.setState("Unknown project");
		
		return Optional.of(presence);
	}

}
