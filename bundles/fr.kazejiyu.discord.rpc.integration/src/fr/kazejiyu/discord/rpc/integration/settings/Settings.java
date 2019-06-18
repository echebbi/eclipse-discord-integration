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
    
    /** Whether the name of the file being edited should be shown in Discord. */
    SHOW_FILE_NAME("SHOW_FILE_NAME"),
    
    /** Whether the name of the project being worked on should be shown in Discord. */
    SHOW_PROJECT_NAME("SHOW_PROJECT_NAME"),
    
    /** Whether the time elapsed since starting to work should be shown in Discord. */
    SHOW_ELAPSED_TIME("SHOW_ELAPSED_TIME"),
    
    /** 
     * The reset elapsed time policy. 
     * <p>
     * Should be one of:
     * <ul>
     *  <li>{@link #RESET_ELAPSED_TIME_ON_NEW_FILE}
     *  <li>{@link #RESET_ELAPSED_TIME_ON_NEW_PROJECT}
     *  <li>{@link #RESET_ELAPSED_TIME_ON_STARTUP}
     * </ul>
     */
    RESET_ELAPSED_TIME("RESET_ELAPSED_TIME"),
    
    /** Indicates that the elapsed time should only be reset when the software is started. */
    RESET_ELAPSED_TIME_ON_STARTUP("RESET_ELAPSED_TIME_ON_STARTUP"),

    /** Indicates that the elapsed time should be reset each time an editor opens for a file located in another project. */
    RESET_ELAPSED_TIME_ON_NEW_PROJECT("RESET_ELAPSED_TIME_ON_NEW_PROJECT"),
    
    /** Indicates that the elapsed time should be reset each time a new editor is focused. */
    RESET_ELAPSED_TIME_ON_NEW_FILE("RESET_ELAPSED_TIME_ON_NEW_FILE"),
    
    /** Whether the icon representing the language of the file being edited should be shown in Discord. */
    SHOW_LANGUAGE_ICON("SHOW_LANGUAGE_ICON"),
    
    /** Whether the project uses project-scope settings or global-scope settings. */
    USE_PROJECT_SETTINGS("USE_PROJECT_SETTINGS"),
    
    /** 
     * Whether any information should be shown in Discord. 
     * <p>
     * False means that the connection with Discord should not be set at all. */
    SHOW_RICH_PRESENCE("SHOW_RICH_PRESENCE"),
    
    /** Indicates a nickname that should be shown in Discord instead of the project's name. */
    PROJECT_NAME("PROJECT_NAME"),
    
    /** Whether a custom Discord Application should be used instead of the default one. */
    USE_CUSTOM_APP("USE_CUSTOM_APP"), 
    
    /** Indicates the ID of the custom Discord Application ID to use. */
    CUSTOM_APP_ID("CUSTOM_APP_ID");
    
    /** Identifies the Discord application to which information has to be sent
     *  in order to appear in Discord's UI. */
    public static final String DEFAULT_DISCORD_APPLICATION_ID = "413038514616139786";
    
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
