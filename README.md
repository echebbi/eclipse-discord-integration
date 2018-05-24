# Discord Rich Presence for Eclipse IDE

![Build Status](https://travis-ci.org/KazeJiyu/eclipse-discord-integration.svg?branch=master) [![Managed with TAIGA.io](https://img.shields.io/badge/managed%20with-TAIGA.io-brightgreen.svg)](https://tree.taiga.io/project/kazejiyu-eclipse-discord-integration/)

## Presentation

Discord Rich Presence for Eclipse IDE is a plug-in that uses [Rich Presence](https://discordapp.com/rich-presence) in order to display information related to your current work in Discord.

Here is an example of the Rich Presence Integration:

<div align="center">
  <img alt="Example of Rich Presence Integration" src="https://github.com/KazeJiyu/fr.kazejiyu.io/blob/master/repos/eclipse-discord-integration/rich-presence-screenshot.jpg"/>
</div>

## Installation

The plug-in can be installed from the following update site:

- [https://dl.bintray.com/kazejiyu/eclipse-discord-integration/updates/](https://dl.bintray.com/kazejiyu/eclipse-discord-integration/updates/)

To use it from Eclipse IDE, click on `Help` > `Install new software...` and then paste the above URL.

**Note**: version 0.y.z should be considered as beta. The 1.0.0 release will be deployed once the plug-in is proved stable.

## Supported OS

 - [x] Windows
 - [x] Linux
 - [ ] macOS
 
 The [library currently used to send Discord RPC information](https://github.com/PSNRigner/discord-rpc-java) does not support macOS yet. I am working on a workaround.

## Troubleshooting

### Discord does not detect Eclipse IDE as a game

If Discord is not showing anything, ensure that Eclipse IDE is detected as a game. To this end, open Discord `User Settings` then go the `Games` tab. Click on the `Add it!` button and then select Eclipse IDE.

### Discord takes some time to update

Discord is updated each time the current selection in Eclipse IDE changes. However, Discord can take up to 15s to update. Unfortunaly, this setting is server-side: Discord limits how fast the Rich Presence can be updated, and we have no way to change this.

## Work in Progress

**Disclaimer**: The work is still in progress and the plug-in is not available yet. The code will also be refactored and the API is likely to change.

## Thanks

- [PSNRigner](https://github.com/PSNRigner) for [its Java implementation](https://github.com/PSNRigner/discord-rpc-java) of Discord RPC.
- [Lorenzo Bettini](https://github.com/LorenzoBettini) for his explanations on [how to publish a p2 composite repository on Bintray](http://www.lorenzobettini.it/2016/02/publish-an-eclipse-p2-composite-repository-on-bintray/).
