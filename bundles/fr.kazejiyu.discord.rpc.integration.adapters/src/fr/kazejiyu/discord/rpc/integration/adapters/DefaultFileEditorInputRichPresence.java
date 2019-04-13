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

import java.util.Optional;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.ImmutableRichPresence;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputRichPresence;
import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * <p>Default implementation of {@link EditorInputRichPresence}.</p>
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
public class DefaultFileEditorInputRichPresence implements EditorInputRichPresence {
    
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
        
        ImmutableRichPresence presence = new ImmutableRichPresence()
                .withProject(project)
                .withDetails(detailsOf(applicablePreferences, file))
                .withState(stateOf(applicablePreferences, project))
                .withLanguage(languageOf(applicablePreferences, file))
                .withLargeImageText(largeImageTextOf(applicablePreferences, file));
        
        return Optional.of(presence);
    }

    private static String detailsOf(UserPreferences preferences, IFile file) {
        if (! preferences.showsFileName()) {
            return "";
        }
        return "Editing " + file.getName();
    }

    private static String stateOf(UserPreferences preferences, IProject project) {
        if (! preferences.showsProjectName()) {
            return "";
        }
        String projectName = preferences.getProjectName()
                                        .orElse(nameOf(project));
        
        return "Working on " + projectName;
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

}
