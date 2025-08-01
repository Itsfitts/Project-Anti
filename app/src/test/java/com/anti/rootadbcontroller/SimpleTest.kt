package com.anti.rootadbcontroller

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example simple unit test.
 */
class SimpleTest {

    @Test
    fun testAddition() {
        val result = 2 + 2
        assertEquals("2 + 2 should equal 4", 4, result)
    }

    @Test
    fun testStringConcatenation() {
        val result = "Hello" + " " + "World"
        assertEquals("String concatenation should work correctly", "Hello World", result)
    }
}
