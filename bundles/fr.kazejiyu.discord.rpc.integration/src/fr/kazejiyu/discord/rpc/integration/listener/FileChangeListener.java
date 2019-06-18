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

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.DEFAULT_DISCORD_APPLICATION_ID;
import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

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
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.ProjectPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.SettingChangeListener;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * Listens for selected part to change.
 * <p>
 * Each time a new {@link EditorPart} is selected, a corresponding {@link RichPresence}
 * is created and then forward to a {@link DiscordRpcLifecycle} instance in order to
 * be shown in Discord's UI.
 */
public class FileChangeListener implements ISelectionListener, IPartListener2 {
    
    /** Used to update Discord when active project's preferences change. */
    private IEditorPart lastSelectedEditor = null;
    
    /** Proxy used to communicate with Discord. */
    private final DiscordRpcLifecycle discord;

    // Used to watch project's preferences
    private IProject lastSelectedProject = null;
    private ProjectPreferences lastSelectedProjectPreferences = null;
    private SettingChangeListener updateDiscordOnProjectSettingChange = null;
    
    
    private final EditionContext context;
    
    private final Function<EditionContext, Optional<RichPresence>> toRichPresence;

    /**
     * Creates a new instances able to listen for the active editor to change.
     * 
     * @param discord
     *          The proxy used to communicate with Discord.
     * @param toRichPresence
     *          The adapter used to create a RichPresence from an EditionContext.
     */
    public FileChangeListener(DiscordRpcLifecycle discord, Function<EditionContext, Optional<RichPresence>> toRichPresence) {
        this.discord = requireNonNull(discord, "The Discord proxy must not be null");
        this.context = new EditionContext();
        this.toRichPresence = requireNonNull(toRichPresence, "The RichPresence adapter must not be null");
    }

    /**
     * Returns the current editing context.
     * @return the current editing context
     */
    public EditionContext editingContext() {
        return context;
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
    @SuppressWarnings({"checkstyle:illegalcatch"})
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (!(part instanceof IEditorPart) || part.equals(lastSelectedEditor)) {
            return;
        }
        try {
            lastSelectedEditor = (IEditorPart) part;
            context.setLastSelectedEditor(lastSelectedEditor);
            
            Optional<RichPresence> presence = toRichPresence.apply(context);
            presence.ifPresent(this::updateTimesOnSelection); // must be called before discord.show()
            
            presence.ifPresent(this::connectAnotherDiscordAppIfRequired);
            presence.ifPresent(discord::show);
            
            presence.ifPresent(this::listenForChangesInProjectPreferences);
            presence.ifPresent(this::registerLastSelectedProject); // must be called after listenFor..()
        } 
        catch (Exception e) {
            // Should never happen, but provides a more appropriate error in case of failure
            Plugin.logException("An error occurred while trying to udpate Discord", e);
        }
    }

    /** Updates times so that we know the time on the last selection. */
    private void updateTimesOnSelection(RichPresence presence) {
        IProject projectOwningFocusedResource = presence.getProject().orElse(null);
        context.getElapsedTimes().updateWithNewSelectionIn(projectOwningFocusedResource);
    }
    
    private void registerLastSelectedProject(RichPresence presence) {
        lastSelectedProject = presence.getProject().orElse(null);
    }
    
    /** Creates a new listener watching for changes in the active project, if a new one has been activated. */
    private void listenForChangesInProjectPreferences(RichPresence presence) {
        boolean isAlreadyListening = Objects.equals(presence.getProject().orElse(null), lastSelectedProject);
        if (isAlreadyListening) {
            return;
        }
        if (lastSelectedProjectPreferences != null) {
            lastSelectedProjectPreferences.removeSettingChangeListener(updateDiscordOnProjectSettingChange);
        }
        presence.getProject().ifPresent(project -> {
            lastSelectedProjectPreferences = new ProjectPreferences(project);

            updateDiscordOnProjectSettingChange = new UpdateDiscordOnSettingChange(context, toRichPresence, discord, new GlobalPreferences());
            lastSelectedProjectPreferences.addSettingChangeListener(updateDiscordOnProjectSettingChange);
        });
    }
    
    // TODO [Refactor] Consider moving this method within DiscordProxy
    //                 so that it is implicitely executing when calling show().
    //                 This piece of code is likely to be duplicated
    //                 and has nothing to do with this class' concerns.
    private void connectAnotherDiscordAppIfRequired(RichPresence presence) {
        GlobalPreferences globalPreferences = new GlobalPreferences();
        UserPreferences preferences = presence.getProject()
                .map(globalPreferences::getApplicablePreferencesFor)
                .orElse(globalPreferences); // Without a project, we cannot access resource's specific preferences and hence assume that global preferences apply
        
        if (! preferences.showsRichPresence()) {
            // we should not be connected to Discord at all
            return;
        }
        String expectedDiscordAppId = preferences.usesCustomDiscordApplication() ? preferences.getDiscordApplicationId().orElse("")
                                                                                 : DEFAULT_DISCORD_APPLICATION_ID;
        if (discord.isConnectedTo(expectedDiscordAppId)) {
            // Nothing to do: we are already connected to the right Discord application
            return;
        }
        discord.shutdown();
        discord.initialize(expectedDiscordAppId);
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
            context.setLastSelectedEditor(null);
        }
    }

    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        // already handled by #partActivated
    }

    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
        // already handled by #partActivated + #partClosed
    }

    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
        // already handled by #partActivated
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
