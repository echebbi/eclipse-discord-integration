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

import static fr.kazejiyu.discord.rpc.integration.languages.Language.UNKNOWN;

import java.util.Optional;

import org.eclipse.core.resources.IProject;

import fr.kazejiyu.discord.rpc.integration.languages.Language;

/**
 * <p>Defines the elements to show in Discord.</p>
 * 
 * <p>Instances of this class are immutable which means that, once built, they are thread safe.</p>
 */
public final class ImmutableRichPresence implements RichPresence {
    
    private final String details;

    private final String state;
    
    private final long startTimestamp;
    
    private final Language language;
    
    private final String largeImageText;
    
    private final IProject project;

    /**
     * Creates a new instance that will show nothing on Discord.
     */
    public ImmutableRichPresence() {
        this("", "", -1L, UNKNOWN, "", null);
    }
    
    /**
     * Kept private because there are too many parameters.
     * Users should use with* methods instead. 
     */
    private ImmutableRichPresence(String details, String state, long startTimestamp, Language language,
            String largeImageText, IProject project) {
        this.details = details;
        this.state = state;
        this.startTimestamp = startTimestamp;
        this.language = language;
        this.largeImageText = largeImageText;
        this.project = project;
    }


    @Override
    public Optional<String> getDetails() {
        if (details.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(details);
    }

    /**
     * Creates a new presence similar to this new but with new details.
     * <br>
     * If the argument is either {@code null} or empty, 
     * it is considered as no details and won't be shown on Discord.
     * 
     * @param details
     *             The details to show on Discord.
     * 
     * @return a new presence with different details
     */
    public ImmutableRichPresence withDetails(String details) {
        return new ImmutableRichPresence(
            details == null ? "" : details,
            state,
            startTimestamp,
            language,
            largeImageText,
            project
        );
    }

    @Override
    public Optional<String> getState() {
        if (state.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(state);
    }

    /**
     * Creates a new presence similar to this one but with a new state.<br>
     * <br>
     * If the argument is either {@code null} or empty, 
     * it is considered as no details and won't be shown on Discord.
     * 
     * @param state
     *             The state to show on Discord.
     * 
     * @return a new presence specifying a different state
     */
    public ImmutableRichPresence withState(String state) {
        return new ImmutableRichPresence(
            details,
            state == null ? "" : state,
            startTimestamp,
            language,
            largeImageText,
            project
        );
    }
    
    @Override
    public Optional<Long> getStartTimestamp() {
        if (startTimestamp < 0) {
            return Optional.empty();
        }
        return Optional.of(startTimestamp);
    }

    /**
     * Creates a new presence similar to this one but with a new start timestamp.<br>
     * <br>
     * The start timestamp allows to specify the elapsed time.<br>
     * <br>
     * If the argument is lower than 0, it is considered as
     * no timestamp and won't be shown on Discord.
     * 
     * @param timestamp
     *             The timestamp to show on Discord, in seconds
     * 
     * @return a new presence specifying a different start timestamp
     * 
     * @see #withCurrentTimestamp()
     */
    public ImmutableRichPresence withStartTimestamp(long timestamp) {
        return new ImmutableRichPresence(
            details,
            state,
            timestamp,
            language,
            largeImageText,
            project
        );
    }

    /**
     * Creates a new presence similar to this one but with a new start timestamp.<br>
     * <br>
     * As the start timestamp is set to the current time this method is a shortcut for
     * {@code withStartTimestamp(System.currentTimeMillis() / 1000}.<br>
     * 
     * @return a new presence specifying a different start timestamp
     */
    public ImmutableRichPresence withCurrentTimestamp() {
        return withStartTimestamp(System.currentTimeMillis() / 1000);
    }
    
    @Override
    public Optional<Language> getLanguage() {
        if (language == UNKNOWN) {
            return Optional.empty();
        }
        return Optional.of(language);
    }

    /**
     * Creates a new presence similar to this one but with a different language.<br>
     * <br>
     * The language defines the large icon to show in Discord.<br>
     * <br>
     * If the argument is either null or {@link Language.UNKNOWN}, it is considered as
     * no language and no icon will be shown in Discord.
     * 
     * @param language
     *             The language of the active file.
     * 
     * @return a new presence specifying a different timestamp
     */
    public ImmutableRichPresence withLanguage(Language language) {
        return new ImmutableRichPresence(
            details,
            state,
            startTimestamp,
            language == null ? UNKNOWN : language,
            largeImageText,
            project
        );
    }
    
    @Override
    public Optional<String> getLargeImageText() {
        if (largeImageText.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(largeImageText);
    }

    /**
     * Creates a new presence similar to this one but with a different hover text.
     * <br>
     * If the argument is either {@code null} or an empty String, it is considered as
     * no text.
     * 
     * @param hover
     *             The text to show when hovering the large icon in Discord.
     * 
     * @return a presence similar to this one but with a different hover
     */
    public ImmutableRichPresence withLargeImageText(String hover) {
        return new ImmutableRichPresence(
            details,
            state,
            startTimestamp,
            language,
            hover == null ? "" : hover,
            project
        );
    }
    
    @Override
    public Optional<IProject> getProject() {
        return Optional.ofNullable(project);
    }

    /**
     * Creates a new instance equal to this one but with the specified project.
     * 
     * @param project
     *             The project associated with the new rich presence instance.
     * 
     * @return a presence similar to this one but with a different project
     */
    public ImmutableRichPresence withProject(IProject project) {
        return new ImmutableRichPresence(
            details,
            state,
            startTimestamp,
            language,
            largeImageText,
            project
        );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((details == null) ? 0 : details.hashCode());
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        result = prime * result + ((largeImageText == null) ? 0 : largeImageText.hashCode());
        result = prime * result + ((project == null) ? 0 : project.hashCode());
        result = prime * result + (int) (startTimestamp ^ (startTimestamp >>> 32));
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ImmutableRichPresence)) {
            return false;
        }
        ImmutableRichPresence other = (ImmutableRichPresence) obj;
        if (details == null) {
            if (other.details != null) {
                return false;
            }
        } 
        else if (!details.equals(other.details)) {
            return false;
        }
        if (language != other.language) {
            return false;
        }
        if (largeImageText == null) {
            if (other.largeImageText != null) {
                return false;
            }
        } 
        else if (!largeImageText.equals(other.largeImageText)) {
            return false;
        }
        if (project == null) {
            if (other.project != null) {
                return false;
            }
        }
        else if (!project.equals(other.project)) {
            return false;
        }
        if (startTimestamp != other.startTimestamp) {
            return false;
        }
        if (state == null) {
            if (other.state != null) {
                return false;
            }
        }
        else if (!state.equals(other.state)) {
            return false;
        }
        return true;
    }

}
