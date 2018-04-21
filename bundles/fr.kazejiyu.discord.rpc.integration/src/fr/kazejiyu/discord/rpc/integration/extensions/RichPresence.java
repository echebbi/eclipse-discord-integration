package fr.kazejiyu.discord.rpc.integration.extensions;

/**
 * Provides Discord's Rich Presence informations.
 * 
 * @author Emmanuel CHEBBI
 */
public class RichPresence {
	
	private String details;

	private String state;
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
