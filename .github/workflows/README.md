# GitHub Workflows

This directory contains GitHub Actions workflows that automate various tasks for the Project-Anti repository.

## Workflows Overview

### Android CI (`android.yml`)
- **Purpose**: Main CI workflow for building and testing the Android app
- **Triggers**: On push to main branch, pull requests to main branch
- **Key Features**:
  - Builds the project with Gradle
  - Runs lint checks
  - Runs unit tests
  - Runs instrumented tests on an Android emulator
  - Uploads test results and build artifacts

### Auto Assign and Fix (`auto-assign-fix.yml`)
- **Purpose**: Automatically assigns issues/PRs and fixes code style issues
- **Triggers**: On issue/PR creation, reopening, or manual trigger
- **Key Features**:
  - Auto-assigns issues and PRs to specified users
  - Runs ktlint to fix Kotlin code style issues
  - Runs Android Lint with auto-fix
  - Updates Gradle dependencies
  - Organizes imports
  - Fixes XML string resources
  - Fixes indentation and whitespace issues
  - Commits and pushes changes back to the PR branch

### Auto Merge PRs (`auto-merge.yml`)
- **Purpose**: Automatically merges PRs that meet certain criteria
- **Triggers**: On PR events, check suite completion, workflow run completion
- **Key Features**:
  - Merges PRs with the "auto-merge" label
  - Merges Dependabot PRs
  - Requires all checks to pass before merging
  - Uses squash merging
  - Provides detailed logging

### Auto Delete Branch (`auto-delete-branch.yml`)
- **Purpose**: Automatically deletes branches after PRs are merged
- **Triggers**: On PR close events
- **Key Features**:
  - Deletes the branch associated with a merged PR
  - Helps keep the repository clean

### Self-Updating Workflows (`self-update.yml`)
- **Purpose**: Keeps GitHub Actions versions up to date
- **Triggers**: Weekly schedule, manual trigger
- **Key Features**:
  - Checks for newer versions of GitHub Actions
  - Updates workflow files with the latest versions
  - Creates a PR with the changes
  - Labels the PR for auto-merging

### GitHub Repository Sync (`github-sync.yml`)
- **Purpose**: Keeps GitHub repository settings in sync
- **Triggers**: Weekly schedule, manual trigger
- **Key Features**:
  - Enables vulnerability alerts
  - Enables automated security fixes
  - Updates branch protection rules
  - Synchronizes repository labels
  - Updates GitHub Pages settings
  - Creates a PR with a report of changes

### Code Quality (`code-quality.yml`)
- **Purpose**: Runs code quality checks
- **Triggers**: On PR events
- **Key Features**:
  - Runs static code analysis
  - Checks code formatting
  - Reports issues as PR comments

### Performance (`performance.yml`)
- **Purpose**: Runs performance tests
- **Triggers**: On PR events
- **Key Features**:
  - Runs performance benchmarks
  - Compares results with baseline
  - Reports performance regressions

### Release (`release.yml`)
- **Purpose**: Creates releases
- **Triggers**: On tag creation
- **Key Features**:
  - Builds release APK
  - Creates GitHub release
  - Uploads artifacts

### Security Scan (`security-scan.yml`)
- **Purpose**: Runs security scans
- **Triggers**: On PR events, schedule
- **Key Features**:
  - Runs dependency vulnerability scans
  - Runs code security analysis
  - Reports security issues

## Dependency Management

The repository uses Dependabot for automated dependency updates. The configuration is in `.github/dependabot.yml`.

- **GitHub Actions**: Updates GitHub Actions dependencies weekly
- **Gradle**: Updates Gradle dependencies weekly
- **Auto-merging**: Dependencies with the "auto-merge" label are automatically merged if tests pass

## Workflow Maintenance

To manually trigger any workflow that supports it, go to the Actions tab in GitHub, select the workflow, and click "Run workflow".

For any issues or questions about these workflows, please open an issue in the repository.
