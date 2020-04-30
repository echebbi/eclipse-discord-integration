/*******************************************************************************
 * Copyright (C) 2018-2020 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.ui.preferences.properties;

import static fr.kazejiyu.discord.rpc.integration.settings.Settings.CUSTOM_DISCORD_DETAILS_WORDING;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.CUSTOM_DISCORD_STATE_WORDING;
import static fr.kazejiyu.discord.rpc.integration.settings.Settings.USE_CUSTOM_WORDING;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.LayoutConstants;
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
import org.osgi.service.prefs.BackingStoreException;

import fr.kazejiyu.discord.rpc.integration.Activator;
import fr.kazejiyu.discord.rpc.integration.Plugin;
import fr.kazejiyu.discord.rpc.integration.ui.preferences.internal.CustomWordingVariables;
import fr.kazejiyu.discord.rpc.integration.ui.preferences.internal.LambdaLabelProvider;

/**
 * A page allowing the user to customize the wording of texts shown in Discord.
 * 
 * @author Emmanuel CHEBBI
 */
public class ProjectPropertiesWordingPage extends AbstractPropertyPage {
    
    private static final boolean USES_CUSTOM_WORDING_BY_DEFAULT = false;
    private static final String DEFAULT_CUSTOM_DETAILS_WORDING = "Editing ${file}";
    private static final String DEFAULT_CUSTOM_STATE_WORDING = "Working on ${project}";
    
    /**
     * Used to get/set Eclipse preferences.
     */
    private IEclipsePreferences preferences;
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
    protected Control createContents(Composite parent) {
        if (! resolveCurrentProject()) {
            return new Composite(parent, SWT.NONE);
        }
        IScopeContext context = new ProjectScope(project);
        preferences = context.getNode(Activator.PLUGIN_ID);
        
        Composite area = new Composite(parent, SWT.NONE);
        final int numColumnsInLayout = 2;
        final boolean makeColumnsEqualWidth = false;
        GridLayout layout = new GridLayout(numColumnsInLayout, makeColumnsEqualWidth);
        layout.numColumns = 1;
        area.setLayout(layout);
        area.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        createWordingGroup(area);
        new Label(area, SWT.HORIZONTAL);
        createVariablesTable(area);

        return area;
    }

    private void createWordingGroup(Composite parent) {
        Group group = createGroup(parent, " Wording ");
        
        // "[ ] Use custom wording" checkbox
        
        useCustomWording = new Button(group, SWT.CHECK | SWT.LEFT);
        useCustomWording.setText("Use &custom wording");
        useCustomWording.setSelection(preferences.getBoolean(USE_CUSTOM_WORDING.property(), USES_CUSTOM_WORDING_BY_DEFAULT));
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
        detailsWording.setText(preferences.get(CUSTOM_DISCORD_DETAILS_WORDING.property(), DEFAULT_CUSTOM_DETAILS_WORDING));
        detailsWording.setToolTipText("The top text");
        GridDataFactory.createFrom(new GridData(GridData.FILL_HORIZONTAL)).span(2, 1).applyTo(detailsWording);
        
        // "State" field
        
        Label stateLabel = new Label(group, SWT.LEFT);
        stateLabel.setText("State: ");
        stateLabel.setToolTipText("The bottom text");
        GridDataFactory.swtDefaults().indent(LayoutConstants.getIndent(), 0).applyTo(stateLabel);
        
        stateWording = new Text(group, SWT.BORDER);
        stateWording.setEnabled(useCustomWording.getSelection());
        stateWording.setText(preferences.get(CUSTOM_DISCORD_STATE_WORDING.property(), DEFAULT_CUSTOM_STATE_WORDING));
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
    
    @Override
    public void performDefaults() {
        super.performDefaults();
        useCustomWording.setSelection(USES_CUSTOM_WORDING_BY_DEFAULT);
        detailsWording.setText(DEFAULT_CUSTOM_DETAILS_WORDING);
        stateWording.setText(DEFAULT_CUSTOM_STATE_WORDING);
        
        detailsWording.setEnabled(useCustomWording.getSelection());
        stateWording.setEnabled(useCustomWording.getSelection());
    }
    
    @Override
    public boolean performOk() {
        try {
            preferences.putBoolean(USE_CUSTOM_WORDING.property(), useCustomWording.getSelection());
            preferences.put(CUSTOM_DISCORD_DETAILS_WORDING.property(), detailsWording.getText());
            preferences.put(CUSTOM_DISCORD_STATE_WORDING.property(), stateWording.getText());

            preferences.flush();
        }
        catch (BackingStoreException e) {
            // Should never happen
            Plugin.logException("An unexpected error occurred while saving preferences", e);
            return false;
        }
        return true;
    }

}
