/*******************************************************************************
 * Copyright (C) 2018-2020 Emmanuel CHEBBI
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
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputToRichPresenceAdapter;
import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * <p>Default implementation of {@link EditorInputToRichPresenceAdapter}.</p>
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
public class DefaultURIEditorInputRichPresence implements EditorInputToRichPresenceAdapter {
    
    private static final String DEFAULT_DETAILS_TEMPLATE = "Editing ${file}";
    private static final String DEFAULT_STATE_TEMPLATE = "Working on ${project}";
    
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
        Language language = languageOf(preferences, file);
        
        ImmutableRichPresence presence = new ImmutableRichPresence();
        presence = presence
                .withLanguage(language)
                .withDetails(detailsOf(preferences, language, file))
                .withState(stateOf(preferences, language, file))
                .withLargeImageText(largeImageTextOf(preferences, file));
        
        return Optional.of(presence);
    }

    private static String detailsOf(UserPreferences preferences, Language language, File file) {
        String template = preferences.getCustomDetailsWording().orElse(DEFAULT_DETAILS_TEMPLATE);
        
        template = template.replace("${file}", preferences.showsFileName() ? file.getName() : "?");
        template = template.replace("${file.baseName}", preferences.showsFileName() ? getBaseFileName(file) : "?");
        template = template.replace("${file.extension}", preferences.showsFileName() ? getFileExtension(file) : "?");
        template = template.replace("${language}", language.getName());
        template = template.replace("${project}", preferences.showsProjectName() ? "unknown project" : "?");
        
        return template;
    }

    private static String stateOf(UserPreferences preferences, Language language, File file) {
        String template = preferences.getCustomDetailsWording().orElse(DEFAULT_STATE_TEMPLATE);
        
        template = template.replace("${file}", preferences.showsFileName() ? file.getName() : "?");
        template = template.replace("${file.baseName}", preferences.showsFileName() ? getBaseFileName(file) : "?");
        template = template.replace("${file.extension}", preferences.showsFileName() ? getFileExtension(file) : "?");
        template = template.replace("${language}", language.getName());
        template = template.replace("${project}", preferences.showsProjectName() ? "unknown project" : "?");
        
        return template;
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
    
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (!fileName.contains(".") || fileName.endsWith(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
    
    private static String getBaseFileName(File file) {
        String fileName = file.getName();
        if (!fileName.contains(".") || fileName.endsWith(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

}
