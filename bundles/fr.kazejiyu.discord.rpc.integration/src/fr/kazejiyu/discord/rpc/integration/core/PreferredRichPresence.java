/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.core;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.eclipse.core.resources.IProject;

import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * A {@link RichPresence} that cares about {@link UserPreferences}.<br>
 * <br>
 * An instance of this class decorates another {@link RichPresence} and enhances it
 * in order to in order to follow user's preferences:
 * <ul>
 *     <li>hide the {@link Language},
 *     <li>select the right start timestamp.
 * </ul>
 */
public final class PreferredRichPresence implements RichPresence {
    
    /** Used to determine which information the user wants to hide. */
    private final UserPreferences preferences;
    
    /** The original, decorated presence. */
    public final RichPresence presence;
    
    /** Provides access to the different timestamps. */
    private final SelectionTimes times;

    /**
     * Creates a new {@link RichPresence} caring about user's preferences.
     * 
     * @param prefs
     *             The preferences to follow. Must not be null.
     * @param presence
     *             The original presence to decorate. Must not be null.
     * @param times
     *             The available timestamps. Must not be null.
     */
    public PreferredRichPresence(UserPreferences prefs, RichPresence presence, SelectionTimes times) {
        this.preferences = requireNonNull(prefs, "The preferences must not be null");
        this.presence = requireNonNull(presence, "The decorated presence must not be null");
        this.times = requireNonNull(times, "The times must not be null");
    }
    
    @Override
    public Optional<Language> getLanguage() {
        if (! preferences.showsLanguageIcon()) {
            return Optional.empty();
        }
        return presence.getLanguage();
    }
    
    @Override
    public Optional<Long> getStartTimestamp() {
        if (! preferences.showsElapsedTime()) {
            return Optional.empty();
        }
        if (preferences.resetsElapsedTimeOnNewFile()) {
            return Optional.of(times.onSelection());
        }
        if (preferences.resetsElapsedTimeOnNewProject()) {
            return Optional.of(times.onNewProject());
        }
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
