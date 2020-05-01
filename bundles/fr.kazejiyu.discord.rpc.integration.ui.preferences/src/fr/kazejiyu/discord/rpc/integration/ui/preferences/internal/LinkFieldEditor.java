/*******************************************************************************
 * Copyright (C) 2018-2020 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.ui.preferences.internal;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;

/**
 * A field editor showing an external link as a clickable text.
 * <p>
 * Clicking on the text opens the link in the default external web browser. 
 */
public class LinkFieldEditor extends FieldEditor {
    
    /**
     * The text displayed in the Preferences page.
     */
    private final String text;
    
    /**
     * The link to open in the external browser.
     */
    private final String link;
    
    /**
     * The index of the first text character corresponding to the link.
     */
    private final int linkStartIndex;
    
    /**
     * The length of the link from linkStartIndex.
     */
    private final int linkLength;

    /**
     * Creates a new clickable text that opens a link on mouse click.
     * 
     * @param text
     *          The text to show in the Preferences page.
     * @param link
     *          The link to open in the external web browser on mouse click.
     * @param parent
     *          The parent of the link.
     */
    public LinkFieldEditor(String text, String link, Composite parent) {
        this(text, link, 0, text.length(), parent);
    }

    /**
     * Creates a new clickable text that opens a link on mouse click.
     * 
     * @param text
     *          The text to show in the Preferences page.
     * @param link
     *          The link to open in the external web browser on mouse click.
     * @param linkStartIndex
     *          The index of the first text character corresponding to the link.
     * @param linkLength
     *          The length of the link starting from linkStartIndex.
     * @param parent
     *          The parent of the link.
     */
    public LinkFieldEditor(String text, String link, int linkStartIndex, int linkLength, Composite parent) {
        super();
        this.text = text;
        this.link = link;
        this.linkStartIndex = linkStartIndex;
        this.linkLength = linkLength;
        createControl(parent);
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        StyledText styledText = new StyledText(parent, SWT.NONE);
        styledText.setText(" " + text);
        styledText.setBackground(parent.getBackground());
        styledText.setMarginColor(parent.getBackground());

        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        styledText.setLayoutData(gd);
        styledText.setLeftMargin(0);
        
        StyleRange style = new StyleRange();
        style.underline = true;
        style.underlineStyle = SWT.UNDERLINE_LINK;
        style.start = this.linkStartIndex;
        style.length = this.linkLength;
        styledText.setStyleRange(style);
        
        styledText.addListener(SWT.MouseDown, event -> {
            int clickOffset = styledText.getCaretOffset();
            if (this.linkStartIndex <= clickOffset && clickOffset < this.linkStartIndex + this.linkLength) {
                // Open the documentation with external browser
                Program.launch(link);
            }
        });
        styledText.setBottomMargin(5);
        styledText.setToolTipText(link);
    }
    
    @Override
    protected void adjustForNumColumns(int numColumns) {
        // is something really needed here?
    }

    @Override
    protected void doLoad() {
        // nothing to load
    }

    @Override
    protected void doLoadDefault() {
        // nothing to load
    }

    @Override
    protected void doStore() {
        // nothing to store
    }

    @Override
    public int getNumberOfControls() {
        return 1;
    }
    
}
