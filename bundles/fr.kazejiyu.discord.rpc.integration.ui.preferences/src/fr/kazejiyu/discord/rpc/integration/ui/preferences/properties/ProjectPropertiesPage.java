/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.ui.preferences.properties;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_FILE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_STARTUP;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_LANGUAGE_ICON;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_PROJECT_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_PROJECT_SETTINGS;
import static java.lang.Boolean.parseBoolean;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

import fr.kazejiyu.discord.rpc.integration.Plugin;

/**
 * A page showing Discord Rich Presence related preferences.
 * 
 * @author Emmanuel CHEBBI
 */
// TODO [Refactor] Since we use IEclipsePreferences to share project properties,
//                 is using Resource.*PersistentProperties method really useful ?
public class ProjectPropertiesPage extends AbstractPropertyPage {
    
    private static final String CORE_PLUGIN_ID = "fr.kazejiyu.discord.rpc.integration";

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
    
    private Text projectName;
    
    @Override
    protected Control createContents(Composite parent) {
        if (! resolveCurrentProject()) {
            return new Composite(parent, SWT.NONE);
        }
        IScopeContext context = new ProjectScope(project);
        preferences = context.getNode(CORE_PLUGIN_ID);
        
        try {
            setMissingPropertiesToDefault(project);
        } 
        catch (CoreException e) {
            Plugin.logException("Unable to set missing preferences to default", e);
            return new Composite(parent, SWT.NONE);
        }
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        composite.setLayout(layout);
        GridData data = new GridData(GridData.FILL);
        data.grabExcessHorizontalSpace = true;
        composite.setLayoutData(data);

        try {
            addLinkToTheDocumentation(composite);
            
            addSettingsScopeSection(composite);
            addPrivacySection(composite);
            addSeparator(composite);
            addResetElapsedTimeSection(composite);
            
            disableUselessFields();
            
            enableFieldsOnUseProjectProperties();
            disableFieldsOnUseWorkspacePreferences();
            disableResetFieldsOnHideElapsedTime();
        } 
        catch (CoreException e) {
            /* Should never happen */
            Plugin.logException("Unable to create the Project page for the 'Discord Rich Presence for Eclipse IDE' plug-in", e);
        }
        
        return composite;
    }

