/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.ui.preferences.properties;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_FILE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_STARTUP;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_LANGUAGE_ICON;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_PROJECT_SETTINGS;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

public class ProjectPropertiesPage extends PropertyPage implements IWorkbenchPropertyPage {
	
	private IProject project;
	
	private IEclipsePreferences preferences;
	
	private Button useProjectSettings;
	private Button useWorkspaceSettings;
	
	private Button showProjectName;
	private Button showFileName;
	private Button showElapsedTime;
	private Button showLanguageIcon;
	
	private Button resetOnStartup;
	private Button resetOnProjectSelection;
	private Button resetOnFileSelection;
	
	@Override
	protected Control createContents(Composite parent) {
		if (!(getElement() instanceof IProject))
			return new Composite(parent, SWT.NONE);
		
		project = (IProject) getElement();
		IScopeContext context = new ProjectScope(project);
		preferences = context.getNode("fr.kazejiyu.discord.rpc.integration");
		
		try {
			setMissingPropertiesToDefault(project);
		} catch (CoreException e) {
			e.printStackTrace();
			return new Composite(parent, SWT.NONE);
		}
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		try {
			addSettingsScopeSection(composite);
			addPrivacySection(composite);
			addSeparator(composite);
			addResetElapsedTimeSection(composite);
			
			disableUselessFields();
			
			enableFieldsOnUseProjectProperties();
			disableFieldsOnUseWorkspacePreferences();
			disableResetFieldsOnHideElapsedTime();
			
		} catch (CoreException e) { /* Should never happen */ }
		
		return composite;
	}

