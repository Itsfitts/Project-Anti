package com.anti.rootadbcontroller

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.anti.rootadbcontroller.utils.AntiDetectionUtils
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for AntiDetectionUtils that runs on an Android device or emulator.
 * These tests verify the emulator detection functionality.
 */
@RunWith(AndroidJUnit4::class)
class AntiDetectionUtilsInstrumentedTest {

    private var context: android.content.Context? = null

    @Before
    fun setUp() {
        // Get the instrumentation context
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testIsEmulator() {
        // When running on an emulator, this should return true
        // When running on a real device, this should return false
        val result = context?.let { AntiDetectionUtils.isEmulator(it) } ?: false

        // Log the result for debugging
        println("[DEBUG_LOG] isEmulator result: $result")

        // Note: This assertion assumes the test is running on an emulator
        // If running on a real device, this test would fail
        assertTrue("Should detect that we're running on an emulator", result)
    }

    @Test
    fun testIsDeveloperModeEnabled() {
        // Check if developer mode is enabled
        val result = context?.let { AntiDetectionUtils.isDeveloperModeEnabled(it) } ?: false

        // Log the result for debugging
        println("[DEBUG_LOG] isDeveloperModeEnabled result: $result")

        // Note: This test doesn't assert a specific result since it depends on the device settings
        // It's primarily for logging and verification purposes
    }
}

