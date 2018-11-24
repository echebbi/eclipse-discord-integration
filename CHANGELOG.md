# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.0.1] - 2018-11-24
### Fixed
- Properly close connection with Discord on workbench shutdown

## [1.0.0] - 2018-08-23
### Added
- Allow to deactivate the Rich Presence integration
- Handle the C language (_*.c_ and _*.h_ files)
- First deployment on the [Eclipse Marketplace](https://marketplace.eclipse.org/content/discord-rich-presence-eclipse-ide)

### Fixed
- Prevent elapsed time from not showing at startup when set on file change

## [0.8.4] - 2018-07-01
### Fixed
- Make the plug-in available on macOS
- Log errors in Eclipse "Error Log" views

## [0.8.3] - 2018-05-29
### Added
- Make possible to change the name displayed in Discord for a given project
- Update plug-in's name to "Discord Rich Presence for Eclipse IDE"

## [0.8.2] - 2018-05-26
### Fixed
- Make the plug-in available from Eclipse Mars (4.5)

## [0.8.1] - 2018-05-25
### Fixed
- Settings were not always shown in projet Properties

### Added
- Explanations about how to install the plug-in

## [0.8.0] - 2018-05-08
### Added 
- Notify Discord of file change on Windows and GNU/Linux
- Provide settings in Eclipse Preferences to tailor integration
- Provide settings in projet Properties to tailor integration per project
- Automate deployment to Bintray with Maven