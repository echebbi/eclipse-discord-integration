# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.1.1] - 2019-09-30
- [#72](https://github.com/echebbi/eclipse-discord-integration/pull/72) Add link to the documentation in Preferences and Project Settings pages

## [1.1.0] - 2019-09-27
- [#66](https://github.com/echebbi/eclipse-discord-integration/issues/66) Allow users to customize icons shown on Discord

## [1.0.3] - 2019-02-26
### Fixed
- [#56](https://github.com/echebbi/eclipse-discord-integration/issues/56) Prevent the plug-in from not working in some environments

## [1.0.1] - 2018-11-24
### Fixed
- [#55](https://github.com/echebbi/eclipse-discord-integration/issues/55) Properly close connection with Discord on workbench shutdown

## [1.0.0] - 2018-08-23
### Added
- [#50](https://github.com/echebbi/eclipse-discord-integration/pull/50) Allow to deactivate the Rich Presence integration
- [#52](https://github.com/echebbi/eclipse-discord-integration/pull/52) Handle the C language (_*.c_ and _*.h_ files)
- [#54](https://github.com/echebbi/eclipse-discord-integration/pull/54) First deployment on the [Eclipse Marketplace](https://marketplace.eclipse.org/content/discord-rich-presence-eclipse-ide)

### Fixed
- Prevent elapsed time from not showing at startup when set on file change

## [0.8.4] - 2018-07-01
### Added
- [#34](https://github.com/echebbi/eclipse-discord-integration/pull/34) Log errors in Eclipse "Error Log" views

### Fixed
- [#26](https://github.com/echebbi/eclipse-discord-integration/issues/26) Make the plug-in available on macOS

## [0.8.3] - 2018-05-29
### Added
- [#32](https://github.com/echebbi/eclipse-discord-integration/pull/32) Make possible to change the name displayed in Discord for a given project
- Update plug-in's name to "Discord Rich Presence for Eclipse IDE"

## [0.8.2] - 2018-05-26
### Fixed
- [#29](https://github.com/echebbi/eclipse-discord-integration/issues/29) Make the plug-in available from Eclipse Mars (4.5)

## [0.8.1] - 2018-05-25
### Fixed
- [#23](https://github.com/echebbi/eclipse-discord-integration/issues/23) Settings were not always shown in projet Properties

### Added
- Explanations about how to install the plug-in

## [0.8.0] - 2018-05-08
### Added
- Notify Discord of file change on Windows and GNU/Linux
- Provide settings in Eclipse Preferences to tailor integration
- Provide settings in projet Properties to tailor integration per project
- Automate deployment to Bintray with Maven