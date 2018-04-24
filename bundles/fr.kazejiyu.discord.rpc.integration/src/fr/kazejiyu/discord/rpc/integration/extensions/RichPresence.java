package fr.kazejiyu.discord.rpc.integration.extensions;

import java.util.Optional;

import org.eclipse.core.resources.IProject;

/**
 * Defines the elements to show on Discord.
 * 
 * @author Emmanuel CHEBBI
 */
public class RichPresence implements Cloneable {
	
	private String details;

	private String state;
	
	private long startTimestamp;
	
	private IProject project;

	/**
	 * Creates a new instance that will show nothing on Discord.
	 */
	public RichPresence() {
		reset();
	}
	
	public RichPresence(RichPresence origin) {
		this.details = origin.details;
		this.state = origin.state;
		this.startTimestamp = origin.startTimestamp;
		this.project = origin.project;
	}
	
	public RichPresence clone() {
		return new RichPresence(this);
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
	
	public Optional<IProject> getProject() {
		return Optional.ofNullable(project);
	}

	public RichPresence withProject(IProject project) {
		this.project = project;
		return this;
	}

}
