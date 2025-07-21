# Dependency Management

This document explains how dependency management is handled in this project using Renovate Bot.

## Overview

This project uses [Renovate Bot](https://docs.renovatebot.com/) to automatically manage dependency updates. Renovate creates a Dependency Dashboard issue that tracks all available updates and provides a centralized view of the project's dependencies.

## Dependency Dashboard

The Dependency Dashboard is automatically created and maintained by Renovate. It provides:

- **Config Migration Needed**: Notifications when Renovate configuration needs to be updated
- **Awaiting Schedule**: Updates that are scheduled but not yet applied
- **Detected Dependencies**: A comprehensive list of all dependencies found in the project

## Configuration

The Renovate configuration is stored in `renovate.json` and includes:

### Package Rules

- **Auto-merge**: Minor and patch updates are automatically merged for stable versions
- **Grouping**: Related dependencies are grouped together (e.g., AndroidX, Gradle, Kotlin)
- **Labeling**: Automatic labeling for different types of dependencies

### Special Handling

- **libsuperuser**: This dependency is disabled from automatic updates as it's not available in standard Maven repositories
- **Shizuku**: Grouped together for easier management

### Schedule

Updates are scheduled to run before 3am on Monday to minimize disruption during development.

## Dependency Categories

### Android Dependencies
- AndroidX libraries (AppCompat, Material, ConstraintLayout, etc.)
- Compose BOM and related libraries
- Testing libraries (JUnit, Espresso, Mockito)

### Build Tools
- Gradle and plugins
- Kotlin compiler and standard library
- Ktlint for code formatting

### Third-party Libraries
- Shizuku for privileged operations
- Gson for JSON processing
- libsuperuser for root operations (manually managed)

## Manual Actions

### Triggering Updates

To manually trigger specific updates:
1. Go to the Dependency Dashboard issue
2. Check the checkbox next to the desired update
3. Renovate will create a PR for that update

### Handling Failed Dependencies

If Renovate fails to look up a dependency (like libsuperuser):
1. The dependency is listed in the warning section
2. Consider adding alternative repositories to `settings.gradle`
3. Or disable the dependency in `renovate.json` if it's not available in public repositories

## Best Practices

1. **Review PRs**: Always review dependency update PRs before merging
2. **Test Changes**: Run tests to ensure updates don't break functionality
3. **Monitor Dashboard**: Regularly check the Dependency Dashboard for important updates
4. **Security Updates**: Prioritize security-related dependency updates
