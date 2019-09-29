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

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.ui.IEditorPart;

import fr.kazejiyu.discord.rpc.integration.core.PreferredRichPresence;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.core.SelectionTimes;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputToRichPresenceAdapter;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorRichPresenceFromInput;
import fr.kazejiyu.discord.rpc.integration.extensions.internal.UnknownInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;

/**
 * An adapter able to turn {@link EditionContext} instances into {@link RichPresence} ones.
 * 
 * @author Emmanuel CHEBBI
 */
public class EditorToRichPresenceAdapter implements Function<EditionContext, Optional<RichPresence>> {
    
    /** Used to to create a RichPresence from the selected editor. */
    private final EditorRichPresenceFromInput adapters;
    
    /** User's preferences regarding the information to show in Discord. */
    private final GlobalPreferences preferences;

    /**
     * Creates a new adapter able to turn EditionContext instances into RichPresence ones.
     * 
     * @param preferences
     *          User's preferences regarding the information to show in Discord.
     *          Must not be {@code null}.
     * @param adapters
     *          Will be notified with a new {@link RichPresence} instance each time
     *          the active editor changes. Must not be null.
     */
    public EditorToRichPresenceAdapter(GlobalPreferences preferences, EditorRichPresenceFromInput adapters) {
        this.adapters = requireNonNull(adapters, "adapters");
        this.preferences = requireNonNull(preferences, "preferences");
    }
    
    @Override
    public Optional<RichPresence> apply(EditionContext context) {
        return context.lastSelectedEditor()
                      .flatMap(this::createRichPresenceFrom)
                      .map(presence -> tailorToPreferences(presence, context.getElapsedTimes()));
    }
    
    private Optional<RichPresence> createRichPresenceFrom(IEditorPart editor) {
        return adapters.findAdapterFor(editor.getEditorInput())
                       .orElseGet(defaultAdapter())
                       .createRichPresence(preferences, editor.getEditorInput());
    }

    /** Returns a built-in adapter that sends nothing to Discord. */
    private static Supplier<EditorInputToRichPresenceAdapter> defaultAdapter() {
        return UnknownInputRichPresence::new;
    }
    
    /** Returns a new RPC equivalent to the given one but which attributes follow user's presences. */
    private RichPresence tailorToPreferences(RichPresence presence, SelectionTimes times) {
        return new PreferredRichPresence(
            preferences.getApplicablePreferencesFor(presence.getProject().orElse(null)), 
            presence, 
            times
        );
    }

}