    private static void setMissingPropertiesToDefault(IResource resource) throws CoreException {
        if (resource.getPersistentProperty(USE_PROJECT_SETTINGS.qualifiedName()) == null) {
            resource.setPersistentProperty(USE_PROJECT_SETTINGS.qualifiedName(), "false");
        }
        if (resource.getPersistentProperty(SHOW_PROJECT_NAME.qualifiedName()) == null) {
            resource.setPersistentProperty(SHOW_PROJECT_NAME.qualifiedName(), "true");
        }
        if (resource.getPersistentProperty(SHOW_FILE_NAME.qualifiedName()) == null) {
            resource.setPersistentProperty(SHOW_FILE_NAME.qualifiedName(), "true");
        }
        if (resource.getPersistentProperty(SHOW_LANGUAGE_ICON.qualifiedName()) == null) {
            resource.setPersistentProperty(SHOW_LANGUAGE_ICON.qualifiedName(), "true");
        }
        if (resource.getPersistentProperty(SHOW_ELAPSED_TIME.qualifiedName()) == null) {
            resource.setPersistentProperty(SHOW_ELAPSED_TIME.qualifiedName(), "true");
        }
        if (resource.getPersistentProperty(RESET_ELAPSED_TIME.qualifiedName()) == null) {
            resource.setPersistentProperty(RESET_ELAPSED_TIME.qualifiedName(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
        }
        if (resource.getPersistentProperty(PROJECT_NAME.qualifiedName()) == null) {
            resource.setPersistentProperty(PROJECT_NAME.qualifiedName(), "");
        }
    }
    
    @Override
    public void performDefaults() {
        super.performDefaults();
        projectName.setText("");
        showProjectName.setSelection(true);
        showFileName.setSelection(true);
        showElapsedTime.setSelection(true);
        showLanguageIcon.setSelection(true);
        resetOnStartup.setSelection(false);
        resetOnProjectSelection.setSelection(true);
        resetOnFileSelection.setSelection(false);
        useWorkspaceSettings.setSelection(true);
        useProjectSettings.setSelection(false);
        
        disableUselessFields();
    }
    
    @Override
    public boolean performOk() {
        try {
            project.setPersistentProperty(PROJECT_NAME.qualifiedName(), projectName.getText());
            project.setPersistentProperty(USE_PROJECT_SETTINGS.qualifiedName(), useProjectSettings.getSelection() + "");
            project.setPersistentProperty(SHOW_PROJECT_NAME.qualifiedName(), showProjectName.getSelection() + "");
            project.setPersistentProperty(SHOW_FILE_NAME.qualifiedName(), showFileName.getSelection() + "");
            project.setPersistentProperty(SHOW_ELAPSED_TIME.qualifiedName(), showElapsedTime.getSelection() + "");
            project.setPersistentProperty(SHOW_LANGUAGE_ICON.qualifiedName(), showLanguageIcon.getSelection() + "");
            
            if (resetOnStartup.getSelection()) {
                project.setPersistentProperty(RESET_ELAPSED_TIME.qualifiedName(), RESET_ELAPSED_TIME_ON_STARTUP.property());
            }
            if (resetOnProjectSelection.getSelection()) {
                project.setPersistentProperty(RESET_ELAPSED_TIME.qualifiedName(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
            }
            if (resetOnFileSelection.getSelection()) {
                project.setPersistentProperty(RESET_ELAPSED_TIME.qualifiedName(), RESET_ELAPSED_TIME_ON_NEW_FILE.property());
            }
            
            preferences.put(PROJECT_NAME.property(), projectName.getText());
            preferences.putBoolean(USE_PROJECT_SETTINGS.property(), useProjectSettings.getSelection());
            preferences.putBoolean(SHOW_PROJECT_NAME.property(), showProjectName.getSelection());
            preferences.putBoolean(SHOW_FILE_NAME.property(), showFileName.getSelection());
            preferences.putBoolean(SHOW_ELAPSED_TIME.property(), showElapsedTime.getSelection());
            preferences.putBoolean(SHOW_LANGUAGE_ICON.property(), showLanguageIcon.getSelection());

            if (resetOnStartup.getSelection()) {
                preferences.put(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_STARTUP.property());
            }
            if (resetOnProjectSelection.getSelection()) {
                preferences.put(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
            }
            if (resetOnFileSelection.getSelection()) {
                preferences.put(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_FILE.property());
            }
            preferences.flush();
        } 
        catch (CoreException | BackingStoreException e) {
            // Should never happen
            Plugin.logException("An unexpected error occurred while saving preferences", e);
            return false;
        }
        return true;
    }
    
    private static void addLinkToTheDocumentation(Composite parent) {
        StyledText styledText = new StyledText(parent, SWT.NONE);
        styledText.setText(" See the documentation for further details.");
        styledText.setBackground(parent.getBackground());
        
        StyleRange style = new StyleRange();
        style.underline = true;
        style.underlineStyle = SWT.UNDERLINE_LINK;
        style.background = parent.getBackground();
        style.borderColor = parent.getBackground();
        style.start = 1;
        style.length = styledText.getText().length() - 1;
        styledText.setStyleRange(style);

        styledText.addListener(SWT.MouseDown, event ->
            // Open the documentation with external browser
            Program.launch("https://discord-rich-presence-for-eclipse-ide.readthedocs.io/en/latest/")
        );
        styledText.setBottomMargin(5);
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
                ! parseBoolean(
                    project.getPersistentProperty(USE_PROJECT_SETTINGS.qualifiedName())
                )
        );

        useProjectSettings = new Button(resetTime, SWT.RADIO);
        useProjectSettings.setText("Use project settings");
        useProjectSettings.setSelection(
                parseBoolean(
                    project.getPersistentProperty(USE_PROJECT_SETTINGS.qualifiedName())
                )
        );
    }

    private void addPrivacySection(Composite parent) throws CoreException {
        
        // PROJECT NAME
        
        Group projectNameGroup = new Group(parent, SWT.NULL);
        projectNameGroup.setText("Display");
        
        final int numColumnsInLayout = 2;
        GridLayout gridLayout = new GridLayout(numColumnsInLayout, false);
        projectNameGroup.setLayout(gridLayout);
        projectNameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Label projectNameLabel = new Label(projectNameGroup, SWT.NULL);
        projectNameLabel.setText("Name displayed for project: ");
        
        projectName = new Text(projectNameGroup, SWT.SINGLE | SWT.BORDER);
        projectName.setText(project.getPersistentProperty(PROJECT_NAME.qualifiedName()));
        GridData projectNameData = new GridData();
        projectNameData.grabExcessHorizontalSpace = true;
        projectNameData.horizontalAlignment = SWT.FILL;
        projectName.setLayoutData(projectNameData);
        
        // PRIVACY GROUP
        
        Group privacy = new Group(parent, SWT.NONE);
        privacy.setText("Privacy");
        privacy.setLayout(new GridLayout());
        privacy.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));    

        showProjectName = new Button(privacy, SWT.CHECK);
        showProjectName.setText("Show project name");
        showProjectName.setSelection(
                parseBoolean(
                    project.getPersistentProperty(SHOW_PROJECT_NAME.qualifiedName())
                )
        );

        showFileName = new Button(privacy, SWT.CHECK);
        showFileName.setText("Show file name");
        showFileName.setSelection(
                parseBoolean(
                    project.getPersistentProperty(SHOW_FILE_NAME.qualifiedName())
                )
        );

        showElapsedTime = new Button(privacy, SWT.CHECK);
        showElapsedTime.setText("Show elapsed time");
        showElapsedTime.setSelection(
                parseBoolean(
                    project.getPersistentProperty(SHOW_ELAPSED_TIME.qualifiedName())
                )
        );

        showLanguageIcon = new Button(privacy, SWT.CHECK);
        showLanguageIcon.setText("Show language icon");
        showLanguageIcon.setSelection(
                parseBoolean(
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
        
        if (RESET_ELAPSED_TIME_ON_STARTUP.property().equals(currentReset)) {
            resetOnStartup.setSelection(true);
        }
        if (RESET_ELAPSED_TIME_ON_NEW_PROJECT.property().equals(currentReset)) {
            resetOnProjectSelection.setSelection(true);
        }
        if (RESET_ELAPSED_TIME_ON_NEW_FILE.property().equals(currentReset)) {
            resetOnFileSelection.setSelection(true);
        }
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
    
    /** Initialize "enabled" property of page's fields. */
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
