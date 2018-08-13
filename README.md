# Discord Rich Presence for Eclipse IDE

![Build Status](https://travis-ci.org/KazeJiyu/eclipse-discord-integration.svg?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/6af5be6899274ddc8367c92bd206c281)](https://www.codacy.com/app/KazeJiyu/eclipse-discord-integration?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=KazeJiyu/eclipse-discord-integration&amp;utm_campaign=Badge_Grade) [![codecov](https://codecov.io/gh/KazeJiyu/eclipse-discord-integration/branch/master/graph/badge.svg)](https://codecov.io/gh/KazeJiyu/eclipse-discord-integration) [![Managed with TAIGA.io](https://img.shields.io/badge/managed%20with-TAIGA.io-brightgreen.svg)](https://tree.taiga.io/project/kazejiyu-eclipse-discord-integration/) [ ![Download](https://api.bintray.com/packages/kazejiyu/eclipse-discord-integration/releases/images/download.svg) ](https://bintray.com/kazejiyu/eclipse-discord-integration/releases/_latestVersion)

## Presentation

Discord Rich Presence for Eclipse IDE is a plug-in that uses [Rich Presence](https://discordapp.com/rich-presence) in order to display information related to your current work in Discord.

Here is an example of the Rich Presence Integration:

<div align="center">
  <img alt="Example of Rich Presence Integration" src="https://github.com/KazeJiyu/fr.kazejiyu.io/blob/master/repos/eclipse-discord-integration/rich-presence-screenshot.jpg"/>
</div>

> **!** Please [take a look at the Wiki](https://github.com/KazeJiyu/eclipse-discord-integration/wiki) for further information about installation, use and customizations.

## Installation

The plug-in can be installed from the following update site:

- [https://dl.bintray.com/kazejiyu/eclipse-discord-integration/updates/](https://dl.bintray.com/kazejiyu/eclipse-discord-integration/updates/)

To use it from Eclipse IDE, click on `Help` > `Install new software...` and then paste the above URL.

> **Note**: version 0.y.z should be considered as beta. The 1.0.0 release will be deployed once the plug-in is proved stable.

## Supported OS

 - [x] Windows
 - [x] Linux
 - [x] macOS

## Troubleshooting

### Discord does not detect Eclipse IDE as a game

If Discord is not showing anything, ensure that Eclipse IDE is detected as a game. To this end, open Discord `User Settings` then go the `Games` tab. Click on the `Add it!` button and then select Eclipse IDE.

### Discord takes some time to update

Discord is updated each time the current selection in Eclipse IDE changes. However, Discord can take up to 15s to update. Unfortunaly, this setting is server-side: Discord limits how fast the Rich Presence can be updated, and we have no way to change this.

## Deactivate Discord Rich Presence

Once the plug-in is installed, Discord Rich Presence is automatically started on Eclipse startup. As a result, Discord displays **Playing Eclipse IDE** as a status message as soon as Eclipse is opened. This behavior may not be desirable.

Currently, there is no option to prevent this once the plug-in is started. However, it is possible to deactivate it: this way, the plug-in won't be launched at Eclipse startup. To this end:

1. Open Eclipse Preferences (`Window` > `Preferences`)
2. Open the _Startup and Shutdown_ page (`General` > `Startup and Shutdown`)
3. Uncheck _Discord Rich Presence for Eclipse IDE_
4. Reboot Eclipse IDE

From that time on, Discord won't be notified anymore by Eclipse IDE. In order to re-activate Rich Presence, follow the steps above and check _Eclipse Discord Integration_ again.

## Work in Progress

**Disclaimer**: The work is still in progress. The code will also be refactored and the API is likely to change.

## Thanks

- [MinnDevelopment](https://github.com/MinnDevelopment) for [its Java implementation](https://github.com/MinnDevelopment/java-discord-rpc) of Discord RPC.
- [Lorenzo Bettini](https://github.com/LorenzoBettini) for his explanations on [how to publish a p2 composite repository on Bintray](http://www.lorenzobettini.it/2016/02/publish-an-eclipse-p2-composite-repository-on-bintray/).
 - [HelloWorld017](https://github.com/HelloWorld017/) for [his adaptation](https://github.com/HelloWorld017/fileicons-render) of [programming language icons](https://github.com/file-icons/atom). These icons are displayed on Discord according to the programming language of the current file.
