# Project-Anti Test Structure

This directory contains unit tests for the Project-Anti application. The tests are organized according to the package structure of the main code.

## Test Organization

- **Root Package**: Contains general tests and examples
  - `SimpleTest.java`: Basic JUnit test examples
  - `AndroidComponentTest.java`: Example of testing Android components with Robolectric
  - `PrivateMethodTest.java`: Example of testing private methods using reflection

- **utils Package**: Contains tests for utility classes
  - Tests for AdbUtils, AntiDetectionUtils, AutomationUtils, RootUtils, ShizukuUtils

## Writing Tests

### Naming Conventions

- Test classes should be named with the suffix `Test`
- Test methods should be named with the prefix `test`
- Test methods should clearly describe what they are testing

### Test Structure

Each test class should:
1. Import the necessary JUnit and assertion classes
2. Include appropriate annotations (e.g., `@RunWith` for Robolectric tests)
3. Have a clear setup method if needed (`@Before`)
4. Include teardown if needed (`@After`)
5. Have well-documented test methods

### Example Test Method Structure

```java
@Test
public void testMethodName() {
    // Arrange - Set up the test data
    
    // Act - Call the method being tested
    
    // Assert - Verify the results
}
```

### Running Tests

- Run all tests: `./gradlew test`
- Run a specific test class: `./gradlew testDebugUnitTest --tests "com.anti.rootadbcontroller.YourTestClass"`

## Testing Resources

- JUnit: [https://junit.org/junit4/](https://junit.org/junit4/)
- Robolectric: [http://robolectric.org/](http://robolectric.org/)
- Mockito: [https://site.mockito.org/](https://site.mockito.org/)
