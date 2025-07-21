package com.anti.rootadbcontroller;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * Example test for private methods using reflection.
 */
public class PrivateMethodTest {

    /**
     * Sample class with a private method for testing.
     */
    private static class ClassWithPrivateMethod {
        private String privateMethod(String input) {
            return "Processed: " + input;
        }

        private int privateCalculation(int a, int b) {
            return a * b;
        }
    }

    private ClassWithPrivateMethod testInstance;

    @Before
    public void setUp() {
        testInstance = new ClassWithPrivateMethod();
    }

    @Test
    public void testPrivateStringMethod() throws Exception {
        // Get the private method
        Method privateMethod = ClassWithPrivateMethod.class.getDeclaredMethod("privateMethod", String.class);
        // Make it accessible
        privateMethod.setAccessible(true);

        // Invoke the method
        Object result = privateMethod.invoke(testInstance, "test input");

        // Assert the result
        assertEquals("Private method should process the input correctly",
                "Processed: test input", result);
    }

    @Test
    public void testPrivateCalculationMethod() throws Exception {
        // Get the private method
        Method privateMethod = ClassWithPrivateMethod.class.getDeclaredMethod("privateCalculation", int.class, int.class);
        // Make it accessible
        privateMethod.setAccessible(true);

        // Invoke the method
        Object result = privateMethod.invoke(testInstance, 5, 7);

        // Assert the result
        assertEquals("Private calculation method should multiply correctly",
                35, result);
    }
}
