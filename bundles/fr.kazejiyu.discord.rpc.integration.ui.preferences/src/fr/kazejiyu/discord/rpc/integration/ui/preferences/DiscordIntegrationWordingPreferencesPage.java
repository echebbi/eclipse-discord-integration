/*******************************************************************************
 * Copyright (C) 2018-2020 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.ui.preferences;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.CUSTOM_DISCORD_DETAILS_WORDING;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.CUSTOM_DISCORD_STATE_WORDING;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_CUSTOM_WORDING;
import static fr.kazejiyu.discord.rpc.integration.ui.preferences.DiscordIntegrationPreferencesPage.PREFERENCES_STORE_ID;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.LayoutConstants;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import fr.kazejiyu.discord.rpc.integration.ui.preferences.internal.CustomWordingVariables;
import fr.kazejiyu.discord.rpc.integration.ui.preferences.internal.LambdaLabelProvider;

/**
 * A page allowing users to tailor wording of text shown in Discord.
 */
public class DiscordIntegrationWordingPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {
    /**
     * The checkbox indicating whether the user wants to use a custom wording.
     */
    private Button useCustomWording;
    /**
     * The custom user wording for Discord "details" field.
     */
    private Text detailsWording;
    /**
     * The custom user wording for Discord "state" field.
     */
    private Text stateWording;
    
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, PREFERENCES_STORE_ID));
        setDescription("Customize the texts shown in Discord.");
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
        
        createWordingGroup(area);
        new Label(area, SWT.HORIZONTAL);
        createVariablesTable(area);
        
        return area;
    }
    
    @Override
    protected void performDefaults() {
        useCustomWording.setSelection(getPreferenceStore().getDefaultBoolean(USE_CUSTOM_WORDING.property()));
        detailsWording.setText(getPreferenceStore().getDefaultString(CUSTOM_DISCORD_DETAILS_WORDING.property()));
        stateWording.setText(getPreferenceStore().getDefaultString(CUSTOM_DISCORD_STATE_WORDING.property()));
        
        detailsWording.setEnabled(useCustomWording.getSelection());
        stateWording.setEnabled(useCustomWording.getSelection());
        
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(USE_CUSTOM_WORDING.property(), useCustomWording.getSelection());
        getPreferenceStore().setValue(CUSTOM_DISCORD_DETAILS_WORDING.property(), detailsWording.getText().trim());
        getPreferenceStore().setValue(CUSTOM_DISCORD_STATE_WORDING.property(), stateWording.getText().trim());
        return true;
    }

    private void createWordingGroup(Composite parent) {
        Group group = createGroup(parent, " Wording ");
        
        // "[ ] Use custom wording" checkbox
        
        useCustomWording = new Button(group, SWT.CHECK | SWT.LEFT);
        useCustomWording.setText("Use &custom wording");
        useCustomWording.setSelection(getPreferenceStore().getBoolean(USE_CUSTOM_WORDING.property()));
        useCustomWording.addSelectionListener(widgetSelectedAdapter(event -> {
            detailsWording.setEnabled(useCustomWording.getSelection());
            stateWording.setEnabled(useCustomWording.getSelection());
        }));
        GridDataFactory.fillDefaults().span(3, 1).applyTo(useCustomWording);
        
        // "Details" field
        
        Label detailsLabel = new Label(group, SWT.LEFT);
        detailsLabel.setText("Details: ");
        detailsLabel.setToolTipText("The top text");
        GridDataFactory.swtDefaults().indent(LayoutConstants.getIndent(), 0).applyTo(detailsLabel);
        
        detailsWording = new Text(group, SWT.BORDER);
        detailsWording.setEnabled(useCustomWording.getSelection());
        detailsWording.setText(getPreferenceStore().getString(CUSTOM_DISCORD_DETAILS_WORDING.property()));
        detailsWording.setToolTipText("The top text");
        GridDataFactory.createFrom(new GridData(GridData.FILL_HORIZONTAL)).span(2, 1).applyTo(detailsWording);
        
        // "State" field
        
        Label stateLabel = new Label(group, SWT.LEFT);
        stateLabel.setText("State: ");
        stateLabel.setToolTipText("The bottom text");
        GridDataFactory.swtDefaults().indent(LayoutConstants.getIndent(), 0).applyTo(stateLabel);
        
        stateWording = new Text(group, SWT.BORDER);
        stateWording.setEnabled(useCustomWording.getSelection());
        stateWording.setText(getPreferenceStore().getString(CUSTOM_DISCORD_STATE_WORDING.property()));
        stateWording.setToolTipText("The bottom text");
        GridDataFactory.createFrom(new GridData(GridData.FILL_HORIZONTAL)).span(2, 1).applyTo(stateWording);
    }
    
    private static void createVariablesTable(Composite parent) {
        new Label(parent, SWT.NONE).setText("Available variables:");
        
        TableViewer variablesTable = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        variablesTable.getTable().setHeaderVisible(true);
        variablesTable.setContentProvider(ArrayContentProvider.getInstance());
        GridDataFactory.fillDefaults()/*.indent(LayoutConstants.getIndent(), 0)*/.applyTo(variablesTable.getTable());
        
        TableViewerColumn colTemplate = new TableViewerColumn(variablesTable, SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL);
        colTemplate.getColumn().setText("Variable");
        colTemplate.setLabelProvider(new LambdaLabelProvider<>(CustomWordingVariables::template));
        
        TableViewerColumn colDescription = new TableViewerColumn(variablesTable, SWT.FILL | SWT.H_SCROLL | SWT.V_SCROLL);
        colDescription.getColumn().setText("Replaced by");
        colDescription.setLabelProvider(new LambdaLabelProvider<>(CustomWordingVariables::replacedBy));
        
        variablesTable.setInput(CustomWordingVariables.values());
        colTemplate.getColumn().pack();
        colDescription.getColumn().pack();
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
