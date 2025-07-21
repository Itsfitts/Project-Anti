package com.anti.rootadbcontroller;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.anti.rootadbcontroller.utils.AntiDetectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Instrumented test for AntiDetectionUtils that runs on an Android device or emulator.
 * These tests verify the emulator detection functionality.
 */
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
        // When running on an emulator, this should return true
        // When running on a real device, this should return false
        boolean result = AntiDetectionUtils.isEmulator(context);

        // Log the result for debugging
        System.out.println("[DEBUG_LOG] isEmulator result: " + result);

        // Note: This assertion assumes the test is running on an emulator
        // If running on a real device, this test would fail
        assertTrue("Should detect that we're running on an emulator", result);
    }

    @Test
    public void testIsDeveloperModeEnabled() {
        // Check if developer mode is enabled
        boolean result = AntiDetectionUtils.isDeveloperModeEnabled(context);

        // Log the result for debugging
        System.out.println("[DEBUG_LOG] isDeveloperModeEnabled result: " + result);

        // Note: This test doesn't assert a specific result since it depends on the device settings
        // It's primarily for logging and verification purposes
    }

    @Test
    public void testIsBeingAnalyzed() {
        // This should return true on an emulator or when developer mode is enabled
        boolean result = AntiDetectionUtils.isBeingAnalyzed(context);

        // Log the result for debugging
        System.out.println("[DEBUG_LOG] isBeingAnalyzed result: " + result);

        // Note: This assertion assumes the test is running on an emulator or with developer mode enabled
        assertTrue("Should detect that we're in an analysis environment", result);
    }
}
