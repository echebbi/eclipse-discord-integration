# How to contribute <!-- omit in toc -->

- [What can I do?](#what-can-i-do)
- [How should I contribute?](#how-should-i-contribute)
- [How is the plug-in architected?](#how-is-the-plug-in-architected)
- [How do I make changes?](#how-do-i-make-changes)
  - [Which IDE should I use?](#which-ide-should-i-use)
  - [How do I open the projects in Eclipse IDE?](#how-do-i-open-the-projects-in-eclipse-ide)
  - [What is the coding policy?](#what-is-the-coding-policy)
  - [How do I create a new plug-in?](#how-do-i-create-a-new-plug-in)
  - [How do I create tests for a new plug-in?](#how-do-i-create-tests-for-a-new-plug-in)
- [How can I check that my changes are consistent with the codebase?](#how-can-i-check-that-my-changes-are-consistent-with-the-codebase)
- [How can I check that the tests still pass?](#how-can-i-check-that-the-tests-still-pass)
- [How can I manually test the plug-in?](#how-can-i-manually-test-the-plug-in)
- [How should I commit my changes?](#how-should-i-commit-my-changes)
  - [Commit message guidelines](#commit-message-guidelines)
- [Commit history](#commit-history)

## What can I do?

If you want to contribute by writing code, feel free to pick an open issue or submit a new feature.

Otherwise, you can create an issue to report a bug, ask for a new feature or for more documentation. This is appreciated too!

> **Note**: if you want to solve an open issue, please leave a comment on the corresponding thread so that we know that someone is working on it.

## How should I contribute?

1. Fork the project
2. Create a new branch with a meaningful name
3. Commit your changes
4. Submit a PR

When a PR is created, it is automatically analyzed by the CI tools to ensure a good code quality:
- [Travis CI](https://travis-ci.org/echebbi/eclipse-discord-integration)
- [SonarCloud](https://sonarcloud.io/dashboard?id=fr.kazejiyu.discord.rpc.integration%3Afr.kazejiyu.discord.rpc.integration.root)
- [CodeCov](https://codecov.io/gh/echebbi/eclipse-discord-integration)

## How is the plug-in architected?

The Discord Rich Presence plug-in is made of several Eclipse bundles. Each bundle is implemented as an Eclipse project and is available in the `bundles/` directory:

Bundle | Purpose
------ | -------
`fr.kazejiyu.discord.rpc.integration` | Plug-in's core. Listens for new selection and updates Discord accordingly.
`fr.kazejiyu.discord.rpc.integration.adapters` | Provides default adapters used to extract information about the current editor in order to show them in Discord.
`fr.kazejiyu.discord.rpc.integration.ui.preferences` | Provides UI integration allowing user to change plug-in's preferences.
`java-discord-rpc` | Provides the java-discord-rpc library to other plug-ins

The entry point of the plug-in is the `Activator` class which is located in the `integration` bundle. Its `start` method is called on Eclipse IDE startup and is responsible of:
 - initializing the connection with Discord
 - setting up listeners in order to be notified when a new file is opened by the user.

## How do I make changes?

### Which IDE should I use?

The [Eclipse IDE for RCP developers](https://www.eclipse.org/downloads/packages/) is the preferred IDE to develop Eclipse plug-ins.

### How do I open the projects in Eclipse IDE?

The projects can be imported from Eclipse IDE:

1. `File` > `Import...`
2. `Existing Projects into Workspace`
3. Type to the path of the `bundles` directory in the `Select root directory` field
4. Check all projects
5. `Finish`

Wait for all projects to be imported. Depending on the Eclipse package you are using many errors may come up: some projects' dependencies may be missing. To solve this, we have to tell Eclipse IDE to use a specific environment. This is achieve thanks to a [target platform](https://www.vogella.com/tutorials/EclipseTargetPlatform/article.html). One is provided in the `releng` directory; to use it:

1. `File` > `Import...`
2. `Existing Projects into Workspace`
3. Type to the path of the `releng` directory in the `Select root directory` field
4. Check only the `fr.kazejiyu.discord.rpc.integration.target` project
5. `Finish`
6. Open the `fr.kazejiyu.discord.rpc.integration.target.target` file
7. On the top-right corner of the editor, click on _Set as Active Target Platform_.

Eclipse IDE starts downloading all the dependencies, which may take some time. The process can be followed thanks to the _Progress_ view.

If the compilation errors do not disappear even after the target platform is set, a full rebuild should fix the issue:
1. `Project` > `Clean...`
2. Check `Clean all projects`
3. Click on `Click`

> **Note**: in some rare cases errors still remain. In such a case, opening the `fr.kazejiyu.discord.rpc.integration.target.target` file and clicking on _Reload target platform_ or restarting Eclipse IDE should definitely fix them.

### What is the coding policy?

To ensure a good code quality:
 - all the features must be tested
 - all the public API must be documented

### How do I create a new plug-in?

A source plug-in should be created in the `bundles/` directory. A new plug-in project can be created as follows:

1. `File` > `New` > `Other...`
2. Select `Plug-in Project`
3. Type the name of the plug-in and change its default location
4. Click `Finish`

> **Caution**: do not forget to add the corresponding module in the `bundles/pom.xml` file, otherwise the plug-in will be ignored by Maven.

### How do I create tests for a new plug-in?

Tests are hosted in _fragments_ located under the `tests/` directory. By convention, a fragment's name is the name of the tested plug-in suffixed with `.tests`. A new fragment project can be created as follows:

1. `File` > `New` > `Other...`
2. Select `Fragment Project`
3. Type the name of the fragment and change its default location
4. Click `Next` then select the host plug-in (the one that contains the code to test)
4. Click `Finish`

In order to include the tests in Maven build, the following steps are required:
1. Add the corresponding module to the `tests/pom.xml` file
2. Add the source and test modules to the `fr.kazejiyu.discord.rpc.integration.tests.report/pom.xml` file.

Tests should be written with JUnit 5 and AssertJ. Please take a look at existing tests and make yours consistent.

## How can I check that my changes are consistent with the codebase?

You can check the code with the following command:
```
mvn clean verify
```

It checks that:
 - the code compiles
 - all the tests pass
 - the code style is consistent

Code style is enforced by Checkstyle. You may need to install the [corresponding Eclipse IDE plug-in](https://checkstyle.org/eclipse-cs/#!/). The plug-in is not able to catch everything, so please try to keep the style consistent.

> **Boy Scout Rule**: Leave your code better than you found it

## How can I check that the tests still pass?

Tests can be run from Maven with the following command:
```
mvn clean verify
```

Alternatively, tests can be run from Eclipse:
1. Right-click on a test project or a test class
2. `Run As` > `JUnit Plug-in Test`

A new Eclipse window should open during the time of the tests, before closing automatically.

## How can I manually test the plug-in?

Manual tests are still useful for prototyping, especially since UI tests are not implemented yet.

To open a new Eclipse IDE instance that uses the plug-in under development:

1. Right-click on a project
2. `Run As` > `Eclipse application`

A new Eclipse IDE window should open, in which new projects can be created for testing purposes.

## How should I commit my changes?

### Commit message guidelines

A commit message is usually made of two sections:
```
<subject>

<details>
```
The subject must:
 - be a one-liner
 - start with a capital letter
 - use the imperative, present tense

The details must:
 - be separated with the subject by a blank line

For instance:
```
Close Discord connection on workbench shutdown

Add an IWorkbenchListener ensuring that the connection with Discord
is properly closed when Eclipse IDE is shut down.
```

## Commit history

During development it is advised to commit as often as possible. However, in order to keep the Git history clean please, when possible, squash your commits before submitting the PR.
