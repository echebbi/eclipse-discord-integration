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

import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.ImmutableRichPresence;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputToRichPresenceAdapter;
import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * <p>Default implementation of {@link EditorInputToRichPresenceAdapter}.</p>
 * <p>
 * This implementation only operates on {@link IFileEditorInput} instances and sets Rich Presence as follows:
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
public class DefaultFileEditorInputRichPresence implements EditorInputToRichPresenceAdapter {
    
    private static final String DEFAULT_DETAILS_TEMPLATE = "Editing ${file}";
    private static final String DEFAULT_STATE_TEMPLATE = "Working on ${project}";
    
    @Override
    public int getPriority() {
        return 0;
    }
    
    @Override
    public Class<IFileEditorInput> getExpectedEditorInputClass() {
        return IFileEditorInput.class;
    }
    
    @Override
    public Optional<RichPresence> createRichPresence(GlobalPreferences preferences, IEditorInput input) {
        if (!(input instanceof IFileEditorInput)) {
            throw new IllegalArgumentException("input must be an instance of " + IFileEditorInput.class);
        }
        IFileEditorInput fileInput = (IFileEditorInput) input;
        IFile file = fileInput.getFile();
        IProject project = file.getProject();
        
        UserPreferences applicablePreferences = preferences.getApplicablePreferencesFor(project);
        Language language = languageOf(applicablePreferences, file);
        
        RichPresence presence = new ImmutableRichPresence() 
                .withProject(project)
                .withLanguage(language)
                .withDetails(detailsOf(applicablePreferences, project, language, file))
                .withState(stateOf(applicablePreferences, project, language, file))
                .withLargeImageText(largeImageTextOf(applicablePreferences, file));
        
        return Optional.of(presence);
    }

    private static String detailsOf(UserPreferences preferences, IProject project, Language language, IFile file) {
        String template = preferences.getCustomDetailsWording().orElse(DEFAULT_DETAILS_TEMPLATE);
        
        template = template.replace("${file}", preferences.showsFileName() ? file.getName() : "?");
        template = template.replace("${file.baseName}", preferences.showsFileName() ? getBaseFileName(file) : "?");
        template = template.replace("${file.extension}", preferences.showsFileName() ? getFileExtension(file) : "?");
        template = template.replace("${language}", language.getName());
        template = template.replace("${project}", preferences.showsProjectName() ? nameOf(project) : "?");
        
        return template;
    }

    private static String stateOf(UserPreferences preferences, IProject project, Language language, IFile file) {
        String template = preferences.getCustomStateWording().orElse(DEFAULT_STATE_TEMPLATE);
        
        template = template.replace("${file}", preferences.showsFileName() ? file.getName() : "?");
        template = template.replace("${file.baseName}", preferences.showsFileName() ? getBaseFileName(file) : "?");
        template = template.replace("${file.extension}", preferences.showsFileName() ? getFileExtension(file) : "?");
        template = template.replace("${language}", language.getName());
        template = template.replace("${project}", preferences.showsProjectName() ? nameOf(project) : "?");
        
        return template;
    }

    /** Returns either the name of the project, or "an unknown project" is project == null. */
    private static String nameOf(IProject project) {
        if (project == null) {
            return "an unknown project";
        }
        return project.getName(); 
    }

    private static Language languageOf(UserPreferences preferences, IFile file) {
        if (! preferences.showsLanguageIcon()) {
            return Language.UNKNOWN;
        }
        return Language.fromFileName(file.getName());
    }

    private static String largeImageTextOf(UserPreferences preferences, IFile file) {
        if (! preferences.showsLanguageIcon()) {
            return "";
        }
        Language language = Language.fromFileName(file.getName());
        return labelOf(language, file.getName());
    }
    
    private static String getFileExtension(IFile file) {
        String fileName = file.getName();
        if (!fileName.contains(".") || fileName.endsWith(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
    
    private static String getBaseFileName(IFile file) {
        String fileName = file.getName();
        if (!fileName.contains(".") || fileName.endsWith(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

}
