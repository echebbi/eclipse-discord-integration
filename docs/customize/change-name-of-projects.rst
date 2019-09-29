Change displayed project names
===============================

By default the plug-in shows on Discord the name of the Eclipse project. However, it may happen that this name is not really accurate. This may happen, for example, when the different Eclipse projects are sub-modules of a bigger one. In such a case you may want to provide a custom name that will be used instead.

The name shown for a given Eclipse project can be changed in the project's preferences page:

1. Right-click on the project > ``Properties``
2. Click on ``Discord Rich Presence``
3. Look at the ``Display`` section
4. Type a name in ``Name displayed for the project`` text field
5. Click on ``Apply``

.. note:: This preference is the only one that is taken into account even if the ``Use project settings`` preference is unchecked. This allows you to customize a project's name while benefiting from global preferences.