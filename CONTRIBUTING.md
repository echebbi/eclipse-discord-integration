# How to contribute <!-- omit in toc -->

- [What can I do?](#what-can-i-do)
- [How should I contribute?](#how-should-i-contribute)
- [How is the plug-in architected?](#how-is-the-plug-in-architected)
- [How do I make changes?](#how-do-i-make-changes)
  - [What is the coding policy?](#what-is-the-coding-policy)
  - [How do I create a new plug-in?](#how-do-i-create-a-new-plug-in)
  - [How do I create tests for a new plug-in?](#how-do-i-create-tests-for-a-new-plug-in)
- [How can I check that my changes are consistent with the codebase?](#how-can-i-check-that-my-changes-are-consistent-with-the-codebase)
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

The whole source code is located under the `bundles/` directory:

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

### What is the coding policy?

To ensure a good code quality:
 - all the features must be tested
 - all the public API must be documented

### How do I create a new plug-in?

A source plug-in should be created in the `bundles/` directory.

Do not forget to add the corresponding module in the `bundles/pom.xml` file, otherwise the plug-in will be ignored by Maven.

### How do I create tests for a new plug-in?

Tests are hosted in _fragments_ located under the `tests/` directory. By convention, a fragment's name is the name of the tested plug-in suffixed with `.tests`.

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