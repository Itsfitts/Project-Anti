# Project-Anti Instrumented Tests

This directory contains instrumented tests for the Project-Anti application. Instrumented tests run on an actual Android device or emulator, allowing you to test your app in a real Android environment.

## Test Organization

- **MainActivityTest.java**: Tests for the main activity UI elements
- **AntiDetectionUtilsInstrumentedTest.java**: Basic tests for the emulator detection functionality
- **EmulatorDetectionFunctionalTest.java**: Comprehensive functional tests for emulator detection
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
The `AntiDetectionUtilsInstrumentedTest` provides basic tests for the app's ability to detect emulators. When running these tests on an emulator, they should pass because the app should detect that it's running in an emulator environment.

The `EmulatorDetectionFunctionalTest` provides a more comprehensive test suite for verifying the emulator detection functionality. This test includes:

1. **Device Information Logging**: Logs detailed information about the device running the test, including manufacturer, brand, model, product, and fingerprint.

2. **Main Emulator Detection Test**: Tests the `isEmulator()` method to verify that it correctly identifies the environment as an emulator.

3. **Individual Detection Method Tests**: Tests each of the private detection methods used by `isEmulator()`:
   - `checkBuild()`: Tests detection based on build properties
   - `checkFiles()`: Tests detection based on emulator-specific files
   - `checkPackages()`: Tests detection based on emulator-related packages
   - `checkTelephony()`: Tests detection based on telephony characteristics
   - `checkDebugger()`: Tests detection based on debugger connection
   - `checkSensors()`: Tests detection based on sensor availability

4. **Comprehensive Detection Test**: Runs all detection methods and logs their results, verifying that at least one method correctly identifies the emulator.

To run this specific test:
```
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.anti.rootadbcontroller.EmulatorDetectionFunctionalTest
```

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

### Creating Functional Tests

For creating comprehensive functional tests like `EmulatorDetectionFunctionalTest`:

1. Test both public and private methods:
   - Use direct method calls for public methods
   - Use reflection for testing private methods (example below):

   ```
   Method privateMethod = YourClass.class.getDeclaredMethod("privateMethodName", parameterTypes);
   privateMethod.setAccessible(true);
   Object result = privateMethod.invoke(objectInstance, parameters);
   ```

2. Log detailed information about the test environment:
   - Device properties
   - System settings
   - Test parameters

3. Create individual test methods for each component or functionality:
   - Test each method independently
   - Create a comprehensive test that verifies all components together

4. Add detailed debug logging to help with troubleshooting:

   ```
   System.out.println("[DEBUG_LOG] Test result: " + result);
   ```
