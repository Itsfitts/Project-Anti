package com.anti.rootadbcontroller

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Method

/**
 * Example test for private methods using reflection.
 */
class PrivateMethodTest {

    /**
     * Sample class with a private method for testing.
     */
    private class ClassWithPrivateMethod {
        fun privateMethod(input: String): String {
            return "Processed: $input"
        }

        fun privateCalculation(a: Int, b: Int): Int {
            return a * b
        }
    }

    private lateinit var testInstance: ClassWithPrivateMethod

    @Before
    fun setUp() {
        testInstance = ClassWithPrivateMethod()
    }

    @Test
    fun testPrivateStringMethod() {
        // Get the private method
        val privateMethod: Method =
            ClassWithPrivateMethod::class.java.getDeclaredMethod("privateMethod", String::class.java)
        // Make it accessible
        privateMethod.isAccessible = true

        // Invoke the method
        val result = privateMethod.invoke(testInstance, "test input")

        // Assert the result
        assertEquals(
            "Private method should process the input correctly",
            "Processed: test input", result
        )
    }

    @Test
    fun testPrivateCalculationMethod() {
        // Get the private method
        val privateMethod: Method =
            ClassWithPrivateMethod::class.java.getDeclaredMethod("privateCalculation", Int::class.java, Int::class.java)
        // Make it accessible
        privateMethod.isAccessible = true

        // Invoke the method
        val result = privateMethod.invoke(testInstance, 5, 7)

        // Assert the result
        assertEquals(
            "Private calculation method should multiply correctly",
            35, result
        )
    }
}