	private void setMissingPropertiesToDefault(IResource resource) throws CoreException {
		if (resource.getPersistentProperty(USE_PROJECT_SETTINGS.qualifiedName()) == null)
			resource.setPersistentProperty(USE_PROJECT_SETTINGS.qualifiedName(), "true");
		if (resource.getPersistentProperty(SHOW_PROJECT_NAME.qualifiedName()) == null)
			resource.setPersistentProperty(SHOW_PROJECT_NAME.qualifiedName(), "true");
		if (resource.getPersistentProperty(SHOW_FILE_NAME.qualifiedName()) == null)
			resource.setPersistentProperty(SHOW_FILE_NAME.qualifiedName(), "true");
		if (resource.getPersistentProperty(SHOW_LANGUAGE_ICON.qualifiedName()) == null)
			resource.setPersistentProperty(SHOW_LANGUAGE_ICON.qualifiedName(), "true");
		if (resource.getPersistentProperty(SHOW_ELAPSED_TIME.qualifiedName()) == null)
			resource.setPersistentProperty(SHOW_ELAPSED_TIME.qualifiedName(), "true");
		if (resource.getPersistentProperty(RESET_ELAPSED_TIME.qualifiedName()) == null)
			resource.setPersistentProperty(RESET_ELAPSED_TIME.qualifiedName(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
	}
	
	@Override
	public void performDefaults() {
		super.performDefaults();
		showProjectName.setSelection(true);
		showFileName.setSelection(true);
		showElapsedTime.setSelection(true);
		showLanguageIcon.setSelection(true);
		resetOnStartup.setSelection(false);
		resetOnProjectSelection.setSelection(true);
		resetOnFileSelection.setSelection(false);
		useWorkspaceSettings.setSelection(true);
		useProjectSettings.setSelection(false);
	}
	
	@Override
	public boolean performOk() {
		try {
			project.setPersistentProperty(USE_PROJECT_SETTINGS.qualifiedName(), useProjectSettings.getSelection() + "");
			project.setPersistentProperty(SHOW_PROJECT_NAME.qualifiedName(), showProjectName.getSelection() + "");
			project.setPersistentProperty(SHOW_FILE_NAME.qualifiedName(), showFileName.getSelection() + "");
			project.setPersistentProperty(SHOW_ELAPSED_TIME.qualifiedName(), showElapsedTime.getSelection() + "");
			project.setPersistentProperty(SHOW_LANGUAGE_ICON.qualifiedName(), showLanguageIcon.getSelection() + "");
			
			if (resetOnStartup.getSelection())
				project.setPersistentProperty(RESET_ELAPSED_TIME.qualifiedName(), RESET_ELAPSED_TIME_ON_STARTUP.property());
			if (resetOnProjectSelection.getSelection())
				project.setPersistentProperty(RESET_ELAPSED_TIME.qualifiedName(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
			if (resetOnFileSelection.getSelection())
				project.setPersistentProperty(RESET_ELAPSED_TIME.qualifiedName(), RESET_ELAPSED_TIME_ON_NEW_FILE.property());

			preferences.putBoolean(USE_PROJECT_SETTINGS.property(), useProjectSettings.getSelection());
			preferences.putBoolean(SHOW_PROJECT_NAME.property(), showProjectName.getSelection());
			preferences.putBoolean(SHOW_FILE_NAME.property(), showFileName.getSelection());
			preferences.putBoolean(SHOW_ELAPSED_TIME.property(), showElapsedTime.getSelection());
			preferences.putBoolean(SHOW_LANGUAGE_ICON.property(), showLanguageIcon.getSelection());

			if (resetOnStartup.getSelection())
				preferences.put(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_STARTUP.property());
			if (resetOnProjectSelection.getSelection())
				preferences.put(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
			if (resetOnFileSelection.getSelection())
				preferences.put(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_FILE.property());
			
			preferences.flush();
			
		} catch (CoreException | BackingStoreException e) {
			// Should never happen
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}
	
	private void addSettingsScopeSection(Composite parent) throws CoreException {
		Group resetTime = new Group(parent, SWT.NONE);
		resetTime.setText("Preferences scope:");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		resetTime.setLayout(gridLayout);
		resetTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		useWorkspaceSettings = new Button(resetTime, SWT.RADIO);
		useWorkspaceSettings.setText("Use workspace settings");
		useWorkspaceSettings.setSelection(
				! Boolean.parseBoolean(
						project.getPersistentProperty(USE_PROJECT_SETTINGS.qualifiedName())
				)
		);

		useProjectSettings = new Button(resetTime, SWT.RADIO);
		useProjectSettings.setText("Use project settings");
		useProjectSettings.setSelection(
				Boolean.parseBoolean(
						project.getPersistentProperty(USE_PROJECT_SETTINGS.qualifiedName())
				)
		);
	}

	private void addPrivacySection(Composite parent) throws CoreException {
		Group privacy = new Group(parent, SWT.NONE);
		privacy.setText("Privacy");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		privacy.setLayout(gridLayout);
		privacy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		showProjectName = new Button(privacy, SWT.CHECK);
		showProjectName.setText("Show project name");
		showProjectName.setSelection(
				Boolean.parseBoolean(
						project.getPersistentProperty(SHOW_PROJECT_NAME.qualifiedName())
				)
		);

		showFileName = new Button(privacy, SWT.CHECK);
		showFileName.setText("Show file name");
		showFileName.setSelection(
				Boolean.parseBoolean(
						project.getPersistentProperty(SHOW_FILE_NAME.qualifiedName())
				)
		);

		showElapsedTime = new Button(privacy, SWT.CHECK);
		showElapsedTime.setText("Show elapsed time");
		showElapsedTime.setSelection(
				Boolean.parseBoolean(
						project.getPersistentProperty(SHOW_ELAPSED_TIME.qualifiedName())
				)
		);

		showLanguageIcon = new Button(privacy, SWT.CHECK);
		showLanguageIcon.setText("Show language icon");
		showLanguageIcon.setSelection(
				Boolean.parseBoolean(
						project.getPersistentProperty(SHOW_LANGUAGE_ICON.qualifiedName())
				)
		);
	}
	
	private void addResetElapsedTimeSection(Composite parent) throws CoreException {
		Group resetTime = new Group(parent, SWT.NONE);
		resetTime.setText("Reset elapsed time:");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		resetTime.setLayout(gridLayout);
		resetTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		resetOnStartup = new Button(resetTime, SWT.RADIO);
		resetOnStartup.setText("On startup");

		resetOnProjectSelection = new Button(resetTime, SWT.RADIO);
		resetOnProjectSelection.setText("On project selection");

		resetOnFileSelection = new Button(resetTime, SWT.RADIO);
		resetOnFileSelection.setText("On new file");		
		
		String currentReset = project.getPersistentProperty(RESET_ELAPSED_TIME.qualifiedName());
		
		if (RESET_ELAPSED_TIME_ON_STARTUP.property().equals(currentReset))
			resetOnStartup.setSelection(true);
		if (RESET_ELAPSED_TIME_ON_NEW_PROJECT.property().equals(currentReset))
			resetOnProjectSelection.setSelection(true);
		if (RESET_ELAPSED_TIME_ON_NEW_FILE.property().equals(currentReset))
			resetOnFileSelection.setSelection(true);
	}
	
	private void enableFieldsOnUseProjectProperties() {
		useProjectSettings.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				showProjectName.setEnabled(true);
				showFileName.setEnabled(true);
				showElapsedTime.setEnabled(true);
				showLanguageIcon.setEnabled(true);
				disableResetGroupIfNeeded();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	private void disableFieldsOnUseWorkspacePreferences() {
		useWorkspaceSettings.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				showProjectName.setEnabled(false);
				showFileName.setEnabled(false);
				showElapsedTime.setEnabled(false);
				showLanguageIcon.setEnabled(false);
				resetOnStartup.setEnabled(false);
				resetOnProjectSelection.setEnabled(false);
				resetOnFileSelection.setEnabled(false);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	private void disableResetFieldsOnHideElapsedTime() {
		showElapsedTime.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				disableResetGroupIfNeeded();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}
	
	private void disableResetGroupIfNeeded() {
		if (showElapsedTime.getSelection()) {
			resetOnStartup.setEnabled(true);
			resetOnProjectSelection.setEnabled(true);
			resetOnFileSelection.setEnabled(true);
		}
		else {
			resetOnStartup.setEnabled(false);
			resetOnProjectSelection.setEnabled(false);
			resetOnFileSelection.setEnabled(false);
		}
	}
	
	/** Initialise "enabled" property of page's fields */
	private void disableUselessFields() {
		disableResetGroupIfNeeded();

		if (useWorkspaceSettings.getSelection()) {
			showProjectName.setEnabled(false);
			showFileName.setEnabled(false);
			showElapsedTime.setEnabled(false);
			showLanguageIcon.setEnabled(false);
			resetOnStartup.setEnabled(false);
			resetOnProjectSelection.setEnabled(false);
			resetOnFileSelection.setEnabled(false);
		}
	}

}
