package com.anti.rootadbcontroller;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Example simple unit test.
 */
public class SimpleTest {

    @Test
    public void testAddition() {
        int result = 2 + 2;
        assertEquals("2 + 2 should equal 4", 4, result);
    }

    @Test
    public void testStringConcatenation() {
        String result = "Hello" + " " + "World";
        assertEquals("String concatenation should work correctly", "Hello World", result);
    }
}
