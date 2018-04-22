package fr.kazejiyu.discord.rpc.integration.extensions;

import org.eclipse.core.resources.IProject;

/**
 * Provides Discord's Rich Presence informations.
 * 
 * @author Emmanuel CHEBBI
 */
public class RichPresence {
	
	private String details;

	private String state;
	
	private IProject project;
	
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
	
	public IProject getProject() {
		return project;
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
