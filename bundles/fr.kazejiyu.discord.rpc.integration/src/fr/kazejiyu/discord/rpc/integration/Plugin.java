/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * <p>Represents Discord Rich Presence for Eclipse IDE plug-in.</p>
 * 
 * <p>Provides utility methods to deal with Eclipse environment.</p>
 * 
 * @author Emmanuel CHEBBI
 */
public final class Plugin {
    
    public static final String ID = Activator.PLUGIN_ID;
    
    /** ID of the 'editor_input_adapter' extension point. */
    public static final String EDITOR_INPUT_ADAPTER_EXTENSION_ID = "fr.kazejiyu.discord.rpc.integration.editor_input_adapter";
    
    private Plugin() {
        // does not make sense to instantiate it
    }
    
    /**
     * Logs a specified message in Eclipse <i>Error Log</i> view.
     * 
     * @param message
     *          The message to log.
     */
    public static void log(String message) {
        IStatus status = new Status(IStatus.INFO, ID, message);
        StatusManager.getManager().handle(status);
    }
    
    /**
     * <p>Logs a specific Exception in Eclipse <i>Error Log</i> view.</p>
     * 
     * <p>The exception's message is used as log's status message.</p>
     * 
     * @param e
     *          The exception to log. Must not be {@code null}.
     * 
     * @see #logException(String, Exception)
     * @see #logExceptionWithDialog(String, Exception)
     */
    public static void logException(Exception e) {
        logException(e.getMessage(), e);
    }
    
    /**
     * Logs a specific Exception in Eclipse <i>Error Log</i> view.
     * 
     * @param message
     *          The log message.
     * @param e
     *          The exception to log. Must not be {@code null}.
     * 
     * @see #logException(String)
     * @see #logExceptionWithDialog(String, Exception)
     */
    public static void logException(String message, Exception e) {
        IStatus status = new Status(IStatus.ERROR, ID, message, e);
        StatusManager.getManager().handle(status);
    }
    
    /**
     * Logs a specific Exception in Eclipse <i>Error Log</i> view and opens a non-modal error dialog.
     * 
     * @param message
     *             The log message.
     * @param e
     *             The exception to log. Must not be {@code null}.
     * 
     * @see #logException(Exception)
     * @see #logException(String, Exception)
     */
    public static void logExceptionWithDialog(String message, Exception e) {
        IStatus status = new Status(IStatus.ERROR, ID, message, e);
        StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
    }
}
