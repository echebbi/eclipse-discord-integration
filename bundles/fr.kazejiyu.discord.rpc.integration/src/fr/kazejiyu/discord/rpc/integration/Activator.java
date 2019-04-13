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

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_LANGUAGE_ICON;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_RICH_PRESENCE;

import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcProxy;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorRichPresenceFromInput;
import fr.kazejiyu.discord.rpc.integration.extensions.internal.EditorRichPresenceFromExtensions;
import fr.kazejiyu.discord.rpc.integration.listener.AddListenerOnWindowOpened;
import fr.kazejiyu.discord.rpc.integration.listener.FileChangeListener;
import fr.kazejiyu.discord.rpc.integration.listener.OnPostShutdown;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;

/**
 * <p>Setup the Discord Rich Presence for Eclipse IDE plug-in.</p>
 * <p> 
 *  This class, activated on Eclipse's start-up:
 *  <ul>
 *      <li>connects Eclipse IDE to any currently opened Discord session,
 *      <li>adds listener making able to notify Discord when Eclipse's current selection changes.
 *  </ul>
 * </p>
 */
public class Activator extends AbstractUIPlugin implements IStartup {

    public static final String PLUGIN_ID = "fr.kazejiyu.discord.rpc.integration"; //$NON-NLS-1$

    /** Listens current selection then notify Discord'RPC. */
    private FileChangeListener fileChangeListener;

    /** Used to communicate with Discord. */
    private DiscordRpcProxy discord;
    
    @Override
    public void earlyStartup() {
        try {
            setDefaultPreferencesValue();
            connectToDiscord();
            listenForSelectionChangesWith(fileChangeListener);
        } 
        catch (Exception e) {
            Plugin.logException("An error occurred while starting the 'Discord Rich Presence for Eclipse IDE' plug-in", e);
        }
    }

    /** Initializes the connection to Discord and shows active editor, if any. */
    private void connectToDiscord() {
        discord = new DiscordRpcProxy();
        GlobalPreferences preferences = new GlobalPreferences();
        
        if (preferences.showsRichPresence()) {
            discord.initialize();
            discord.showNothing();
        }
        EditorRichPresenceFromInput adapters = new EditorRichPresenceFromExtensions(RegistryFactory.getRegistry());
        fileChangeListener = new FileChangeListener(discord, adapters);
        fileChangeListener.notifyDiscordWithActivePart();
    }

    /** Sets up listeners so that the given listener is notified each time a new part is selected. */
    private <T extends ISelectionListener & IPartListener2> void listenForSelectionChangesWith(T listener) {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        
        workbench.addWindowListener(new AddListenerOnWindowOpened<>(listener));
        workbench.addWorkbenchListener(new OnPostShutdown(iworkbench -> discord.close()));
        workbench.getDisplay()
                 .asyncExec(listenForSelectionInOpenedWindows(workbench));
    }
    
    @Override
    public void stop(BundleContext context) throws Exception {
        try {
            discord.shutdown();
        }
        catch (Exception e) {
            Plugin.logException("An error occurred while shutting Discord Rich Presence down", e);
        }
        finally {
            super.stop(context);
        }
    }
    
    /** Sets default values for plug-in's Preferences. */
    private void setDefaultPreferencesValue() {
        getPreferenceStore().setDefault(SHOW_FILE_NAME.property(), true);
        getPreferenceStore().setDefault(SHOW_PROJECT_NAME.property(), true);
        getPreferenceStore().setDefault(SHOW_ELAPSED_TIME.property(), true);
        getPreferenceStore().setDefault(SHOW_LANGUAGE_ICON.property(), true);
        getPreferenceStore().setDefault(SHOW_RICH_PRESENCE.property(), true);
        getPreferenceStore().setDefault(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
    }
    
    /** Adds fileChangeListener as an ISelectionListener to each opened window. */
    private Runnable listenForSelectionInOpenedWindows(IWorkbench workbench) {
        return () -> {
            for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
                if (window != null) {
                    window.getSelectionService().addSelectionListener(fileChangeListener);
                    
                    for (IWorkbenchPage page : window.getPages()) {
                        page.addPartListener(fileChangeListener);
                    }
                }
            }
        };
    }
}
