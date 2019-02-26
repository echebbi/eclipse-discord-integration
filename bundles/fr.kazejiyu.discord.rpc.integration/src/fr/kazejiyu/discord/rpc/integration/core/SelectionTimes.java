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

import java.util.Objects;

import org.eclipse.core.resources.IProject;

/**
 * The times at which selection changed. 
 */
public class SelectionTimes {

	private final long timeOnStartup;
	
	private long timeOnNewProject;
	
	private long timeOnNewSelection;
	
	private IProject lastSelectedProject = null;
	
	public SelectionTimes() {
		this.timeOnStartup = this.timeOnNewProject = this.timeOnNewSelection = System.currentTimeMillis() / 1000;
	}
	
	public SelectionTimes withNewSelectionInResourceOwnedBy(IProject project) {
		this.timeOnNewProject = isANewProject(project) ? System.currentTimeMillis() / 1000 : timeOnNewProject;
		this.timeOnNewSelection = System.currentTimeMillis() / 1000;
		this.lastSelectedProject = project;
		
		return this;
	}
	
	/** @return whether the selection associated with the presence is in a new project */
	private boolean isANewProject(IProject project) {
		return ! Objects.equals(project, lastSelectedProject);
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
