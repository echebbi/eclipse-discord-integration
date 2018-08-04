/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.listener;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import fr.kazejiyu.discord.rpc.integration.Plugin;
import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcLifecycle;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.DiscordIntegrationExtensions;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.impl.UnknownInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.ProjectPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.SettingChangeListener;

/**
 * Listens for selected part to change.
 */
public class FileChangeListener implements ISelectionListener, IPartListener2 {
	
	/** Used to update Discord when active project's preferences change */
	private IWorkbenchPart lastSelectedPart = null;
	
	/** Proxy used to communicate with Discord */
	private final DiscordRpcLifecycle discord;

	/** User's preferences */
	private final GlobalPreferences preferences = new GlobalPreferences();
	
	/** Manages extension points */
	private DiscordIntegrationExtensions extensions = new DiscordIntegrationExtensions();
	
	private IProject lastSelectedProject = null;
	private ProjectPreferences lastSelectedProjectPreferences = null;
	private SettingChangeListener lastSelectedProjectListener = null;
	
	public FileChangeListener(DiscordRpcLifecycle discord) {
		this.discord = discord;
	}

	public void notifyDiscordWithActivePart() {
		PlatformUI.getWorkbench().getDisplay().syncExec(this::findActivePart);
		updateDiscord();
	}
	
	/** Sets {@link #lastSelectedPart} to workbench's active editor, if any.
	 * 	Helps to select automatically the active part on IDE startup. */
	private void findActivePart() {
		try {
			lastSelectedPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		} catch (NullPointerException e) {
			// no active editor
		}
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(part instanceof EditorPart) || part.equals(lastSelectedPart))
			return;
		
		lastSelectedPart = part;
		updateDiscord();
	}
		
	public void updateDiscord() {
		try {
			if (lastSelectedPart == null)
				return;
			
			EditorPart editor = (EditorPart) lastSelectedPart;
			
			EditorInputRichPresence adapter = extensions.findAdapterFor(editor.getEditorInput())
														.orElseGet(defaultAdapter());
			
			Optional<RichPresence> presence = adapter.createRichPresence(preferences, editor.getEditorInput());
			presence.ifPresent(discord::show);
			presence.ifPresent(this::listenForChangesInProjectPreferences);
			presence.ifPresent(this::updateLastSelectedProject);
			
		} catch (Exception e) {
			Plugin.logException("An error occured while trying to udpate Discord", e);
		}
	}

	/** @return a built-in adapter that sends nothing to Discord */
	private Supplier<EditorInputRichPresence> defaultAdapter() {
		return UnknownInputRichPresence::new;
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		// Helps to select automatically a new part when the last one has been closed
		selectionChanged(partRef.getPart(false), null);
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		if (Objects.equals(partRef.getPart(false), lastSelectedPart)) {
			discord.showNothing();
		}
	}
	
	private void updateLastSelectedProject(RichPresence presence) {
		lastSelectedProject = presence.getProject().orElse(null);
	}
	
	/** Creates a new listener watching for changes in the active project, if a new one has been activated */
	private void listenForChangesInProjectPreferences(RichPresence presence) {
		boolean alreadyListening = Objects.equals(presence.getProject().orElse(null), lastSelectedProject);
		if (alreadyListening)
			return;
		
		if (lastSelectedProjectPreferences != null)
			lastSelectedProjectPreferences.removeSettingChangeListener(lastSelectedProjectListener);
		
		presence.getProject().ifPresent(project -> {
			lastSelectedProjectPreferences = new ProjectPreferences(project);
			lastSelectedProjectListener = new RunOnSettingChange(this::updateDiscord);
			lastSelectedProjectPreferences.addSettingChangeListener(lastSelectedProjectListener);
		});
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
