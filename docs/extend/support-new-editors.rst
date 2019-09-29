.. _part-support-new-editors:

Support new editors
###################

.. important:: This page requires some knowledge about `Eclipse RCP development <https://www.vogella.com/tutorials/EclipseRCP/article.html>`_ and `Eclipse Extension Points <https://www.vogella.com/tutorials/EclipseExtensionPoint/article.html>`_.

How editors are handled by the plug-in
--------------------------------------

The *Discord Rich Presence for Eclipse IDE* plug-in shows on Discord information extracted from the currently active editor. In order to gather these information, it proceeds as depicted in the following sequence diagram:

.. image:: http://www.plantuml.com/plantuml/png/bP71Rk8m48RlVefHzX8Ey01eXTZLeXLIfQ9UUZRsa8ngnfOzAVJjQv8OWdAg3s2ZVxwVpzucqL6wirCjMOIJztBqLwNo1rHx5eoiNQhWrmirhVnr7Ih8A-GXCXGq2roEe1DKE-Ce98Ht7p-EoAQjPidWkeQuJn_oTabSAMGBZyRTLP0JURvN_e-8F2FjB8cFUmwTl95f3n5JhiGRMbiT16C1_qZb7oNZepr4V_swiUnmVh1K4tqN0BLfGUYS2_jveDnhX3wre5KzDj_ASZCQh_lGdnAeRwV54DDEHW8dqLd1ds0_iLCmh96KcKLm3IPYnatzU_AgGPaGwiMYGChe4qMwo4-LG2m-Yqt1Sy94xZK8UTRkztHYC_ZT7SUVLvI6XDJ3tvhvbKjVGshzfIw3ZpIoLUMxVvd-JTGsRxkp-mO0
   :align: center
   :alt: Sequence diagram showing how a RichPresence is created from active editor

Basically:

1. The plug-in is notified each time a new `part <https://wiki.eclipse.org/FAQ_Pages,_parts,_sites,_windows:_What_is_all_this_stuff%3F>`_ is opened.
2. If the part is an instance of ``IEditorPart`` then we get its ``IEditorInput`` (which represents the underlying element being edited).
3. This ``IEditorInput`` is broadcasted to an instance of ``EditorInputRichPresence``, an interface defined by this plug-in.
4. The ``EditorInputRichPresence`` instance turns the ``IEditorInput`` into a ``RichPresence``.
5. The ``RichPresence`` is then shown on Discord through a proxy.

Most of this stuff is managed internally so you don't have to bother with it. The only thing you'll have to do is to register a new ``EditorInputRichPresence`` (see the `Adapt a new IEditorInput`_ section below).

Editors supported out of the box
--------------------------------

By default, almost all text-based editors should be taken into account. More specifically, all the editors which input is an instance of one of the following classes are handled:

- `IFileEditorInput <https://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fui%2FIFileEditorInput.html>`_,
- `FileStoreEditorInput <https://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fui%2Fide%2FFileStoreEditorInput.html>`_.

Adapt a new IEditorInput
------------------------

Provide a dedicated extension
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

You can provide an adapter for a given ``IEditorInput`` by contributing to the ``fr.kazejiyu.discord.rpc.integration.editor_input_adapter`` extension point. You'll have to provide a class that implements the ``EditorInputRichPresence`` interface.

This interface is defined as follows:

.. code-block:: java
   :linenos:

   /**
    * Extracts a RichPresence from an IEditorInput.
    *
    * This interface should be implemented by clients who aim to define
    * the information shown in Discord for their own editor.
    */
   public interface EditorInputRichPresence extends Comparable<EditorInputRichPresence> {

       /**
        * Helps to choose an adapter over another when several ones
        * are registered for the same {@code IEditorInput}.
        *
        * The higher the priority, the more the adapter will be favored.
        *
        * For instance, given two adapters registering themselves for inputs of type
        * FileEditorInput and which priorities are 0 and 1, then the adapter
        * of priority 1 will be chosen to handle the input.
        *
        * Built-in adapters have a priority of 0. Hence, giving a higher priority
        * ensures that the adapter will be preferred over default ones. This allows
        * to dynamically override other adapters if needed.
        *
        * It is advised to only choose tens, such as 10 or 20, instead of digits
        * so that it is easier to add new adapters later if needed.
        *
        * @return the priority associated with this adapter.
        */
       int getPriority();

       /**
        * Returns the class of the input expected as an argument of createRichPresence(GlobalPreferences, IEditorInput)}.
        * @return the class of the input expected as an argument of createRichPresence(GlobalPreferences, IEditorInput)}
        */
       Class<? extends IEditorInput> getExpectedEditorInputClass();

       /**
        * Creates the Rich Presence information to send to Discord.
        *
        * Important: this method may be called several times in a row with the same editor input.
        *
        * @param preferences
        *             User's preferences regarding the information to show in Discord.
        *             Must not be null.
        * @param input
        *             The input of the active editor.
        *             Must satisfy getExpectedEditorInputClass().isInstance(input) == true.
        *
        * @return the information to show in Discord if the input can be handled
        */
       Optional<RichPresence> createRichPresence(GlobalPreferences preferences, IEditorInput input);
   }

As you can see, you have to implement 3 different methods. Their use is detailed in the JavaDoc, but I believe that the third one may benefit from additional hints.

Manage user's preferences
^^^^^^^^^^^^^^^^^^^^^^^^^

First of all, the ``createRichPresence`` is responsible of actually creating a Rich Presence that will be shown on Discord from an editor's input. Its first parameter, ``preferences``, is here because it is your responsibility to ensure that the information shown in Discord follow user's preferences. The ``GlobalPreferences`` class represents the preferences set by the user at :ref:`global scope <part-global-scope-preferences>`. If you manage to find the ``IProject`` associated to the ``IEditorInput`` you use the following snippet to get the applicable preferences (either project or global scope):

.. code-block:: java

     UserPreferences applicablePreferences = preferences.getApplicablePreferencesFor(project);

Create a new RichPresence
^^^^^^^^^^^^^^^^^^^^^^^^^

The ``ImmutableRichPresence`` class allows you to create a new Rich Presence easily. See the `official RPC documentation <https://discordapp.com/developers/docs/rich-presence/how-to#updating-presence-update-presence-payload-fields>`_ for a description of how available fields are shown on Discord.

.. hint:: If no Rich Presence can be created from the given ``IEditorInput`` (for instance in the case where the input does not provide enough information) then you should not throw any exception but rather return ``Optional.empty()`` instead.

Example
^^^^^^^^

The following snippet is extracted from `DefaultFileEditorInputRichPresence <https://github.com/echebbi/eclipse-discord-integration/blob/7504a36177ffd9eb7a84dc176b29007dbd17420e/bundles/fr.kazejiyu.discord.rpc.integration.adapters/src/fr/kazejiyu/discord/rpc/integration/adapters/DefaultFileEditorInputRichPresence.java#L51>`_, a built-in adapter, and shows how a RichPresence is created from an editor input:

.. code-block:: java
   :linenos:

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