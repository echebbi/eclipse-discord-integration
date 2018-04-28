/*********************************************************************
* Copyright (c) 2018 Emmanuel CHEBBI
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package fr.kazejiyu.discord.rpc.integration.extensions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.ui.IEditorInput;

import fr.kazejiyu.discord.rpc.integration.core.RichPresence;

/**
 * Manages plug-in's extensions.<br>
 * <br>
 * Instances of this class are notably charged of choosing the right
 * {@link EditorInputRichPresence adapter} for a given {@link IEditorInput}.
 * 
 * @author Emmanuel CHEBBI
 */
public class DiscordIntegrationExtensions {

	private static final String EDITOR_INPUT_ADAPTER_EXTENSION_ID = "fr.kazejiyu.discord.rpc.integration.editor_input_adapter";

	/** The adapters able to create RichPresence instances from IEditorInput instances */
	private final List<EditorInputRichPresence> adapters;
	
	public DiscordIntegrationExtensions() {
		this.adapters = findAllAdapters();
		this.adapters.sort(null);
	}

	/**
	 * Returns an adapter able to turn {@code input} into a {@link RichPresence}
	 * instance.<br>
	 * <br>
	 * The adapter is one of the adapters registered to the
	 * {@value #EDITOR_INPUT_ADAPTER_EXTENSION_ID} extension point.
	 * 
	 * @param input
	 *			The input to turn into a {@code RichPresence} instance. 
	 *          Must not be {@code null}.
	 * 
	 * @return an adapter able to handle {@code input}, if any.
	 */
	public Optional<EditorInputRichPresence> findAdapterFor(IEditorInput input) {
		return adapters.stream()
					   .filter(canHandle(input))
					   .sorted(byDepthInTreeFrom(input.getClass()))
					   .findFirst();
	}
	
	/** @return all the elements registered through {@value #EDITOR_INPUT_ADAPTER_EXTENSION_ID} extension
	 *  		that are instances of {@link EditorInputRichPresence} */
	private List<EditorInputRichPresence> findAllAdapters() {
		List<EditorInputRichPresence> adaptersFound = new ArrayList<>();
		
		IConfigurationElement[] elements = 
				RegistryFactory.getRegistry().getConfigurationElementsFor(EDITOR_INPUT_ADAPTER_EXTENSION_ID);
		
		for (IConfigurationElement element : elements) {
			Object extension = createExecutableExtension(element);
			
			if (extension instanceof EditorInputRichPresence)
				adaptersFound.add((EditorInputRichPresence) extension);
		}
		
		return adaptersFound;
	}

	/** @return a new instance of {@code element}'s class property if possible, {@code null} otherwise */
	private Object createExecutableExtension(IConfigurationElement element) {
		try {
			return element.createExecutableExtension("class");

		} catch (CoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	/** @return whether {@code adapter} can handle {@code input} */
	private Predicate<EditorInputRichPresence> canHandle(IEditorInput input) {
		return adapter -> adapter.getExpectedEditorInputClass().isInstance(input);
	}
	
	/**
	 * @return a comparator comparing two elements depending on 
	 * 		   the distance between their class and {@code parent}. */
	private <T> Comparator<EditorInputRichPresence> byDepthInTreeFrom(Class<T> parent) {
		return (lhs, rhs) -> {
			int lhsProximity = nbrOfClassesBetween(lhs.getExpectedEditorInputClass(), parent);
			int rhsProximity = nbrOfClassesBetween(rhs.getExpectedEditorInputClass(), parent);
			
			// Same distance to parent class, so let's use the priority
			if (lhsProximity == rhsProximity)
				return rhs.getPriority() - lhs.getPriority();
			
			return rhsProximity - lhsProximity;
		};
	}

	/** @return the number of classes between {@code parent} and {@code child} */
	private <P, C> int nbrOfClassesBetween(Class<P> parent, Class<C> child) {
		if (parent.equals(child))
			return 0;

		// True if child inherits from a sub-class of parent
		if (child.getSuperclass() != null && parent.isAssignableFrom(child.getSuperclass()))
			return 1 + nbrOfClassesBetween(parent, child.getSuperclass());
		
		for (Class<?> interf : child.getInterfaces())
			if (parent.isAssignableFrom(interf))
				return 1 + nbrOfClassesBetween(parent, interf);
		
		// Should not happen if child inherit from parent
		return Integer.MAX_VALUE;
	}
}
