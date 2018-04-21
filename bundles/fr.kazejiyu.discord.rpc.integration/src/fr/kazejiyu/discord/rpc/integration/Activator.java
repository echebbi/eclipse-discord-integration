package fr.kazejiyu.discord.rpc.integration;

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
	private final NotifyDiscordRpcOnSelection rpcNotifier = new NotifyDiscordRpcOnSelection();

	@Override
	public void earlyStartup() {
		final IWorkbench workbench = PlatformUI.getWorkbench();
		
		workbench.addWindowListener(new AddListenerOnWindowOpened<NotifyDiscordRpcOnSelection>(rpcNotifier));

		workbench.getDisplay()
				 .asyncExec(listenForSelectionInOpenedWindows(workbench));
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
