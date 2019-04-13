/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.listener;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import fr.kazejiyu.discord.rpc.integration.Plugin;
import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcLifecycle;
import fr.kazejiyu.discord.rpc.integration.core.PreferredRichPresence;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.core.SelectionTimes;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorRichPresenceFromInput;
import fr.kazejiyu.discord.rpc.integration.extensions.internal.UnknownInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.ProjectPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.SettingChangeListener;

/**
 * <p>Listens for selected part to change.</p>
 * 
 * <p>Each a new {@link EditorPart} is selected, a corresponding {@link RichPresence}
 * is created and then forward to a {@link DiscordRpcLifecycle} instance in order to
 * be shown in Discord's UI.</p>
 */
public class FileChangeListener implements ISelectionListener, IPartListener2 {
    
    /** Used to update Discord when active project's preferences change. */
    private IEditorPart lastSelectedEditor = null;
    
    /** Proxy used to communicate with Discord. */
    private final DiscordRpcLifecycle discord;

    /** User's preferences. */
    private final GlobalPreferences preferences = new GlobalPreferences();
    
    /** Used to to create a RichPresence from the selected editor. */
    private final EditorRichPresenceFromInput adapters;
    
    // Used to watch project's preferences
    private IProject lastSelectedProject = null;
    private ProjectPreferences lastSelectedProjectPreferences = null;
    private SettingChangeListener updateDiscordOnSettingChange = null;
    
    /** Provides access to the different timestamps. */
    private SelectionTimes times = new SelectionTimes();

    /**
     * Creates a new instances able to listen for the active editor to change.
     * 
     * @param discord
     *          The proxy used to communicate with Discord.
     * @param adapters
     *          Will be notified with a new {@link RichPresence} instance each time
     *          the active editor changes. Must not be null.
     *              
     */
    public FileChangeListener(DiscordRpcLifecycle discord, EditorRichPresenceFromInput adapters) {
        this.discord = requireNonNull(discord, "The Discord proxy must not be null");
        this.adapters = requireNonNull(adapters, "The Discord extensions must not be null");
        this.preferences.addSettingChangeListener(new RunOnSettingChange(discord, this::updateDiscord));
        this.preferences.addSettingChangeListener(new SynchronizeConnection(discord, this::updateDiscord));
    }

    /**
     * Searches for the current active part then updates Discord with the corresponding presence. 
     */
    public void notifyDiscordWithActivePart() {
        PlatformUI.getWorkbench().getDisplay().syncExec(this::findActivePart);
        
        // a little trick required because activePart cannot be directly assigned
        // from within the lambda passed to Display.syncExec()
        IEditorPart activePart = lastSelectedEditor;
        lastSelectedEditor = null;
        
        selectionChanged(activePart, null);
    }
    
    /** Sets {@link #lastSelectedEditor} to workbench's active editor, if any.
     *     Helps to select automatically the active part on IDE startup. */
    private void findActivePart() {
        try {
            lastSelectedEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        } 
        catch (NullPointerException e) {
            // no active editor
        }
    }

    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (!(part instanceof IEditorPart) || part.equals(lastSelectedEditor)) {
            return;
        }
        try {
            lastSelectedEditor = (IEditorPart) part;
            
            Optional<RichPresence> presence = createRichPresenceFrom(lastSelectedEditor);
            presence.ifPresent(this::updateTimesOnSelection); // must be called before discord.show()
            
            presence.map(this::tailorToPreferences)
                    .ifPresent(discord::show);
            
            presence.ifPresent(this::listenForChangesInProjectPreferences);
            presence.ifPresent(this::registerLastSelectedProject); // must be called after listenFor..()
        } 
        catch (Exception e) {
            // Should never happen, but provides a more appropriate error in case of failure
            Plugin.logException("An error occurred while trying to udpate Discord", e);
        }
    }
    
    /** Updates Discord by showing a RichPresence corresponding to the last selected editor. */
    private void updateDiscord() {
        try {
            if (lastSelectedEditor != null) {
                createRichPresenceFrom(lastSelectedEditor)
                    .map(this::tailorToPreferences)
                    .ifPresent(discord::show);
            }
        } 
        catch (Exception e) {
            // Should never happen, but provides a more appropriate error in case of failure
            Plugin.logException("An error occured while trying to udpate Discord", e);
        }
    }
    
    /** 
     * Creates a new RichPresence for the given editor part.
     * 
     * @param editor 
     *             The editor from which a RichPresence has to be created. Must not be null.
     *  
     * @throws Exception if the adapter used to create the new instance does not follow
     *                    the contract specified by {@link EditorInputRichPresence}.
     */
    private Optional<RichPresence> createRichPresenceFrom(IEditorPart editor) {
        return adapters.findAdapterFor(editor.getEditorInput())
                       .orElseGet(defaultAdapter())
                       .createRichPresence(preferences, editor.getEditorInput());
    }

    /** Returns a built-in adapter that sends nothing to Discord. */
    private static Supplier<EditorInputRichPresence> defaultAdapter() {
        return UnknownInputRichPresence::new;
    }
    
    private void registerLastSelectedProject(RichPresence presence) {
        lastSelectedProject = presence.getProject().orElse(null);
    }
    
    private RichPresence tailorToPreferences(RichPresence presence) {
        return new PreferredRichPresence(
            preferences.getApplicablePreferencesFor(presence.getProject().orElse(null)), 
            presence, 
            times
        );
    }

    /** Updates times so that we know the time on the last selection. */
    private void updateTimesOnSelection(RichPresence presence) {
        times = times.withNewSelectionInResourceOwnedBy(presence.getProject().orElse(null));
    }
    
    /** Creates a new listener watching for changes in the active project, if a new one has been activated. */
    private void listenForChangesInProjectPreferences(RichPresence presence) {
        boolean isAlreadyListening = Objects.equals(presence.getProject().orElse(null), lastSelectedProject);
        if (isAlreadyListening) {
            return;
        }
        if (lastSelectedProjectPreferences != null) {
            lastSelectedProjectPreferences.removeSettingChangeListener(updateDiscordOnSettingChange);
        }
        presence.getProject().ifPresent(project -> {
            lastSelectedProjectPreferences = new ProjectPreferences(project);

            updateDiscordOnSettingChange = new RunOnSettingChange(discord, this::updateDiscord);
            lastSelectedProjectPreferences.addSettingChangeListener(updateDiscordOnSettingChange);
        });
    }
    
    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        // Helps to select automatically a new part when the last one has been closed
        selectionChanged(partRef.getPart(false), null);
    }
    
    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        if (Objects.equals(partRef.getPart(false), lastSelectedEditor)) {
            discord.showNothing();
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        // irrelevant event
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
        // irrelevant event
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
        // irrelevant event
    }

    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
        // irrelevant event
    }

    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
        // irrelevant event
    }

    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
        // irrelevant event
    }

}
