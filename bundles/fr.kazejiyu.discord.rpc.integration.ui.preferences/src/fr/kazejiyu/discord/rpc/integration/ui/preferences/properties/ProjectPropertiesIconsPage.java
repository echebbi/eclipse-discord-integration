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

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.CUSTOM_APP_ID;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_CUSTOM_APP;
import static java.lang.Boolean.parseBoolean;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

import fr.kazejiyu.discord.rpc.integration.Activator;
import fr.kazejiyu.discord.rpc.integration.Plugin;

/**
 * A page allowing the user to customize the icons shown in Discord.
 * 
 * @author Emmanuel CHEBBI
 */
public class ProjectPropertiesIconsPage extends AbstractPropertyPage {
    /**
     * Used to get/set Eclipse preferences.
     */
    private IEclipsePreferences preferences;
    /**
     * The text field used to type the ID of the Discord application to use.
     */
    private Text applicationIdTxtField;
    /**
     * The check box used to chose whether another Discord application should be used instead of the default one.
     */
    private Button useCustomAppCheckBox;

    @Override
    protected Control createContents(Composite parent) {
        if (! resolveCurrentProject()) {
            return new Composite(parent, SWT.NONE);
        }
        IScopeContext context = new ProjectScope(project);
        preferences = context.getNode(Activator.PLUGIN_ID);
        
        Composite composite = new Composite(parent, SWT.NONE);
        final int numColumnsInLayout = 2;
        final boolean makeColumnsEqualWidth = false;
        GridLayout layout = new GridLayout(numColumnsInLayout, makeColumnsEqualWidth);
        layout.numColumns = 2;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        try {
            addDescriptionSection(composite);
            addCustomApplicationSection(composite);
            
            disableUselessFields();
        } 
        catch (CoreException e) {
            /* Should never happen */
            Plugin.logException("Unable to create the Icons Project page for the 'Discord Rich Presence for Eclipse IDE' plug-in", e);
        }
        return composite;
    }

    private static void addDescriptionSection(Composite parent) {
        Label description = new Label(parent, SWT.WRAP);
        description.setText("Customize icons shown in Discord by using your own Discord application (https://discordapp.com/developers):");
        GridData descriptionLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        descriptionLayoutData.grabExcessHorizontalSpace = true;
        descriptionLayoutData.horizontalSpan = 2;
        descriptionLayoutData.horizontalAlignment = SWT.FILL;
        description.setLayoutData(descriptionLayoutData);
    }

    private void addCustomApplicationSection(Composite parent) throws CoreException {
        useCustomAppCheckBox = new Button(parent, SWT.CHECK);
        useCustomAppCheckBox.setText("Use a custom application");
        GridData customAppCheckBoxLayoutData = new GridData();
        customAppCheckBoxLayoutData.grabExcessHorizontalSpace = true;
        customAppCheckBoxLayoutData.horizontalSpan = 2;
        customAppCheckBoxLayoutData.horizontalAlignment = SWT.FILL;
        useCustomAppCheckBox.setLayoutData(customAppCheckBoxLayoutData);
        useCustomAppCheckBox.setSelection(
                parseBoolean(
                    project.getPersistentProperty(USE_CUSTOM_APP.qualifiedName())
                )
        );
        useCustomAppCheckBox.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                disableUselessFields();
                updateApplyButton();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        
        String userDiscordApplicationId = project.getPersistentProperty(CUSTOM_APP_ID.qualifiedName());
        if (userDiscordApplicationId == null) {
            userDiscordApplicationId = "";
        }
        Label applicationIdLabel = new Label(parent, SWT.NULL);
        applicationIdLabel.setText("ID of the Discord application to use: ");
        
        applicationIdTxtField = new Text(parent, SWT.SINGLE | SWT.BORDER);
        applicationIdTxtField.setText(userDiscordApplicationId);
        GridData projectNameData = new GridData();
        projectNameData.grabExcessHorizontalSpace = true;
        projectNameData.horizontalAlignment = SWT.FILL;
        applicationIdTxtField.setLayoutData(projectNameData);
        applicationIdTxtField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateApplyButton();
            }
        });
    }

    @Override
    public boolean isValid() {
        if (! useCustomAppCheckBox.getSelection()) {
            return true;
        }
        return !applicationIdTxtField.getText().trim().isEmpty();
    }
    
    @Override
    public boolean performOk() {
        try {
            project.setPersistentProperty(USE_CUSTOM_APP.qualifiedName(), useCustomAppCheckBox.getSelection() + "");
            project.setPersistentProperty(CUSTOM_APP_ID.qualifiedName(), applicationIdTxtField.getText());
            
            preferences.putBoolean(USE_CUSTOM_APP.property(), useCustomAppCheckBox.getSelection());
            preferences.put(CUSTOM_APP_ID.property(), applicationIdTxtField.getText());
            preferences.flush();
        }
        catch (CoreException | BackingStoreException e) {
            // Should never happen
            Plugin.logException("An unexpected error occurred while saving preferences", e);
            return false;
        }
        return true;
    }
    
    @Override
    public void performDefaults() {
        super.performDefaults();
        useCustomAppCheckBox.setSelection(false);
        applicationIdTxtField.setText("");
        
        disableUselessFields();
    }
    
    private void disableUselessFields() {
        applicationIdTxtField.setEnabled(useCustomAppCheckBox.getSelection());
    }

}
