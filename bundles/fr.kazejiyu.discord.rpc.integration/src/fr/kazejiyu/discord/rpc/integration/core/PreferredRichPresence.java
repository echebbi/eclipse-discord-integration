/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.core;

import java.util.Optional;

import org.eclipse.core.resources.IProject;

import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * A {@link ImmutableRichPresence} that cares about {@link UserPreferences}. 
 */
public class PreferredRichPresence implements RichPresence {
	
	private final UserPreferences preferences;
	
	private final RichPresence presence;
	
	private final SelectionTimes times;

	public PreferredRichPresence(UserPreferences prefs, RichPresence presence, SelectionTimes times) {
		this.preferences = prefs;
		this.presence = presence;
		this.times = times;
	}
	
	@Override
	public Optional<Language> getLanguage() {
		if (! preferences.showsLanguageIcon())
			return Optional.of(Language.UNKNOWN);
		
		return presence.getLanguage();
	}
	
	@Override
	public Optional<Long> getStartTimestamp() {
		if (! preferences.showsElapsedTime())
			return Optional.empty();
		
		if (preferences.resetsElapsedTimeOnNewFile())
			return Optional.of(times.onSelection());
		
		if (preferences.resetsElapsedTimeOnNewProject())
			return Optional.of(times.onNewProject());
		
		// last possible case: the time starts on startup
		return Optional.of(times.onStartup());
	}

	@Override
	public Optional<String> getDetails() {
		return presence.getDetails();
	}

	@Override
	public Optional<String> getState() {
		return presence.getState();
	}

	@Override
	public Optional<String> getLargeImageText() {
		return presence.getLargeImageText();
	}

	@Override
	public Optional<IProject> getProject() {
		return presence.getProject();
	}
}
