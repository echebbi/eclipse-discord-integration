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

import static fr.kazejiyu.discord.rpc.integration.languages.Language.BINARY;
import static fr.kazejiyu.discord.rpc.integration.languages.Language.DOCKER;
import static fr.kazejiyu.discord.rpc.integration.languages.Language.GIT;
import static fr.kazejiyu.discord.rpc.integration.languages.Language.SCALA;
import static fr.kazejiyu.discord.rpc.integration.languages.Language.TERMINAL;
import static fr.kazejiyu.discord.rpc.integration.languages.Language.TEXT;
import static fr.kazejiyu.discord.rpc.integration.languages.Language.UNKNOWN;

import fr.kazejiyu.discord.rpc.integration.languages.Language;

/**
 * Creates a label for a given {@link Language}.
 * 
 * @author Emmanuel CHEBBI
 */
// TODO [Refactor] Consider turning Language into an OO architecture to get rid of this class
final class LanguageLabel {
    
    private LanguageLabel() {
        // utility classes do not need to be instantiated
    }
    
    protected static String labelOf(Language language, String fileName) {
        if (language == UNKNOWN) {
            return "";
        }
        if (language == BINARY) {
            return "Binary file";
        }
        if (language == DOCKER) {
            return "Configuring Docker image";
        }        
        if (language == GIT) {
            return "Configuring Git";
        }
        if (language == SCALA && fileName.endsWith(".sbt")) {
            return "Configuring SBT build";
        }
        if (language == TERMINAL) {
            return "Configuring an OS script";
        }
        if (language == TEXT) {
            return "Text file";
        }
        return "Programming in " + language.getName();
    }

}
