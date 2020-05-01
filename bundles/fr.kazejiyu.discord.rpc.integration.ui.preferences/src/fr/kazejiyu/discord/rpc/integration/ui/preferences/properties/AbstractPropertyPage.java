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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Abstract PropertyPage providing new services to sub-classes.
 * 
 * @author Emmanuel CHEBBI
 */
abstract class AbstractPropertyPage extends PropertyPage {
    
    /** The project which properties are shown by this page. */
    protected IProject project;
    
    /**
     * Sets {@link #project} to the value of the current project.
     * 
     * @return {@code true} if the current project has been resolved successfully,
     *            {@code false} otherwise.
     */
    protected boolean resolveCurrentProject() {
        final IAdaptable adaptable = getElement();
        
        if (adaptable == null) {
            return false;
        }
        project = adaptable.getAdapter(IProject.class);
        return project != null;
    }
    
    protected static void addSeparator(Composite parent) {
        Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        separator.setLayoutData(gridData);
    }

}
