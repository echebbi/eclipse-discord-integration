Deactivate the plug-in
======================

Once the plug-in is installed, Discord Rich Presence is automatically started on Eclipse startup. As a result, Discord displays **Playing Eclipse IDE** as a status message as soon as Eclipse IDE is opened. This behavior may not be desirable but can be easily deactivated.

Deactivate occasionally
-----------------------

The plug-in provides a preference to deactivate its behavior:

1. Open Eclipse Preferences (``Window`` > ``Preferences``)
2. Open the plug-in's page (``Discord Rich Presence``)
3. Uncheck *Activate Rich Presence Integration*
4. Click on *Apply*

From that time on, Discord won't be notified anymore by Eclipse IDE. In order to re-activate Rich Presence, follow the steps above and check *Activate Rich Presence Integration* again.

Deactive the plug-in for a long time
------------------------------------

While convenient, the previous method is not really optimized. Indeed, even if no Rich Presence is shown on Discord the plug-in is still activated and is notified each time a new editor opens, which may cause a little overhead.

Follow the steps below to completely deactivate the plug-in:

1. Open Eclipse Preferences (``Window`` > ``Preferences``)
2. Open the *Startup and Shutdown* page (``General`` > ``Startup and Shutdown``)
3. Uncheck *Discord Rich Presence for Eclipse IDE*
4. Reboot Eclipse IDE

From that time on, Discord won't be notified anymore by Eclipse IDE. In order to re-activate Rich Presence, follow the steps above and check *Discord Rich Presence for Eclipse IDE* again then reboot the IDE.