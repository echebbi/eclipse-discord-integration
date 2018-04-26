package fr.kazejiyu.discord.rpc.integration;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import fr.kazejiyu.discord.rpc.integration.listener.AddListenerOnWindowOpened;
import fr.kazejiyu.discord.rpc.integration.listener.NotifyDiscordRpcOnSelection;

/**
 * This class, activated on Eclipse's start-up:
 * <ul>
 * 	<li>connects Eclipse to any currently opened Discord session,</li>
 * 	<li>adds listener making able to notify Discord when Eclipse's current selection changes.</li>
 * </ul>
 */
public class Activator extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.kazejiyu.discord.rpc.integration"; //$NON-NLS-1$

	/** Listens current selection then notify Discord'RPC */
	private NotifyDiscordRpcOnSelection rpcNotifier;
	
	@Override
	public void earlyStartup() {
		setDefaultPreferencesValue();
		
		rpcNotifier = new NotifyDiscordRpcOnSelection();
		final IWorkbench workbench = PlatformUI.getWorkbench();
		
		workbench.addWindowListener(new AddListenerOnWindowOpened<NotifyDiscordRpcOnSelection>(rpcNotifier));

		workbench.getDisplay()
				 .asyncExec(listenForSelectionInOpenedWindows(workbench));
	}
	
	/** Sets default values for plug-in's Preferences */
	private void setDefaultPreferencesValue() {
		getPreferenceStore().setDefault(SHOW_FILE_NAME.property(), true);
		getPreferenceStore().setDefault(SHOW_PROJECT_NAME.property(), true);
		getPreferenceStore().setDefault(SHOW_ELAPSED_TIME.property(), true);
		getPreferenceStore().setDefault(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
	}
	
	
	/** Adds {@code rpcNotifier} as an {@code ISelectionListener} to each opened window. */
	private Runnable listenForSelectionInOpenedWindows(IWorkbench workbench) {
		return () -> {
			for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
				if (window != null) {
					window.getSelectionService().addSelectionListener(rpcNotifier);
					
					for (IWorkbenchPage page : window.getPages()) {
						page.addPartListener(rpcNotifier);
					}
				}
			}
		};
	}
}
