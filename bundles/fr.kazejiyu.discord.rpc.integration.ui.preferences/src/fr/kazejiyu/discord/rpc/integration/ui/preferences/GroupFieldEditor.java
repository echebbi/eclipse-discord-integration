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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class GroupFieldEditor extends FieldEditor {
	
	private static final int MARGIN = 6;
	
	private Collection<FieldEditor> fields = new ArrayList<>();
	
	private final Group group;
	
	public GroupFieldEditor(String name, Composite parent) {
		FillLayout layout = new FillLayout();
		layout.marginWidth = layout.marginHeight = MARGIN;
		parent.setLayout(layout);
		
		this.group = new Group(parent, SWT.DEFAULT);
		this.group.setText(name);
	}
	
	public Composite getFieldEditorParent() {
		return group;
	}
	
	public void addFieldEditor(FieldEditor field) {
		fields.add(field);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		// is something really needed here ?
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		group.setLayout(new GridLayout());
		group.getParent().layout();
		group.getParent().redraw();
		
		for (FieldEditor field : fields)
			field.fillIntoGrid(getFieldEditorParent(), numColumns);
	}
	
	@Override
	protected void doLoad() {
		for (FieldEditor field : fields) {
			field.load();
		}
	}

	@Override
	protected void doLoadDefault() {
		for (FieldEditor field : fields) {
			field.loadDefault();
		}
	}

	@Override
	protected void doStore() {
		for (FieldEditor field : fields) {
			field.store();
		}
	}

	@Override
	public void store() {
		if (getPreferenceStore() == null)
			return;
		
		if (presentsDefaultValue()) {
			for (FieldEditor field : fields) {
				getPreferenceStore().setToDefault(field.getPreferenceName());
			}
		}
		else {
			doStore();
		}
	}
	
	@Override
	public int getNumberOfControls() {
		return 1;
	}
	
	@Override
	public void setPreferenceStore(IPreferenceStore store) {
		super.setPreferenceStore(store);
		for (FieldEditor field : fields)
			field.setPreferenceStore(store);
	}
	
	@Override
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		for (FieldEditor field : fields)
			field.setEnabled(enabled, parent);
	}
	
}
