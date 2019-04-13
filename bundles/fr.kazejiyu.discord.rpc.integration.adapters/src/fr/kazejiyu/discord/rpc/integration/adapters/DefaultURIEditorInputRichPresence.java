/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.adapters;

import static fr.kazejiyu.discord.rpc.integration.adapters.LanguageLabel.labelOf;

import java.io.File;
import java.net.URI;
import java.util.Optional;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.ImmutableRichPresence;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * <p>Default implementation of {@link EditorInputRichPresence}.</p>
 * <p>
 * This implementation only operates on {@link IURIEditorInput} instances and sets Rich Presence as follows:
 * 
 * <table style="border: 1px solid black ; border-collapse: collapse">
 *     <tr style="border: 1px solid black">
 *         <th style="border: 1px solid black">Property</th>
 *         <th style="border: 1px solid black">Shown in Discord</th>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid black"><b>Details</b></td>
 *         <td style="border: 1px solid black">Editing <i>&lt;file.name&gt;</i></td>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid black"><b>State</b></td>
 *         <td style="border: 1px solid black">Working on <i>&lt;project.name&gt;</i></td>
 *     </tr>
 * </table>
 * </p>
 * 
 * @author Emmanuel CHEBBI
 */
public class DefaultURIEditorInputRichPresence implements EditorInputRichPresence {
    
    @Override
    public int getPriority() {
        return 0;
    }
    
    @Override
    public Class<FileStoreEditorInput> getExpectedEditorInputClass() {
        return FileStoreEditorInput.class;
        
        // TODO Handle inheritance so that returning IURIEditorInput.class here does not prevent
        // DefaultFileEditorInputRichPresence to be taken into account at runtime
//        return IURIEditorInput.class;
    }
    
    @Override
    public Optional<RichPresence> createRichPresence(GlobalPreferences preferences, IEditorInput input) {
        if (!(input instanceof IURIEditorInput)) {
            throw new IllegalArgumentException("input must be an instance of " + IURIEditorInput.class);
        }
        
        IURIEditorInput uriInput = (IURIEditorInput) input;
        URI fileURI = uriInput.getURI();
        File file = new File(fileURI.getPath());
        
        ImmutableRichPresence presence = new ImmutableRichPresence()
                .withDetails(detailsOf(preferences, file))
                .withState(stateOf(preferences))
                .withLanguage(languageOf(preferences, file))
                .withLargeImageText(largeImageTextOf(preferences, file));
        
        return Optional.of(presence);
    }

    private static String detailsOf(GlobalPreferences preferences, File file) {
        if (! preferences.showsFileName()) {
            return "";
        }
        return "Editing " + file.getName();
    }

    private static String stateOf(GlobalPreferences preferences) {
        if (! preferences.showsProjectName()) {
            return "";
        }
        return "Unknown project";
    }

    private static Language languageOf(UserPreferences preferences, File file) {
        if (! preferences.showsLanguageIcon()) {
            return Language.UNKNOWN;
        }
        return Language.fromFileName(file.getName());
    }

    private static String largeImageTextOf(UserPreferences preferences, File file) {
        if (! preferences.showsLanguageIcon()) {
            return "";
        }
        Language language = Language.fromFileName(file.getName());
        return labelOf(language, file.getName());
    }

}
