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
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import fr.kazejiyu.discord.rpc.integration.core.DiscordRpcProxy;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.DiscordIntegrationExtensions;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.impl.UnknownInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.DiscordIntegrationPreferences;

/**
 * Notifies {@link DiscordRpcProxy} each time Eclipse's current selection changes.
 * 
 * @author Emmanuel CHEBBI
 */
public class NotifyDiscordRpcOnSelection implements ISelectionListener, IPartListener2 {
	
	/** Communicates informations to Discord */
	private final DiscordRpcProxy discord = new DiscordRpcProxy();
	
	IWorkbenchPart lastSelectedPart = null;
	
	private IProject lastSelectedProject = null;
	
	private long timeOnNewProject = 0;
	
	private final long timeOnStartup = System.currentTimeMillis() / 1000;
	
	private long timeOnSelection = -1;
	
	/** Manages extension points */
	private DiscordIntegrationExtensions extensions = new DiscordIntegrationExtensions();

	/** User's preferences */
	private final DiscordIntegrationPreferences preferences = new DiscordIntegrationPreferences();
	
	public NotifyDiscordRpcOnSelection() {
		discord.initialize();
		
		PlatformUI.getWorkbench().getDisplay().syncExec(this::findActivePart);
		preferences.addSettingChangeListener(new RunOnSettingChange(this::updateDiscord));
		
		if (lastSelectedPart == null)
			showNoActivity();
		else
			updateDiscord();
	}
	
	/** Sets {@link #lastSelectedPart} to workbench's active editor, if any */
	private void findActivePart() {
		try {
			lastSelectedPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		} catch (NullPointerException e) {
			// no active editor
		}
	}
	
	/** Shows nothing except "Playing Eclipse IDE" on Discord */
	private void showNoActivity() {
		// no activity == nothing to show on Discord
		discord.show(new RichPresence());
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(part instanceof EditorPart) || part.equals(lastSelectedPart))
			return;
		
		timeOnSelection = System.currentTimeMillis() / 1000;
		lastSelectedPart = part;
		
		updateDiscord();
	}

	/** Shows information corresponding to the last selected part in Discord */
	void updateDiscord() {
		if (lastSelectedPart == null)
			return;
		
		EditorPart editor = (EditorPart) lastSelectedPart;
		
		Optional<EditorInputRichPresence> maybeUserAdapter = extensions.findAdapterFor(editor.getEditorInput());
		
		EditorInputRichPresence adapter = maybeUserAdapter.orElseGet(defaultAdapterFor(editor.getEditorInput()));
		
		Optional<RichPresence> maybePresence = adapter.createRichPresence(preferences, editor.getEditorInput());
		maybePresence.map(withStartTimeStamp())
					 .ifPresent(discord::show);
	}
	
	/** @return a built-in adapter handling {@code input} */
	private Supplier<EditorInputRichPresence> defaultAdapterFor(IEditorInput input) {
		return UnknownInputRichPresence::new;
	}
	
	/** Enriches a {@code RichPresence} with the appropriate start timestamp. */
	private Function<RichPresence, RichPresence> withStartTimeStamp() {
		return presence -> {
			if (! preferences.showsElapsedTime())
				return presence;
			
			if (preferences.resetsElapsedTimeOnNewFile())
				return presence.withStartTimestamp(timeOnSelection);
			
			if (preferences.resetsElapsedTimeOnNewProject()) {
				if (! Objects.equals(presence.getProject().orElse(null), lastSelectedProject)) {
					timeOnNewProject = System.currentTimeMillis() / 1000;
					lastSelectedProject = presence.getProject().orElse(null);
				}
				return presence.withStartTimestamp(timeOnNewProject);
			}
			
			// last possible case: the time starts on startup
			return presence.withStartTimestamp(timeOnStartup);
		};
	}
	
	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		if (Objects.equals(partRef.getPart(false), lastSelectedPart)) {
			showNoActivity();
		}
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		// Helps to select automatically a new part when the last one has been closed
		selectionChanged(partRef.getPart(false), null);
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
