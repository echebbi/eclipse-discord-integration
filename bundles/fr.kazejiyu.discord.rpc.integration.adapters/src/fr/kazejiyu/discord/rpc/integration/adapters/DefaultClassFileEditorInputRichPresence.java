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

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.ui.IEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.ImmutableRichPresence;
import fr.kazejiyu.discord.rpc.integration.core.RichPresence;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputToRichPresenceAdapter;
import fr.kazejiyu.discord.rpc.integration.languages.Language;
import fr.kazejiyu.discord.rpc.integration.settings.GlobalPreferences;
import fr.kazejiyu.discord.rpc.integration.settings.UserPreferences;

/**
 * <p>Default implementation of {@link EditorInputToRichPresenceAdapter}.</p>
 * <p>
 * This implementation only operates on {@link IClassFileEditorInput} instances and sets Rich Presence as follows:
 * 
 * <table style="border: 1px solid black ; border-collapse: collapse">
 *     <tr style="border: 1px solid black">
 *         <th style="border: 1px solid black">Property</th>
 *         <th style="border: 1px solid black">Shown in Discord</th>
 *     </tr>
 *     <tr>
 *         <td style="border: 1px solid black"><b>Details</b></td>
 *         <td style="border: 1px solid black">Viewing <i>&lt;file.name&gt;</i></td>
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
public class DefaultClassFileEditorInputRichPresence implements EditorInputToRichPresenceAdapter {

	private static final String DEFAULT_DETAILS_TEMPLATE = "Viewing ${file}";
    private static final String DEFAULT_STATE_TEMPLATE = "Working on ${project}";
    
    @Override
    public int getPriority() {
        return 0;
    }
    
    @Override
    public Class<IClassFileEditorInput> getExpectedEditorInputClass() {
    	try {
    		return IClassFileEditorInput.class;
    	}
    	catch (NoClassDefFoundError e) {
    		// may happen since JDT dependencies are optional
    		return null;
    	}
    }
    
    @Override
    public Optional<RichPresence> createRichPresence(GlobalPreferences preferences, IEditorInput input) {
        try {
	    	if (!(input instanceof IClassFileEditorInput)) {
	            throw new IllegalArgumentException("input must be an instance of " + IClassFileEditorInput.class);
	        }
	        IClassFileEditorInput fileInput = (IClassFileEditorInput) input;
	        String fileName = fileInput.getName();
	        Optional<IProject> project = projectDependingOn(fileInput.getClassFile());
	        
	        UserPreferences applicablePreferences = project.map(preferences::getApplicablePreferencesFor)
	        											   .orElse(preferences);
	        
	        RichPresence presence = new ImmutableRichPresence()
	        		.withProject(project.orElse(null))
	                .withLanguage(Language.JAVA)
	                .withDetails(detailsOf(applicablePreferences, project, fileName))
	                .withState(stateOf(applicablePreferences, project, fileName))
	                .withLargeImageText(largeImageTextOf(applicablePreferences, fileName));
	        
	        return Optional.of(presence);
        }
        catch (NoClassDefFoundError e) {
        	// may happen since JDT dependencies are optional
        	return Optional.empty();
        }
    }

    private static Optional<IProject> projectDependingOn(IClassFile classFile) {
		if (classFile == null || classFile.getJavaProject() == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(classFile.getJavaProject().getProject());
	}

	private static String detailsOf(UserPreferences preferences, Optional<IProject> project, String fileName) {
        String template = preferences.getCustomDetailsWording().orElse(DEFAULT_DETAILS_TEMPLATE);
        
        template = template.replace("${file}", preferences.showsFileName() ? fileName : "?");
        template = template.replace("${file.baseName}", preferences.showsFileName() ? getBaseFileName(fileName) : "?");
        template = template.replace("${file.extension}", preferences.showsFileName() ? getFileExtension(fileName) : "?");
        template = template.replace("${language}", Language.JAVA.getName());
        template = template.replace("${project}", preferences.showsProjectName() ? project.map(IProject::getName).orElse("undetermined") : "?");
        
        return template;
    }

    private static String stateOf(UserPreferences preferences, Optional<IProject> project, String fileName) {
        String template = preferences.getCustomStateWording().orElse(DEFAULT_STATE_TEMPLATE);
        
        template = template.replace("${file}", preferences.showsFileName() ? fileName : "?");
        template = template.replace("${file.baseName}", preferences.showsFileName() ? getBaseFileName(fileName) : "?");
        template = template.replace("${file.extension}", preferences.showsFileName() ? getFileExtension(fileName) : "?");
        template = template.replace("${language}", Language.JAVA.getName());
        template = template.replace("${project}", preferences.showsProjectName() ? project.map(IProject::getName).orElse("undetermined") : "?");
        
        return template;
    }

    private static String largeImageTextOf(UserPreferences preferences, String fileName) {
        if (! preferences.showsLanguageIcon()) {
            return "";
        }
        return labelOf(Language.JAVA, fileName);
    }
    
    private static String getFileExtension(String fileName) {
        if (!fileName.contains(".") || fileName.endsWith(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
    
    private static String getBaseFileName(String fileName) {
        if (!fileName.contains(".") || fileName.endsWith(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

}
