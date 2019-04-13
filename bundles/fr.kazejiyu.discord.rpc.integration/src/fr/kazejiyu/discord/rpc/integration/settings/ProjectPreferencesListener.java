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

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_FILE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_STARTUP;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_LANGUAGE_ICON;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_RICH_PRESENCE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_PROJECT_SETTINGS;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.fromProperty;
import static java.lang.Boolean.parseBoolean;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;

/**
 * <p>Listens for change in a specific project's preferences.</p>
 * 
 * <p>Each change event is forwarded to a one or several 
 * {@link SettingChangeListener SettingChangeListeners}.</p>
 * 
 * @author Emmanuel CHEBBI
 */
public class ProjectPreferencesListener implements IPreferenceChangeListener {
    
    private final Collection<SettingChangeListener> listeners;
    
    private final Map<String, BiConsumer<PreferenceChangeEvent, SettingChangeListener>> events = new HashMap<>();

    /**
     * Creates a new instance aimed to listen for change in global preferences.
     *  
     * @param listeners
     *             Every change event in project's preferences will be forwarded to them.
     *             Must not be {@code null}.
     */
    public ProjectPreferencesListener(Collection<SettingChangeListener> listeners) {
        this.listeners = requireNonNull(listeners, "The collection of listeners must not be null");
        
        events.put(PROJECT_NAME.property(), (event, listener) -> listener.projectNameChanged(String.valueOf(event.getOldValue()), String.valueOf(event.getNewValue())));
        events.put(SHOW_FILE_NAME.property(), (event, listener) -> listener.fileNameVisibilityChanged(parseBoolean((String) event.getNewValue())));
        events.put(SHOW_RICH_PRESENCE.property(), (event, listener) -> listener.richPresenceVisibilityChanged(parseBoolean((String) event.getNewValue())));
        events.put(SHOW_PROJECT_NAME.property(), (event, listener) -> listener.projectNameVisibilityChanged(parseBoolean((String) event.getNewValue())));
        events.put(SHOW_ELAPSED_TIME.property(), (event, listener) -> listener.elapsedTimeVisibilityChanged(parseBoolean((String) event.getNewValue())));
        events.put(SHOW_LANGUAGE_ICON.property(), (event, listener) -> listener.languageIconVisibilityChanged(parseBoolean((String) event.getNewValue())));
        events.put(RESET_ELAPSED_TIME.property(), (event, listener) -> listener.elapsedTimeResetMomentChanged(toMoment(event.getOldValue()), toMoment(event.getNewValue())));
        events.put(USE_PROJECT_SETTINGS.property(), (event, listener) -> listener.useProjectProperties(parseBoolean((String) event.getNewValue())));
    }
    
    @Override
    public void preferenceChange(PreferenceChangeEvent event) {
        BiConsumer<PreferenceChangeEvent, SettingChangeListener> callback = events.get(event.getKey());
        
        for (SettingChangeListener listener : listeners) {
            callback.accept(event, listener);
        }
    }
    
    private static Moment toMoment(Object value) {
        Settings setting = fromProperty((String) value);
        
        if (setting == RESET_ELAPSED_TIME_ON_NEW_FILE) {
            return Moment.ON_NEW_FILE;
        }
        if (setting == RESET_ELAPSED_TIME_ON_NEW_PROJECT) {
            return Moment.ON_NEW_PROJECT;
        }
        if (setting == RESET_ELAPSED_TIME_ON_STARTUP) {
            return Moment.ON_STARTUP;
        }
        return null;
    }

}
