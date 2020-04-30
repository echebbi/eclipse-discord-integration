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

import java.util.function.Function;

import org.eclipse.jface.viewers.ColumnLabelProvider;

/**
 * Provides a column label based on a given lambda.
 * 
 * @author Emmanuel Chebbi
 *
 * @param <T> The type of the objects contained by the column
 */
public class LambdaLabelProvider<T> extends ColumnLabelProvider {

    private final Function<T, String> labelProvider;

    /**
     * Creates a new label provider.
     * 
     * @param labelProvider
     *          The lambda actually used to compute elements' label.
     */
    public LambdaLabelProvider(Function<T, String> labelProvider) {
        super();
        this.labelProvider = labelProvider;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getText(Object element) {
        try {
            return labelProvider.apply((T) element);
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException("cannot compute label of " + element, e);
        }
    }

}