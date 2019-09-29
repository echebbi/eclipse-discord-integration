A 'Workbench early startup error' occurs since the plug-in is installed
=======================================================================

In case you encounter the following error on startup:

.. code-block::

   An internal error occurred during: "Workbench early startup".

   There is an incompatible JNA native library installed on this system
   Expected: 5.1.0
   Found: 4.0.1

you can try to modify the *eclipse.ini* file which is located next to *eclipse.exe* (within Eclipse IDE's installation directory) in order to set the ``jna.nosys`` property to true as follows:

.. code-block::

   -vmargs
   -Djna.nosys=true

.. note:: The ``-vmargs`` line should already exist, otherwise you can append it at the end of the file.