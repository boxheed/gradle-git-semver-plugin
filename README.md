# Gradle Git Semver Plugin

## Introduction

The `gradle-git-semver-plugin` is a Gradle plugin designed to manage semantic versioning using Git tags. It automates the process of versioning your project based on the Git history, ensuring that your project versions are consistent and follow the semantic versioning specification.

## Installation

To apply the `gradle-git-semver-plugin`, add the following to your `build.gradle` file:

```groovy
plugins {
    id 'com.fizzpod.gradle-git-semver-plugin' version '24.0.0'
}
```

## Configuration

Configure the plugin in your `build.gradle` file using the following options:

```groovy
gitSemver {
    version = "latest" // The version of git-semver to use
    location = ".git-semver" // Directory where git-semver is installed
    repository = "PSanetra/git-semver" // Repository to download git-semver from
    prefix = "v" // Prefix for version tags
    os = null // Operating system for which to install git-semver
    arch = null // Architecture for which to install git-semver
    flags = "" // Additional flags for git-semver
    binary = "" // Path to the git-semver binary
    snapshotSuffix = "-SNAPSHOT" // Suffix for snapshot versions
    ttl = 1000 * 60 * 60 * 24 // Time-to-live for the binary in milliseconds
    stable = true // Whether to use stable versions
}
```

## Usage Examples

To use the plugin, simply run the following Gradle tasks:

- To print the current version:

  ```bash
  ./gradlew semver
  ```
- To create a new version tag:

  ```bash
  ./gradlew tagSemver
  ```

## Available Tasks

- **semver**: Retrieves the current semantic version of the project based on the Git tags.

- **nextSemver**: Calculates the next semantic version based on the current version and the commit history.

- **gitStatus**: Provides the status of the current versioning, showing any changes that might affect the version.

- **tagSemver**: Creates a new Git tag for the current version, effectively marking a release point in the repository.

- **installSemver**: Installs the current version of the project, potentially preparing it for deployment.

- **installAllSemvers**: Installs all components of the project, ensuring everything is up-to-date and ready for use.

## License Information

This project is licensed under the Apache-2 Licence. See the LICENSE file for more details.
