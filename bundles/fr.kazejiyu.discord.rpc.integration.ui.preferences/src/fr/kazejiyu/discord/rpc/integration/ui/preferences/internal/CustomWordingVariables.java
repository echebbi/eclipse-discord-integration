/*******************************************************************************
 * Copyright (C) 2018-2020 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.ui.preferences.internal;

/**
 * The variables that can be used by the user when defining a custom wording.
 */
public enum CustomWordingVariables {
    
    /**
     * A variable replaced by the name of the file with its extension.
     */
    FILE_NAME("${file}", "the name of the file with its extension"),
    
    /**
     * A variable replaced by the name of the file without its extension.
     */
    FILE_WITHOUT_EXTENSION("${file.baseName}", "the name of the file without its extension"),
    
    /**
     * A variable replaced by the extension of the file.
     */
    FILE_EXTENSION("${file.extension}", "the extension of the file"),
    
    /**
     * A variable replaced by the name of the programming language.
     */
    LANGUAGE("${language}", "the name of the programming language"),
    
    /**
     * A variable replaced by the name of the project.
     */
    PROJECT("${project}", "the name of the project");
    
    private final String template;
    
    private final String replacedBy;
    
    private CustomWordingVariables(String template, String replacedBy) {
        this.template = template;
        this.replacedBy = replacedBy;
    }
    
    /**
     * Returns the template associated with the variable.
     * @return the template associated with the variable
     */
    public String template() {
        return template;
    }
    
    /**
     * Returns a description of the value by which the variable is replaced.
     * @return a description of the value by which the variable is replaced.
     */
    public String replacedBy() {
        return replacedBy;
    }

}
