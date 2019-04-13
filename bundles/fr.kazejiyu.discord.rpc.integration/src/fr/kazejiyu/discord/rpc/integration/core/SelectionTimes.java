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
    
    /**
     * Creates a new object that keep tracks of selection times.
     */
    public SelectionTimes() {
        long currentTime = System.currentTimeMillis() / 1000;
        
        this.timeOnStartup = currentTime;
        this.timeOnNewProject = currentTime;
        this.timeOnNewSelection = currentTime; 
    }
    
    /**
     * Sets the time on new selection to now, and change the time on new project
     * if the given project is not the last selected project.
     * 
     * @param project
     *          The project from which a resource has been selected.
     *          
     * @return the current instance to enable method chaining
     */
    public SelectionTimes withNewSelectionInResourceOwnedBy(IProject project) {
        this.timeOnNewProject = isANewProject(project) ? System.currentTimeMillis() / 1000 : timeOnNewProject;
        this.timeOnNewSelection = System.currentTimeMillis() / 1000;
        this.lastSelectedProject = project;
        
        return this;
    }
    
    private boolean isANewProject(IProject project) {
        return ! Objects.equals(project, lastSelectedProject);
    }
    
    /**
     * Returns the timestamp corresponding to Eclipse IDE startup.
     * @return the timestamp on Eclipse startup 
     */
    public long onStartup() {
        return timeOnStartup;
    }
    
    /**
     * Returns the timestamp corresponding to the first time a resource
     * has been selected on current project.
     * 
     * @return the timestamp on the current project
     */
    public long onNewProject() {
        return timeOnNewProject;
    }

    /**
     * Returns the timestamp corresponding to the last time current resource has been selected.
     * @return the timestamp on the current resource
     */
    public long onSelection() {
        return timeOnNewSelection;
    }
}
