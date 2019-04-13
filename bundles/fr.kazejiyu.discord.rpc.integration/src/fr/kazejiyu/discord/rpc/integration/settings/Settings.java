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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.QualifiedName;

import fr.kazejiyu.discord.rpc.integration.Activator;

/**
 * Constants used to identify the different available settings.
 * 
 * @author Emmanuel CHEBBI
 */
// TODO [Refactor] Add a field specifying setting's default value
public enum Settings {
    
    SHOW_FILE_NAME("SHOW_FILE_NAME"),
    
    SHOW_PROJECT_NAME("SHOW_PROJECT_NAME"),
    
    SHOW_ELAPSED_TIME("SHOW_ELAPSED_TIME"),
    
    RESET_ELAPSED_TIME("RESET_ELAPSED_TIME"),
    
    RESET_ELAPSED_TIME_ON_STARTUP("RESET_ELAPSED_TIME_ON_STARTUP"),

    RESET_ELAPSED_TIME_ON_NEW_PROJECT("RESET_ELAPSED_TIME_ON_NEW_PROJECT"),
    
    RESET_ELAPSED_TIME_ON_NEW_FILE("RESET_ELAPSED_TIME_ON_NEW_FILE"),
    
    SHOW_LANGUAGE_ICON("SHOW_LANGUAGE_ICON"),
    
    USE_PROJECT_SETTINGS("USE_PROJECT_SETTINGS"),
    
    SHOW_RICH_PRESENCE("SHOW_RICH_PRESENCE"),
    
    PROJECT_NAME("PROJECT_NAME");
    
    private static final Map<String, Settings> propertyToSetting = new HashMap<>();

    static {
        for (Settings setting : Settings.values()) {
            propertyToSetting.put(setting.property, setting);
        }
    }
    
    /** A key identifying the setting. */
    private final String property;
    
    private Settings(String property) {
        this.property = property;
    }
    
    /**
     * Returns a unique string identifying the setting.
     * @return a unique string identifying the setting
     */
    public String property() {
        return property;
    }
    
    /**
     * Returns a unique qualified name for this setting.
     * @return a unique qualified name for this setting
     */
    public QualifiedName qualifiedName() {
        return new QualifiedName(Activator.PLUGIN_ID, property());
    }
    
    /**
     * Returns the setting identified by the given property.
     * 
     * @param property
     *          The property to turn into a setting.
     * 
     * @return the setting corresponding to the given key, or null if no one match 
     */
    public static Settings fromProperty(String property) {
        return propertyToSetting.get(property);
    }

}
