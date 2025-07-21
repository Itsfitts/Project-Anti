# Project-Anti Instrumented Tests

This directory contains instrumented tests for the Project-Anti application. Instrumented tests run on an actual Android device or emulator, allowing you to test your app in a real Android environment.

## Test Organization

- **MainActivityTest.java**: Tests for the main activity UI elements
- **AntiDetectionUtilsInstrumentedTest.java**: Tests for the emulator detection functionality
- **PermissionTest.java**: Demonstrates how to test features requiring runtime permissions

## Running Instrumented Tests

### Method 1: Run from Android Studio
1. Right-click on a test class or method
2. Select "Run 'YourTestClass'"
3. Choose the emulator or connected device you want to run the test on

### Method 2: Run from Command Line
```
./gradlew connectedAndroidTest
```

To run a specific test:
```
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.anti.rootadbcontroller.MainActivityTest
```

## Setting Up an Emulator

1. Open the AVD Manager in Android Studio (Tools > AVD Manager)
2. Click "Create Virtual Device"
3. Select a device definition (e.g., Pixel 6)
4. Select a system image (e.g., API 34)
5. Configure the AVD options and click "Finish"

## Test Considerations

### Emulator Detection
The `AntiDetectionUtilsInstrumentedTest` tests the app's ability to detect emulators. When running these tests on an emulator, they should pass because the app should detect that it's running in an emulator environment.

### UI Testing
The `MainActivityTest` tests the UI elements of the main activity. These tests use Espresso to interact with the UI and verify that elements are displayed correctly.

### Permissions Testing
The `PermissionTest` demonstrates how to test features that require runtime permissions. It uses the `GrantPermissionRule` to automatically grant permissions during testing, which is essential for testing features like camera access, location tracking, and file operations.

Example of using GrantPermissionRule:
```java
@Rule
public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.ACCESS_FINE_LOCATION
);
```

This rule automatically grants the specified permissions before the tests run, eliminating the need to manually handle permission dialogs during testing.

## Debugging Tests

If your tests fail:

1. Check the logcat output in Android Studio
2. Look for debug logs with the prefix `[DEBUG_LOG]`
3. Run tests with the debugger attached

## Adding New Tests

When adding new instrumented tests:

1. Create a new test class in this directory
2. Use the `@RunWith(AndroidJUnit4.class)` annotation
3. Use `InstrumentationRegistry.getInstrumentation().getTargetContext()` to get the application context
4. Use Espresso for UI testing
5. Add debug logs with `System.out.println("[DEBUG_LOG] Your message")` for easier debugging
