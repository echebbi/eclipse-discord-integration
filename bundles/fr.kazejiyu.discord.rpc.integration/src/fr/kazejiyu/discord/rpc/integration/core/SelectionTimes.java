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

/**
 * The times at which selection changed. 
 */
public class SelectionTimes {

	private final long timeOnStartup;
	
	private final long timeOnNewProject;
	
	private final long timeOnNewSelection;
	
	public SelectionTimes() {
		this.timeOnStartup = this.timeOnNewProject = this.timeOnNewSelection = System.currentTimeMillis() / 1000;
	}
	
	protected SelectionTimes(long timeOnStartup, long timeOnNewProject, long timeOnNewSelection) {
		this.timeOnStartup = timeOnStartup;
		this.timeOnNewProject = timeOnNewProject;
		this.timeOnNewSelection = timeOnNewSelection;
	}

	public SelectionTimes withNewSelection(boolean isANewProject) {
		return new SelectionTimes(
			timeOnStartup,
			isANewProject ? System.currentTimeMillis() / 1000 : timeOnNewProject,
			System.currentTimeMillis() / 1000
		);
	}
	
	/** @return the timestamp on Eclipse startup */
	public long onStartup() {
		return timeOnStartup;
	}
	
	/* @return  */
	public long onNewProject() {
		return timeOnNewProject;
	}

	public long onSelection() {
		return timeOnNewSelection;
	}
}
