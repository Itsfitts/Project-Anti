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
@Config(sdk = {28})
public class AndroidComponentTest {
    
    @Test
    public void testSomeAndroidFeature() {
        // Test code using Android components
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

### Running Tests
- **Run Unit Tests**: `./gradlew test`
- **Run Instrumented Tests**: `./gradlew connectedAndroidTest`
- **Run Specific Test Class**: `./gradlew testDebugUnitTest --tests "com.anti.rootadbcontroller.YourTestClass"`

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

### Security Considerations
- The app includes anti-detection measures to prevent analysis
- Root detection and ADB detection are implemented
- The app can clear traces to prevent forensic analysis
