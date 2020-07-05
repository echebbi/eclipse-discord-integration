/*******************************************************************************
 * Copyright (C) 2018-2020 Emmanuel CHEBBI
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package fr.kazejiyu.discord.rpc.integration.extensions.internal;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.ui.IEditorInput;

import fr.kazejiyu.discord.rpc.integration.Plugin;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorInputToRichPresenceAdapter;
import fr.kazejiyu.discord.rpc.integration.extensions.EditorRichPresenceFromInput;

/**
 * <p>Manages plug-in's extensions.</p>
 * 
 * <p>Instances of this class are notably charged of choosing the right
 * {@link EditorInputToRichPresenceAdapter adapter} for a given {@link IEditorInput}.</p>
 * 
 * <p>Please note that this implementation does not cache the adapters. 
 * As a result, contributions provided by new plug-ins that have been installed 
 * during runtime should be dynamically handled. However, this can have important 
 * consequences on the performance if this method is often called.</p>
 * 
 * @author Emmanuel CHEBBI
 */
public class EditorRichPresenceFromExtensions implements EditorRichPresenceFromInput {

    /** The registry in which extensions are looked for. */
    private final IExtensionRegistry registry;
    
    /**
     * Creates a new instance to manage plug-in-related extensions.
     * 
     * @param registry
     *             The registry storing all the contributions to extension points.
     */
    public EditorRichPresenceFromExtensions(IExtensionRegistry registry) {
        this.registry = requireNonNull(registry, "Cannot adapt extensions from a null registry");
    }

    @Override
    public Optional<EditorInputToRichPresenceAdapter> findAdapterFor(IEditorInput input) {
        return allAdaptersIn(registry).stream()
                                      .filter(canHandle(input))
                                      .sorted(byDepthInTreeFrom(input.getClass()))
                                      .findFirst();
    }
    
    /** Returns all the elements registered through {@value #EDITOR_INPUT_ADAPTER_EXTENSION_ID} extension
     *          that are instances of {@link EditorInputToRichPresenceAdapter}. */
    private static List<EditorInputToRichPresenceAdapter> allAdaptersIn(IExtensionRegistry registry) {
        List<EditorInputToRichPresenceAdapter> adaptersFound = new ArrayList<>();
        
        IConfigurationElement[] elements = registry.getConfigurationElementsFor(Plugin.EDITOR_INPUT_ADAPTER_EXTENSION_ID);
        
        for (IConfigurationElement element : elements) {
            Object extension = createExecutableExtension(element);
            
            if (extension instanceof EditorInputToRichPresenceAdapter) {
                adaptersFound.add((EditorInputToRichPresenceAdapter) extension);
            }
        }
        
        return adaptersFound;
    }

    /** Returns a new instance of {@code element}'s class property if possible, {@code null} otherwise. */
    private static Object createExecutableExtension(IConfigurationElement element) {
        try {
            return element.createExecutableExtension("class");

        } 
        catch (CoreException e) {
            Plugin.logException("Unable to create a new instance for the " + element.getName() + " extension", e);
            return null;
        }
    }

    /** Returns whether {@code adapter} can handle {@code input}. */
    private static Predicate<EditorInputToRichPresenceAdapter> canHandle(IEditorInput input) {
        return adapter -> adapter.getExpectedEditorInputClass() != null
        			   && adapter.getExpectedEditorInputClass().isInstance(input);
    }
    
    /**
     * Returns a comparator comparing two elements depending on 
     *            the distance between their class and {@code parent}. */
    private static <T> Comparator<EditorInputToRichPresenceAdapter> byDepthInTreeFrom(Class<T> parent) {
        return (lhs, rhs) -> {
            int lhsProximity = nbrOfClassesBetween(lhs.getExpectedEditorInputClass(), parent);
            int rhsProximity = nbrOfClassesBetween(rhs.getExpectedEditorInputClass(), parent);
            
            // Same distance to parent class, so let's use the priority (highest first)
            if (lhsProximity == rhsProximity) {
                return rhs.getPriority() - lhs.getPriority();
            }
            // Lowest proximity first
            return lhsProximity - rhsProximity;
        };
    }

    /** Returns the number of classes between {@code parent} and {@code child}. */
    private static <P, C> int nbrOfClassesBetween(Class<P> parent, Class<C> child) {
        if (parent.equals(child)) {
            return 0;
        }
        // True if child inherits from a sub-class of parent
        if (child.getSuperclass() != null && parent.isAssignableFrom(child.getSuperclass())) {
            return 1 + nbrOfClassesBetween(parent, child.getSuperclass());
        }
        for (Class<?> interf : child.getInterfaces()) {
            if (parent.isAssignableFrom(interf)) {
                return 1 + nbrOfClassesBetween(parent, interf);
            }
        }
        // Should not happen if child inherits from parent
        return Integer.MAX_VALUE;
    }
}
