/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.settings;

/**
 * Represents an instant in plug-in's lifecycle.
 * 
 * @author Emmanuel CHEBBI
 */
public enum Moment {

    /** Eclipse startup. */
    ON_STARTUP,
    
    /** Each time a new file is selected. */
    ON_NEW_FILE,
    
    /** Each time the current project changes. */
    ON_NEW_PROJECT;
    
}
