/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.settings;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_FILE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_STARTUP;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.fromProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Listens for change in plug-in's preferences.
 * 
 * @author Emmanuel CHEBBI
 */
public class GlobalPreferencesListener implements IPropertyChangeListener {
	
	private final List<SettingChangeListener> listeners;
	
	private final Map<String, BiConsumer<PropertyChangeEvent, SettingChangeListener>> events = new HashMap<>();

	public GlobalPreferencesListener(List<SettingChangeListener> listeners) {
		this.listeners = listeners;
		
		events.put(SHOW_FILE_NAME.property(), (event, listener) -> listener.fileNameVisibilityChanged(Boolean.parseBoolean((String) event.getNewValue())));
		events.put(SHOW_PROJECT_NAME.property(), (event, listener) -> listener.projectNameVisibilityChanged(Boolean.parseBoolean((String) event.getNewValue())));
		events.put(SHOW_ELAPSED_TIME.property(), (event, listener) -> listener.elapsedTimeVisibilityChanged(Boolean.parseBoolean((String) event.getNewValue())));
		events.put(RESET_ELAPSED_TIME.property(), (event, listener) -> listener.elapsedTimeResetMomentChanged(toMoment(event.getOldValue()), toMoment(event.getNewValue())));
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		BiConsumer<PropertyChangeEvent, SettingChangeListener> callback = events.get(event.getProperty());
		
		for (SettingChangeListener listener : listeners) {
			callback.accept(event, listener);
		}
	}
	
	private Moment toMoment(Object value) {
		Settings setting = fromProperty((String) value);
		
		if (setting == RESET_ELAPSED_TIME_ON_NEW_FILE)
			return Moment.ON_NEW_FILE;
		
		if (setting == RESET_ELAPSED_TIME_ON_NEW_PROJECT)
			return Moment.ON_NEW_PROJECT;
		
		if (setting == RESET_ELAPSED_TIME_ON_STARTUP)
			return Moment.ON_STARTUP;
		
		return null;
	}

}
