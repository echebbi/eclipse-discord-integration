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

import static fr.kazejiyu.discord.rpc.integration.languages.Language.UNKNOWN;

import java.util.Optional;

import org.eclipse.core.resources.IProject;

import fr.kazejiyu.discord.rpc.integration.languages.Language;

/**
 * Defines the elements to show in Discord.
 * 
 * @author Emmanuel CHEBBI
 */
public class RichPresence {
	
	private String details;

	private String state;
	
	private long startTimestamp;
	
	private Language language;
	
	private String largeImageText;
	
	private IProject project;

	/**
	 * Creates a new instance that will show nothing on Discord.
	 */
	public RichPresence() {
		reset();
	}
	
	/**
	 * Creates a copy of another {@code RichPresence}.
	 * 
	 * @param origin
	 * 			The presence to copy.
	 * 			Must not be {@code null}.
	 */
	public RichPresence(RichPresence origin) {
		this.details = origin.details;
		this.state = origin.state;
		this.startTimestamp = origin.startTimestamp;
		this.language = origin.language;
		this.largeImageText = origin.largeImageText;
		this.project = origin.project;
	}
	
	/**
	 * Resets specified values so that the presence will show nothing
	 * on Discord. 
	 * 
	 * @return the current instance, enabling method chaining
	 */
	public RichPresence reset() {
		withDetails("");
		withState("");
		withStartTimestamp(-1l);
		withLanguage(UNKNOWN);
		withLargeImageText("");
		return this;
	}
	
	/**
	 * Returns the details to show on Discord, if any.
	 * @return the details to show on Discord, if any
	 */
	public Optional<String> getDetails() {
		if (details.isEmpty())
			return Optional.empty();
		return Optional.of(details);
	}

	/**
	 * Sets the details to show on Discord.<br>
	 * <br>
	 * If the argument is either {@code null} or empty, 
	 * it is considered as no details and won't be shown on Discord.
	 * 
	 * @param details
	 * 			The details to show on Discord.
	 * 
	 * @return the current instance, enabling method chaining
	 */
	public RichPresence withDetails(String details) {
		this.details = details == null ? "" : details;
		return this;
	}

	/**
	 * Returns the state to show on Discord, if any.
	 * @return the state to show on Discord, if any
	 */
	public Optional<String> getState() {
		if (state.isEmpty())
			return Optional.empty();
		return Optional.of(state);
	}

	/**
	 * Sets the state to show on Discord.<br>
	 * <br>
	 * If the argument is either {@code null} or empty, 
	 * it is considered as no details and won't be shown on Discord.
	 * 
	 * @param state
	 * 			The state to show on Discord.
	 * 
	 * @return the current instance, enabling method chaining
	 */
	public RichPresence withState(String state) {
		this.state = state == null ? "" : state;
		return this;
	}
	
	/**
	 * Returns the start timestamp, if any.
	 * @return the start timestamp, if any
	 */
	public Optional<Long> getStartTimestamp() {
		if (startTimestamp < 0)
			return Optional.empty();
		return Optional.of(startTimestamp);
	}

	/**
	 * Sets the start timestamp to show on Discord.<br>
	 * <br>
	 * Corresponds to the time elapsed.<br>
	 * <br>
	 * If the argument is lower than 0, it is considered as
	 * no timestamp and won't be shown on Discord.
	 * 
	 * @param timestamp
	 * 			The timestamp to show on Discord.
	 * 
	 * @return the current instance, enabling method chaining
	 */
	
	public RichPresence withStartTimestamp(long timestamp) {
		this.startTimestamp = timestamp;
		return this;
	}

	/**
	 * Sets the start timestamp to show on Discord.<br>
	 * <br>
	 * Corresponds to the time elasped.<br>
	 * <br>
	 * If the argument is , 
	 * it is considered as no details and won't be shown on Discord.
	 * 
	 * @param details
	 * 			The details to show on Discord.
	 * 
	 * @return the current instance, enabling method chaining
	 */
	public RichPresence withCurrentTimestamp() {
		return withStartTimestamp(System.currentTimeMillis() / 1000);
	}
	
	/**
	 * Returns the language of the active file, if known.
	 * @return the language of the active file, if known
	 */
	public Optional<Language> getLanguage() {
		if (language == null || language == UNKNOWN)
			return Optional.empty();
		return Optional.of(language);
	}

	/**
	 * Sets the language of the active file. Defines the large icon to show in Discord.<br>
	 * <br>
	 * If the argument is {@link Language.UNKNOWN}, it is considered as
	 * no language and no icon will be shown in Discord.
	 * 
	 * @param language
	 * 			The language of the active file.
	 * 
	 * @return the current instance, enabling method chaining
	 */
	
	public RichPresence withLanguage(Language language) {
		this.language = language;
		return this;
	}
	
	/**
	 * Returns the text to show when hovering the large icon, if any.
	 * @return the text to show when hovering the large icon, if any
	 */
	public Optional<String> getLargeImageText() {
		if (largeImageText == null || largeImageText.isEmpty())
			return Optional.empty();
		return Optional.of(largeImageText);
	}

	/**
	 * Sets the text to show when hovering the large icon in Discord.<br>
	 * <br>
	 * If the argument is either {@code null} or an empty String, it is considered as
	 * no text won't be sent to Discord.
	 * 
	 * @param hover
	 * 			The text to show when hovering the large icon in Discord.
	 * 
	 * @return the current instance, enabling method chaining
	 */
	
	public RichPresence withLargeImageText(String hover) {
		this.largeImageText = hover;
		return this;
	}
	
	public Optional<IProject> getProject() {
		return Optional.ofNullable(project);
	}

	public RichPresence withProject(IProject project) {
		this.project = project;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((details == null) ? 0 : details.hashCode());
		result = prime * result + (int) (startTimestamp ^ (startTimestamp >>> 32));
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RichPresence))
			return false;
		RichPresence other = (RichPresence) obj;
		if (details == null) {
			if (other.details != null)
				return false;
		} else if (!details.equals(other.details))
			return false;
		if (startTimestamp != other.startTimestamp)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}
	
}
