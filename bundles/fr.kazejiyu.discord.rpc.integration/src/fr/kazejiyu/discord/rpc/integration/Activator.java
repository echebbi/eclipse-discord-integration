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

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.DEFAULT_DISCORD_APPLICATION_ID;

import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.preferences.InstanceScope;
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
import fr.kazejiyu.discord.rpc.integration.files.AddListenerOnWindowOpened;
import fr.kazejiyu.discord.rpc.integration.files.EditionContext;
import fr.kazejiyu.discord.rpc.integration.files.EditorToRichPresenceAdapter;
import fr.kazejiyu.discord.rpc.integration.files.OnPostShutdown;
import fr.kazejiyu.discord.rpc.integration.files.UpdateDiscordOnEditorChange;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.UpdateDiscordOnSettingChange;

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
// Ignore the DAC here because the Activator is the application's entry and thus has a lot to set up.
// Moreover, I believe that even if a lot of classes are involved the code remain pretty understandable.
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class Activator extends AbstractUIPlugin implements IStartup {

    public static final String PLUGIN_ID = "fr.kazejiyu.discord.rpc.integration"; //$NON-NLS-1$

    /** Used to communicate with Discord. */
    private DiscordRpcProxy discord;
    
    private UpdateDiscordOnEditorChange editorChangeListener;

    private GlobalPreferences preferences = new GlobalPreferences();
    
    @Override
    @SuppressWarnings({"checkstyle:illegalcatch"})
    public void earlyStartup() {
        try {
            // Caution: following methods have side effects which impose a precise call-order, do not change it!
            //          (mutability have been chosen over purity because it makes this method easier to read)
            
            connectToDiscord();
            listenForSelectionChanges();
            listenForGlobalSettingChanges();
            showActivePartInDiscord();
        } 
        catch (Exception e) {
            // 'Exception' is caught on purpose in order to handle any unexpected error properly
            Plugin.logException("An error occurred while starting the 'Discord Rich Presence for Eclipse IDE' plug-in", e);
        }
    }

    /** Initializes the connection to Discord and shows nothing. */
    private void connectToDiscord() {
        discord = new DiscordRpcProxy();
        
        // Do not initialize any connection if the user does not want to
        if (! preferences.showsRichPresence()) {
            return;
        }
        
        // Else connect: 
        //     - either to the Discord application specified by the user in its instance-scope preferences
        //     - or, if none is specified, to the default Discord application I created for this project.
        
        String applicationId;
        
        if (preferences.usesCustomDiscordApplication()) {
            applicationId = preferences.getDiscordApplicationId().orElse("");
        }
        else {
            applicationId = DEFAULT_DISCORD_APPLICATION_ID;
        }
        discord.initialize(applicationId);
        discord.showNothing();
    }
    
    /**
     * Sets up a listener that will:
     *      - be notified each time a global preference (see {@link InstanceScope#INSTANCE}) is modified,
     *      - update Discord to show information according to the new preferences.
     */
    private void listenForGlobalSettingChanges() {
        EditorRichPresenceFromInput adapters = new EditorRichPresenceFromExtensions(RegistryFactory.getRegistry());
        EditorToRichPresenceAdapter editingContextToRichPresenceAdapter = new EditorToRichPresenceAdapter(preferences, adapters);
        EditionContext editingContext = editorChangeListener.editingContext();
        
        UpdateDiscordOnSettingChange updateDiscord = new UpdateDiscordOnSettingChange(editingContext, editingContextToRichPresenceAdapter, discord, preferences);
        preferences.addSettingChangeListener(updateDiscord);
    }
    
    /** 
     * Sets up a listener that will:
     *      - be notified each time a new editor is focused,
     *      - update Discord to show information relevant to the new editor.
     *      
     * Moreover, ensures that:
     *      - Discord is still updated when editors are opened on new windows,
     *      - the connection with Discord is closed when the workbench is closed. 
     */
    private void listenForSelectionChanges() {
        EditorRichPresenceFromInput adapters = new EditorRichPresenceFromExtensions(RegistryFactory.getRegistry());
        EditorToRichPresenceAdapter editingContextToRichPresenceAdapter = new EditorToRichPresenceAdapter(preferences, adapters);
        
        editorChangeListener = new UpdateDiscordOnEditorChange(discord, editingContextToRichPresenceAdapter);
        final IWorkbench workbench = PlatformUI.getWorkbench();
        
        workbench.addWindowListener(new AddListenerOnWindowOpened<>(editorChangeListener));
        workbench.addWorkbenchListener(new OnPostShutdown(iworkbench -> discord.close()));
        workbench.getDisplay()
                 .asyncExec(listenForSelectionInOpenedWindows(workbench, editorChangeListener));
    }
    
    private void showActivePartInDiscord() {
        editorChangeListener.notifyDiscordWithActivePart();
    }
    
    @Override
    @SuppressWarnings({"checkstyle:illegalcatch"})
    public void stop(BundleContext context) throws Exception {
        try {
            discord.shutdown();
        }
        catch (Exception e) {
            // 'Exception' is caught on purpose in order to handle any unexpected error properly
            Plugin.logException("An error occurred while shutting Discord Rich Presence down", e);
        }
        finally {
            super.stop(context);
        }
    }
    
    /** Adds fileChangeListener as an ISelectionListener to each opened window. */
    private static Runnable listenForSelectionInOpenedWindows(IWorkbench workbench, UpdateDiscordOnEditorChange editorChangeListener) {
        return () -> {
            for (IWorkbenchWindow window : workbench.getWorkbenchWindows()) {
                if (window != null) {
                    window.getSelectionService().addSelectionListener(editorChangeListener);
                    
                    for (IWorkbenchPage page : window.getPages()) {
                        page.addPartListener(editorChangeListener);
                    }
                }
            }
        };
    }
}
