/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.extensions;

import java.util.Optional;

import org.eclipse.ui.IEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.ImmutableRichPresence;

/**
 * Turns an {@link IEditorInput} into an {@link EditorInputRichPresence}.
 * 
 * @author Emmanuel CHEBBI
 */
@FunctionalInterface
public interface EditorRichPresenceFromInput {
    
    /**
     * <p>Returns an adapter able to turn {@code input} into a {@link ImmutableRichPresence}
     * instance.</p>
     * 
     * <p>The adapter is one of the adapters registered to the
     * {@value #EDITOR_INPUT_ADAPTER_EXTENSION_ID} extension point.</p>
     * 
     * @param input
     *            The input to turn into a {@code RichPresence} instance. 
     *          Must not be {@code null}.
     * 
     * @return an adapter able to handle {@code input}, if any.
     */
    Optional<EditorInputRichPresence> findAdapterFor(IEditorInput input);

}
