/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.files;

import java.util.Optional;

import org.eclipse.ui.IEditorPart;

import fr.kazejiyu.discord.rpc.integration.core.SelectionTimes;

/**
 * Indicates what the user is working on. 
 * 
 * @author Emmanuel CHEBBI
 */
public class EditionContext {
    
    private IEditorPart editor;
    
    /** Provides access to the different timestamps. */
    private SelectionTimes times = new SelectionTimes();

    /**
     * Returns the last IEditorPart selected by the user and that is still open.
     * @return the last IEditorPart selected by the user, or nothing if there is none
     */
    public Optional<IEditorPart> lastSelectedEditor() {
        return Optional.ofNullable(editor);
    }

    /**
     * Sets the last IEditorPart selected by the user.
     * <p>
     * Accepts {@code null} for indicating that no editor is activated.
     * 
     * @param editor
     *          The last editor selected by the user or null if there is none
     */
    public void setLastSelectedEditor(IEditorPart editor) {
        this.editor = editor;
    }

    /**
     * Returns the elapsed times.
     * @return the elapsed times
     */
    public SelectionTimes getElapsedTimes() {
        return times;
    }
    
}
