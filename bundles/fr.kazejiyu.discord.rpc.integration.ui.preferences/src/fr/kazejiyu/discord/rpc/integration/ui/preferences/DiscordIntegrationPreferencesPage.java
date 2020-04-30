/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.ui.preferences;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_FILE;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_NEW_PROJECT;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.RESET_ELAPSED_TIME_ON_STARTUP;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_ELAPSED_TIME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_FILE_NAME;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.*;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.SHOW_RICH_PRESENCE;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * A page allowing users to tailor Discord Rich Presence related preferences.
 * 
 * @author Emmanuel CHEBBI
 */
public class DiscordIntegrationPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {

    static final String PREFERENCES_STORE_ID = "fr.kazejiyu.discord.rpc.integration";
    /**
     * The checkbox indicating whether Rich Presence should be sent to Discord.
     */
    private Button showRichPresence;
    /**
     * The checkbox indicating whether the name of the file should be shown on Discord.
     */
    private Button showFileName;
    /**
     * The checkbox indicating whether the name of the project should be shown on Discord.
     */
    private Button showProjectName;
    /**
     * The checkbox indicating whether the elapsed time should be shown on Discord.
     */
    private Button showElapsedTime;
    /**
     * The checkbox indicating whether the language should be shown on Discord.
     */
    private Button showLanguageIcon;
    private Button resetTimeOnStartup;
    private Button resetTimeOnNewProject;
    private Button resetTimeOnNewFile;

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, PREFERENCES_STORE_ID));
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout gl = new GridLayout(1, false);
        gl.marginHeight = 0;
        gl.marginTop = 5;
        area.setLayout(gl);
        area.setBackground(parent.getBackground());
        area.setForeground(parent.getForeground());
        
        createDescription(area);
        addSeparator(area);
        createActivationToggle(area);
        createPrivacyGroup(area);
        createElapsedTimeGroup(area);
        
        // Initialize fields from preferences
        performDefaults();
        
        return area;
    }
    
    protected static void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        separator.setLayoutData(gridData);
    }
    
    @Override
    protected void performDefaults() {
        // Restore state
        
        showRichPresence.setSelection(getPreferenceStore().getBoolean(SHOW_RICH_PRESENCE.property()));
        
        showFileName.setSelection(getPreferenceStore().getBoolean(SHOW_FILE_NAME.property()));
        showProjectName.setSelection(getPreferenceStore().getBoolean(SHOW_PROJECT_NAME.property()));
        showElapsedTime.setSelection(getPreferenceStore().getBoolean(SHOW_ELAPSED_TIME.property()));
        showLanguageIcon.setSelection(getPreferenceStore().getBoolean(SHOW_LANGUAGE_ICON.property()));

        String elapsedTimeResetTrigger = getPreferenceStore().getString(RESET_ELAPSED_TIME.property());
        resetTimeOnStartup.setSelection(RESET_ELAPSED_TIME_ON_STARTUP.property().equals(elapsedTimeResetTrigger));
        resetTimeOnNewProject.setSelection(RESET_ELAPSED_TIME_ON_NEW_PROJECT.property().equals(elapsedTimeResetTrigger));
        resetTimeOnNewFile.setSelection(RESET_ELAPSED_TIME_ON_NEW_FILE.property().equals(elapsedTimeResetTrigger));
        
        updateEnabledProperty();
        
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(SHOW_RICH_PRESENCE.property(), showRichPresence.getSelection());
        
        getPreferenceStore().setValue(SHOW_FILE_NAME.property(), showFileName.getSelection());
        getPreferenceStore().setValue(SHOW_PROJECT_NAME.property(), showProjectName.getSelection());
        getPreferenceStore().setValue(SHOW_ELAPSED_TIME.property(), showElapsedTime.getSelection());
        getPreferenceStore().setValue(SHOW_LANGUAGE_ICON.property(), showLanguageIcon.getSelection());

        if (resetTimeOnStartup.getSelection()) {
            getPreferenceStore().setValue(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_STARTUP.property());
        }
        else if (resetTimeOnNewProject.getSelection()) {
            getPreferenceStore().setValue(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_PROJECT.property());
        }
        else if (resetTimeOnNewFile.getSelection()) {
            getPreferenceStore().setValue(RESET_ELAPSED_TIME.property(), RESET_ELAPSED_TIME_ON_NEW_FILE.property());
        }
        return true;
    }

    /**
     * Create a label explaining the purpose of the page and providing an
     * hyperlink to online's documentation.
     */
    @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
    private void createDescription(Composite parent) {
        String description = " Customize the way informations are shown in Discord. See the documentation for further details.";
        String link = "https://discord-rich-presence-for-eclipse-ide.readthedocs.io/en/latest/";
        int linkStartIndex = description.indexOf("See") - 1;
        int linkLength = description.length() - description.indexOf("See");
                
        StyledText styledText = new StyledText(parent, SWT.NONE);
        styledText.setText(description);
        styledText.setBackground(parent.getBackground());
        styledText.setMarginColor(parent.getBackground());

        GridDataFactory.fillDefaults().applyTo(styledText);
        styledText.setLeftMargin(0);
        
        StyleRange style = new StyleRange();
        style.underline = true;
        style.underlineStyle = SWT.UNDERLINE_LINK;
        style.start = linkStartIndex;
        style.length = linkLength;
        styledText.setStyleRange(style);
        
        styledText.addListener(SWT.MouseDown, event -> {
            int clickOffset = styledText.getCaretOffset();
            if (linkStartIndex <= clickOffset && clickOffset < linkStartIndex + linkLength) {
                // Open the documentation with external browser
                Program.launch(link);
            }
        });
        styledText.setBottomMargin(5);
        styledText.setToolTipText(link);
    }

    private void createActivationToggle(Composite parent) {
        showRichPresence = new Button(parent, SWT.CHECK);
        showRichPresence.setText("&Activate Rich Presence Integration");
        showRichPresence.setToolTipText("Check to show information on Discord, uncheck to hide");
        showRichPresence.addSelectionListener(widgetSelectedAdapter(event -> this.updateEnabledProperty()));
    }

    private void createPrivacyGroup(Composite parent) {
        GridLayout layout = new GridLayout();
        layout.marginWidth = 10;
        layout.marginHeight = 5;
        
        Group group = createGroup(parent, " Privacy ");
        group.setLayout(layout);
        GridDataFactory.fillDefaults().indent(LayoutConstants.getIndent(), 0).applyTo(group);
        
        showFileName = new Button(group, SWT.CHECK);
        showFileName.setText("Show &file name");
        showFileName.setToolTipText("Uncheck to prevent showing the name of the editor on Discord");
        
        showProjectName = new Button(group, SWT.CHECK);
        showProjectName.setText("Show &project name");
        showProjectName.setToolTipText("Uncheck to prevent showing the name of the project on Discord");
        
        showElapsedTime = new Button(group, SWT.CHECK);
        showElapsedTime.setText("Show &elapsed time");
        showElapsedTime.setToolTipText("Uncheck to prevent showing the time elapsed on Discord");
        
        showLanguageIcon = new Button(group, SWT.CHECK);
        showLanguageIcon.setText("Show &language icon");
        showLanguageIcon.setToolTipText("Uncheck to prevent showing the programming language on Discord");
    }

    private void createElapsedTimeGroup(Composite parent) {
        FillLayout layout = new FillLayout(SWT.HORIZONTAL);
        layout.marginWidth = 10;
        layout.marginHeight = 5;
        
        Group group = createGroup(parent, " &Reset elapsed time: ");
        group.setLayout(layout);
        GridDataFactory.fillDefaults().indent(LayoutConstants.getIndent(), 0).applyTo(group);
        
        resetTimeOnStartup = new Button(group, SWT.RADIO);
        resetTimeOnStartup.setText("On startup");
        resetTimeOnStartup.setToolTipText("Resets the time when Eclipse IDE starts");
        
        resetTimeOnNewProject = new Button(group, SWT.RADIO);
        resetTimeOnNewProject.setText("On new project");
        resetTimeOnNewProject.setToolTipText("Resets the time when a file from a different project is open");
        
        resetTimeOnNewFile = new Button(group, SWT.RADIO);
        resetTimeOnNewFile.setText("On new file");
        resetTimeOnNewFile.setToolTipText("Resets the time when a new file is open");
    }

    /**
     * Makes sure fields are only enabled if Show Rich Presence is true.
     */
    private void updateEnabledProperty() {
        showFileName.setEnabled(showRichPresence.getSelection());
        showProjectName.setEnabled(showRichPresence.getSelection());
        showElapsedTime.setEnabled(showRichPresence.getSelection());
        showLanguageIcon.setEnabled(showRichPresence.getSelection());
        
        resetTimeOnStartup.setEnabled(showRichPresence.getSelection());
        resetTimeOnNewProject.setEnabled(showRichPresence.getSelection());
        resetTimeOnNewFile.setEnabled(showRichPresence.getSelection());
    }
    
    private static Group createGroup(Composite parent, String title) {
        Group group = new Group(parent, SWT.LEFT);
        group.setText(title);
        
        GridLayout locationLayout = new GridLayout();
        locationLayout.numColumns = 3;
        locationLayout.marginHeight = 10;
        locationLayout.marginWidth = 10;
        group.setLayout(locationLayout);
        
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(gd);
        return group;
    }

}
