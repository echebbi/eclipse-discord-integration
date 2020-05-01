<h1 align="center">
  Discord Rich Presence for Eclipse IDE
</h1>
<p align="center">
	<i>Show the world the projects you're working on!</i>
</p>

<div align="center">

[![Build Status](https://travis-ci.org/echebbi/eclipse-discord-integration.svg?branch=master)](https://travis-ci.org/echebbi/eclipse-discord-integration) [![Documentation Status](https://readthedocs.org/projects/discord-rich-presence-for-eclipse-ide/badge/?version=latest)](https://discord-rich-presence-for-eclipse-ide.readthedocs.io/en/latest/?badge=latest) <a href="https://dl.bintray.com/kazejiyu/eclipse-discord-integration/updates/"><img alt="p2 update site" src="https://img.shields.io/website?label=p2&logo=p2%20update%20site&up_message=available&url=https:%2F%2Fdl.bintray.com%2Fkazejiyu%2Feclipse-discord-integration%2Fupdates%2F"></a> [![Eclipse Marketplace](https://img.shields.io/badge/Eclipse-Marketplace-blue.svg?longCache=true&style=flat&logo=eclipse)](https://marketplace.eclipse.org/content/discord-rich-presence-eclipse-ide)
<br/>
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=fr.kazejiyu.discord.rpc.integration%3Afr.kazejiyu.discord.rpc.integration.root&metric=sqale_index)](https://sonarcloud.io/dashboard?id=fr.kazejiyu.discord.rpc.integration%3Afr.kazejiyu.discord.rpc.integration.root) [![codecov](https://codecov.io/gh/echebbi/eclipse-discord-integration/branch/master/graph/badge.svg)](https://codecov.io/gh/echebbi/eclipse-discord-integration)

</div>

<div align="center">
  <img alt="Example of Rich Presence Integration" src="docs/images/rich-presence-examples.gif"/>
</div>

<br/>

_Discord Rich Presence for Eclipse IDE_ is a plug-in that uses [Rich Presence](https://discordapp.com/rich-presence) in order to display information related to your current work in Discord.

## Main Features:

<p>
  	<ul>
	  	<li align="justify"><strong>Exposure</strong>: have a great project? <a href="#Installation">Show it to the world!</a></li>
	  	<li align="justify"><strong>Customization</strong>: don't like the wording or the icons? <a href="https://discord-rich-presence-for-eclipse-ide.readthedocs.io/en/latest/customize/change-wording.html">Provide your own!</a></li>
	  	<li align="justify"><strong>Extensibility</strong>: have a custom Eclipse editor? <a href="https://discord-rich-presence-for-eclipse-ide.readthedocs.io/en/latest/extend/support-new-editors.html"/>We can support it!</a></li>
		  <li align="justify"><strong>Privacy</strong>: working on a secret project? <a href="https://discord-rich-presence-for-eclipse-ide.readthedocs.io/en/latest/customize/hide-information.html">Show only what you want!</a></li>
	</ul>
</p>

Please [take a look at the documentation](https://discord-rich-presence-for-eclipse-ide.readthedocs.io/en/latest/) for further information about installation, use and customizations.

## Usage

Discord will automatically be notified as soon as the plug-in is installed. The plug-in can be deactivated from the Eclipse's Preferences (`Window` > `Preferences` > `Discord Rich Presence` > Uncheck _Activate Rich Presence Integration_).

## Installation

The plug-in is available in the [Eclipse Marketplace](https://marketplace.eclipse.org/content/discord-rich-presence-eclipse-ide).

Drag the following button to your running Eclipse workspace to start the installation:
<div align="center">
  <a href="http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=4240174" class="drag" title="Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client"><img typeof="foaf:Image" class="img-responsive" src="https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.png" alt="Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client" /></a>
</div>

<details>
  <summary><b>Or show how to install it manually</b></summary>

  1. Open Eclipse IDE
  2. Go to *Help > Install New Software...*
  3. Copy the update site’s URL in the *Work with* textbox:
     	- https://dl.bintray.com/kazejiyu/eclipse-discord-integration/updates/
  4. Hit *Enter* and wait for the list to load
  5. Check *Action Language for EMF*
  6. Click *Next* then *Finish*
</details>

## Troubleshooting

### Discord does not detect Eclipse IDE as a game

If Discord is not showing anything, ensure that Eclipse IDE is detected as a game:

1. Open Discord's `User Settings`
2. Go the `Games` tab
3. Click on the `Add it!` button
4. Select Eclipse IDE

### A 'Workbench early startup error' occurs since the plug-in is installed

In case you encounter the following error on startup:
```
An internal error occurred during: "Workbench early startup".

There is an incompatible JNA native library installed on this system
Expected: 5.1.0
Found: 4.0.1
```
modify the _eclipse.ini_ file (located next to _eclipse.exe_) to set the `jna.nosys` property to true:
```
-vmargs
-Djna.nosys=true
```

> :information_source: The `-vmargs` line should already exist, otherwise you can append it at the end of the file.

## Changelog

See [CHANGELOG.md](CHANGELOG.md).

## Contributing

<details>
  <summary><b>Setup your dev environment</b></summary>

  1. Download the latest _[Eclipse IDE for RCP Developers](https://www.eclipse.org/downloads/packages/)_ release
  2. Clone the repository `git clone https://github.com/echebbi/eclipse-discord-integration.git`
  3. Import all projects in Eclipse IDE
  4. Open the `releng/*.target/*.target` file
  5. Click on *Set as Target Platform*
  6. Wait for the dependencies to be loaded
</details>

<details>
  <summary><b>Technical documentation</b></summary>

  <br/>
  &emsp;&emsp;Plug-in's architecture is presented in [CONTRIBUTING.md/how-is-the-plug-in-architected](https://github.com/echebbi/eclipse-discord-integration/blob/master/CONTRIBUTING.md#how-is-the-plug-in-architected).
</details>

See [CONTRIBUTING.md](CONTRIBUTING.md) for further details.

## License

The Discord Rich Presence for Eclipse IDE plug-in is licensed under the [Eclipse Public License 2.0](https://www.eclipse.org/legal/epl-2.0/).

It uses different third-party components which are licensed under:
- the Apache Public License 2.0
  - [java-discord-rpc](https://github.com/MinnDevelopment/java-discord-rpc) Copyright (c) 2016 - 2019 Florian Spieß and the java-discord-rpc contributors
- the MIT License (MIT)
  - [fileicons-render](https://github.com/HelloWorld017/fileicons-render) Copyright (c) 2014

## Thanks

- [MinnDevelopment](https://github.com/MinnDevelopment) for [his Java implementation](https://github.com/MinnDevelopment/java-discord-rpc) of Discord RPC.
- [Lorenzo Bettini](https://github.com/LorenzoBettini) for his explanations on [how to publish a p2 composite repository on Bintray](http://www.lorenzobettini.it/2016/02/publish-an-eclipse-p2-composite-repository-on-bintray/).
 - [HelloWorld017](https://github.com/HelloWorld017/) for [his adaptation](https://github.com/HelloWorld017/fileicons-render) of [programming language icons](https://github.com/file-icons/atom). These icons are displayed on Discord according to the programming language of the current file.
- The [Eclipse Foundation](https://www.eclipse.org/org/foundation/) for allowing me to display the logo of the Eclipse IDE in Discord's status.