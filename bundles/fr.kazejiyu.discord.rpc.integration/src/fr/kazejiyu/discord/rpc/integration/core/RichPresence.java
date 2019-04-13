/*******************************************************************************
 * Copyright (C) 2018-2019 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.core;

import java.util.Optional;

import org.eclipse.core.resources.IProject;

import fr.kazejiyu.discord.rpc.integration.languages.Language;

/**
 * The information about current selection to show in Discord.
 */
public interface RichPresence {

    /**
     * Returns the details to show on Discord, if any.
     * @return the details to show on Discord, if any
     */
    Optional<String> getDetails();
    
    /**
     * Returns the state to show on Discord, if any.
     * @return the state to show on Discord, if any
     */
    Optional<String> getState();

    /**
     * Returns the start timestamp, if any.
     * @return the start timestamp, if any
     */
    Optional<Long> getStartTimestamp();

    /**
     * Returns the language of the active file, if known.
     * @return the language of the active file, if known
     */
    Optional<Language> getLanguage();

    /**
     * Returns the text to show when hovering the large icon, if any.
     * @return the text to show when hovering the large icon, if any
     */
    Optional<String> getLargeImageText();

    /**
     * Returns the Eclipse project owning the resource related to this presence, if any.
     * @return the Eclipse project owning the resource related to this presence, if any
     */
    Optional<IProject> getProject();
}
