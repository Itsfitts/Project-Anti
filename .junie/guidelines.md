# Project-Anti Development Guidelines

This document provides essential information for developers working on the Project-Anti Android application.

## Build/Configuration Instructions

### Prerequisites
- Android Studio (latest version recommended)
- JDK 17
- Android SDK with API level 34

### Setup
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files

### Build Configuration
The project uses Gradle as the build system with the following key configurations:

- **Compile SDK**: 34
- **Min SDK**: 21
- **Target SDK**: 34
- **Kotlin Version**: 2.2.0
- **Compose Compiler Extension**: 1.5.15

### Build Types
The project includes several build types:
- **Debug**: Default development build
- **Release**: Production build with optimizations
- **Staging**: Testing environment with debugging enabled
- **CustomTest**: Additional testing environment

## Testing Information

### Test Structure
The project uses JUnit for unit testing and supports both unit tests and instrumented tests:

- **Unit Tests**: Located in `app/src/test/java/`
- **Instrumented Tests**: Located in `app/src/androidTest/java/`

### Testing Dependencies
The project includes the following testing dependencies:
```gradle
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.mockito:mockito-core:4.11.0'
testImplementation 'org.robolectric:robolectric:4.10.3'
androidTestImplementation 'androidx.test.ext:junit:1.1.5'
androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
```

### Creating Tests
#### Unit Tests
1. Create a test class in the `app/src/test/java/` directory
2. Use JUnit annotations (`@Test`, `@Before`, etc.)
3. For Android-specific components, use Robolectric for simulation

Example of a simple unit test:
```java
package com.anti.rootadbcontroller;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SimpleTest {

    @Test
    public void testAddition() {
        int result = 2 + 2;
        assertEquals("2 + 2 should equal 4", 4, result);
    }
}
```

Example of testing Android components with Robolectric:
```java
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {34})
public class AndroidComponentTest {

    @Test
    public void testContextNotNull() {
        Context context = RuntimeEnvironment.getApplication();
        assertNotNull("Application context should not be null", context);
    }

    @Test
    public void testTextViewCreation() {
        Context context = RuntimeEnvironment.getApplication();
        TextView textView = new TextView(context);
        textView.setText("Hello Robolectric");

        assertEquals("TextView text should be set correctly",
                "Hello Robolectric", textView.getText().toString());
    }
}
```

#### Testing Private Methods
For testing private methods, you can use reflection:

```java
@Test
public void testPrivateMethod() throws Exception {
    Method privateMethod = YourClass.class.getDeclaredMethod("privateMethodName", parameterTypes);
    privateMethod.setAccessible(true);
    Object result = privateMethod.invoke(objectInstance, parameters);
    // Assert on result
}
```

#### Instrumented Tests
1. Create a test class in the `app/src/androidTest/java/` directory
2. Use `@RunWith(AndroidJUnit4.class)` annotation
3. Use `ActivityScenarioRule` for testing activities
4. Use Espresso for UI testing

Example of an instrumented test:
```java
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
        new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testRecyclerViewDisplayed() {
        // Check if the RecyclerView is displayed
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testAppTitleDisplayed() {
        // Check if the app title is displayed in the toolbar
        Espresso.onView(ViewMatchers.withText(R.string.app_name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
```

Example of testing utility classes with instrumented tests:
```java
@RunWith(AndroidJUnit4.class)
public class AntiDetectionUtilsInstrumentedTest {

    private Context context;

    @Before
    public void setUp() {
        // Get the instrumentation context
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void testIsEmulator() {
        // Test the isEmulator method
        boolean result = AntiDetectionUtils.isEmulator(context);

        // Log the result for debugging
        System.out.println("[DEBUG_LOG] isEmulator result: " + result);

        // Assert based on your environment
        // assertTrue("Should detect that we're running on an emulator", result);
    }
}
```

### Running Tests
- **Run Unit Tests**: `./gradlew test`
- **Run Instrumented Tests**: `./gradlew connectedAndroidTest`
- **Run Specific Test Class**: `./gradlew testDebugUnitTest --tests "com.anti.rootadbcontroller.YourTestClass"`

### Debugging Tests
You can enhance debugging by adding logging to your tests:
- Always start debug messages with `[DEBUG_LOG]` prefix
- Java: `System.out.println("[DEBUG_LOG] Your message here")`
- Kotlin: `println("[DEBUG_LOG] Your message here")`

Example of a test with debug logging:
```java
@Test
public void testStringLength() {
    String testString = "Project-Anti";
    int length = testString.length();

    // Log for debugging
    System.out.println("[DEBUG_LOG] String length: " + length);

    assertEquals("String length should be 12", 12, length);
}
```

## Additional Development Information

### Code Style
The project uses ktlint for Kotlin code style enforcement. The configuration is in the app-level build.gradle:

```gradle
plugins {
    id 'org.jlleitschuh.gradle.ktlint' version '11.3.2'
}
```

### Key Components

#### Utility Classes
- **AdbUtils**: Handles ADB-related operations
- **AntiDetectionUtils**: Detects emulators and analysis tools
- **AutomationUtils**: Handles UI automation
- **RootUtils**: Manages root-related operations
- **ShizukuUtils**: Interfaces with the Shizuku service

#### Services
- **KeyloggerAccessibilityService**: Accessibility service for keylogging
- **KillSwitchReceiver**: Handles kill switch functionality
- **LocationTrackerService**: Tracks device location
- **MicRecorderService**: Records audio from the microphone
- **OverlayService**: Manages screen overlays
- **RemoteAdbService**: Handles remote ADB connections
- **ShizukuManagerService**: Manages Shizuku service
- **StealthCameraService**: Controls camera operations stealthily

### UI Framework
The project uses both traditional Android XML layouts and Jetpack Compose for UI:

- XML layouts are in `app/src/main/res/layout/`
- Compose UI components are in `app/src/main/java/com/anti/rootadbcontroller/ui/theme/`

### Debugging
- Lint is configured with specific rules and reports
- Developer mode detection is implemented in AntiDetectionUtils
- Use the `[DEBUG_LOG]` prefix for debug messages in tests and code

### Security Considerations
- The app includes anti-detection measures to prevent analysis
- Root detection and ADB detection are implemented
- The app can clear traces to prevent forensic analysis
