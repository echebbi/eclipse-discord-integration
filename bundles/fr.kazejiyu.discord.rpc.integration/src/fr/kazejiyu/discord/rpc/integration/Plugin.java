package fr.kazejiyu.discord.rpc.integration;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Represents Discord Rich Presence for Eclipse IDE plug-in.<br>
 * <br>
 * Provides utility methods to deal with Eclipse environment.
 * 
 * @author Emmanuel CHEBBI
 */
public class Plugin {
	
	public static final String ID = Activator.PLUGIN_ID;
	
	private Plugin() {
		// does not make sense to instantiate it
	}
	
	/**
	 * Logs a specific Exception in Eclipse <i>Error Log</i> view.<br>
	 * <br>
	 * The exception's message is used as log's status message.
	 * 
	 * @param e
	 * 			The exception to log. Must not be {@code null}.
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
	 * 			The log message.
	 * @param e
	 * 			The exception to log. Must not be {@code null}.
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
	 * 			The log message.
	 * @param e
	 * 			The exception to log. Must not be {@code null}.
	 * 
	 * @see #logException(Exception)
	 * @see #logException(String, Exception)
	 */
	public static void logExceptionWithDialog(String message, Exception e) {
		IStatus status = new Status(IStatus.ERROR, ID, message, e);
		StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
	}
}
