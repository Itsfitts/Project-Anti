# Project-Anti: ADB Functionality

A tool that provides convenient access to Android Debug Bridge (ADB) features directly from your device.

## 🛠️ Usage
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/Project-Anti.git
   ```
2. **Open in Android Studio**:
   - Launch Android Studio and select "Open an existing project."
   - Navigate to the cloned repository and open it.
3. **Build the Project**:
   - Allow Android Studio to sync and build the project.
4. **Run on a Device**:
   - Connect your Android device to your development machine.
   - Run the app from Android Studio.
5. **Grant Permissions**:
   - The app will request necessary permissions including accessibility service permissions.

## 🔐 Privilege Modes
This app can operate in different privilege modes:

1. **ADB Mode**: Uses standard ADB commands for device management.
2. **Shizuku Mode**: Requires the Shizuku app. Provides ADB-like functionality without a computer connection.
3. **Standard Mode**: Limited functionality with normal Android permissions.

## 📸 Screenshots
*(Coming Soon)*

## 🤝 Contributing
Contributions are welcome! If you would like to contribute to this project, please follow these steps:
1. **Fork the Repository**: Create your own fork of the project.
2. **Create a Branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. **Commit Your Changes**:
   ```bash
   git commit -m "Add your feature"
   ```
4. **Push to Your Branch**:
   ```bash
   git push origin feature/your-feature-name
   ```
5. **Open a Pull Request**: Submit a pull request with a detailed description of your changes.

This project provides a user-friendly interface for ADB functionality.
For information about specific features, please refer to internal documentation.

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.
For **educational purposes only**. Use only on devices you own or have permission to access.

## 📝 Changes

### Workflow Changes

#### Fixed Workflows

1. **Android CI Workflow (`android.yml`)**
   - Added Gradle Build Action for better caching and performance
   - Added instrumented tests on an Android emulator
   - Improved artifact handling
   - Added specific step to upload the debug APK

2. **Self-Updating Workflows (`self-update.yml`)**
   - Updated hardcoded versions to match actual versions used (v4)
   - Replaced deprecated `::set-output` with newer `$GITHUB_OUTPUT` syntax
   - Expanded the set of actions that are checked for updates
   - Added fallback values for actions that might not have releases
   - Updated Node.js version from 16 to 18
   - Updated PR description to include all actions

3. **Auto Merge PRs (`auto-merge.yml`)**
   - Added more conditions for auto-merging (requiring passing tests)
   - Added detailed logging
   - Added support for workflow run events
   - Added check to ensure all required checks have passed before merging

4. **Auto Assign and Fix (`auto-assign-fix.yml`)**
   - Fixed deprecated `::set-output` syntax

#### Removed Workflows

1. **Build Workflow (`build.yml`)**
   - Removed as its functionality is now covered by the improved `android.yml`

#### Added Workflows

1. **GitHub Repository Sync (`github-sync.yml`)**
   - Added new workflow to keep GitHub repository settings in sync
   - Features include:
     - Enabling vulnerability alerts
     - Enabling automated security fixes
     - Updating branch protection rules
     - Synchronizing repository labels
     - Updating GitHub Pages settings
     - Creating a PR with a report of changes

2. **Dependabot Configuration (`.github/dependabot.yml`)**
   - Added configuration for automated dependency updates
   - Set up for both GitHub Actions and Gradle dependencies
   - Configured to create PRs with the "auto-merge" label

#### Documentation

1. **Workflows README (`.github/workflows/README.md`)**
   - Added comprehensive documentation for all workflows
   - Includes purpose, triggers, and key features for each workflow
   - Provides guidance on dependency management and workflow maintenance

#### Testing

All workflows have been reviewed for correctness and consistency. The changes ensure that:

1. The CI workflow properly builds and tests the Android app
2. Workflows are kept up to date with the latest GitHub Actions versions
3. PRs are automatically merged when they meet the criteria
4. Code style issues are automatically fixed
5. GitHub repository settings are kept in sync
6. Dependencies are automatically updated

#### Automated Testing

The project includes a comprehensive automated testing setup:

1. **Unit Tests**: Located in `app/src/test/java/`
   - Run locally with: `./gradlew test`
   - Tests Java/Kotlin classes without Android dependencies
   - Uses JUnit, Mockito, and Robolectric

2. **Instrumented Tests**: Located in `app/src/androidTest/java/`
   - Run locally with: `./gradlew connectedCheck`
   - Tests components that require Android framework
   - Uses AndroidJUnit4, Espresso, and ActivityScenarioRule

3. **Code Coverage**: JaCoCo integration for test coverage reporting
   - Unit test coverage: `./gradlew jacocoTestReport`
   - Instrumented test coverage: `./gradlew jacocoAndroidTestReport`
   - Combined coverage: `./gradlew jacocoCombinedReport`
   - Reports are generated in HTML and XML formats in `app/build/reports/jacoco/`

4. **CI Integration**: Automated testing in GitHub Actions
   - Unit tests run on every push and pull request
   - Instrumented tests run on multiple API levels (21, 24, 29, 33)
   - Test results and coverage reports are uploaded as artifacts
   - Test summaries are added as comments to pull requests
   - Coverage reports are uploaded to Codecov for visualization

#### Future Improvements

Potential future improvements could include:

1. Adding more test cases to increase code coverage
2. Adding deployment workflows for different environments
3. Enhancing the dependency update process with more granular control
4. Adding more sophisticated branch protection rules
5. Implementing UI testing with screenshot comparison

## License
MIT

---

### Building in Android Studio

Android Studio handles all development tasks:
- **Coding**: Write Java/Kotlin classes in the code editor.
- **UI Design**: Use the **Layout Editor** (Design tab) for layouts.
- **Building**: Compile with **Build > Make Project** or **Run > Run 'app'**.
- **Debugging**: Use **Logcat**, **Debugger**, and **Profiler** (View > Tool Windows).
- **Testing**: Run on a device/emulator via **Run > Run 'app'**.
- **Version Control**: Manage GitHub via **VCS > Git** (commit, push).
- **Dependencies**: Handle libraries in `build.gradle`.
- **APK Signing**: Sign APKs in **Build > Generate Signed Bundle/APK**.
