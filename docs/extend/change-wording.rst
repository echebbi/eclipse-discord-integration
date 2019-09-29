Change wordings
#############################

.. important:: This section requires some knowledge about `Eclipse RCP development <https://www.vogella.com/tutorials/EclipseRCP/article.html>`_ and `Eclipse Extension Points <https://www.vogella.com/tutorials/EclipseExtensionPoint/article.html>`_.

    You must also have read :ref:`part-support-new-editors`.

Motivation
----------

By default, information is displayed on Discord using the following format:

.. code-block::

    Editing <file_name>
    Working on <project_name>

It is very simple but maybe that it does not fulfil your needs. Read below to learn how to change it.

Implementation
--------------

I plan to add preferences allowing one to customize these default wordings easily from the Eclipse IDE (see `issue #71 <https://github.com/echebbi/eclipse-discord-integration/issues/71>`_) but it will take some time so in the meantime you'll have to get your hands a little dirty.

In order to provide your own wording you have to create two ``EditorInputToRichPresenceAdapter`` as explained in :ref:`part-support-new-editors`.

These adapters must:

- expect editor input which are respectively instances of ``IFileEditorInput`` and ``FileStoreEditorInput``,
- have a priority greater than 0 in order to be favoured over built-in ones.

You can now implement the ``createRichPresence`` method as you wish to create a Rich Presence that follows your own wording.

.. tip:: I advise you to create those adapters by copying the code of:

    - `DefaultFileEditorInputRichPresence <https://github.com/echebbi/eclipse-discord-integration/blob/master/bundles/fr.kazejiyu.discord.rpc.integration.adapters/src/fr/kazejiyu/discord/rpc/integration/adapters/DefaultFileEditorInputRichPresence.java>`_
    - `DefaultURIEditorInputRichPresence <https://github.com/echebbi/eclipse-discord-integration/blob/master/bundles/fr.kazejiyu.discord.rpc.integration.adapters/src/fr/kazejiyu/discord/rpc/integration/adapters/DefaultURIEditorInputRichPresence.java>`_