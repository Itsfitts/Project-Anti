package com.anti.rootadbcontroller;

import android.content.Context;
import android.os.Build;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.anti.rootadbcontroller.utils.AntiDetectionUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Functional test for emulator detection that runs on an Android device or emulator.
 * This test provides comprehensive verification of all emulator detection methods.
 */
@RunWith(AndroidJUnit4.class)
public class EmulatorDetectionFunctionalTest {

    private Context context;

    @Before
    public void setUp() {
        // Get the instrumentation context
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertNotNull("Context should not be null", context);

        // Log device information for debugging
        System.out.println("[DEBUG_LOG] Device information:");
        System.out.println("[DEBUG_LOG] Manufacturer: " + Build.MANUFACTURER);
        System.out.println("[DEBUG_LOG] Brand: " + Build.BRAND);
        System.out.println("[DEBUG_LOG] Model: " + Build.MODEL);
        System.out.println("[DEBUG_LOG] Product: " + Build.PRODUCT);
        System.out.println("[DEBUG_LOG] Fingerprint: " + Build.FINGERPRINT);
    }

    @Test
    public void testIsEmulator() {
        // Test the main emulator detection method
        boolean result = AntiDetectionUtils.isEmulator(context);
        System.out.println("[DEBUG_LOG] isEmulator result: " + result);

        // When running on an emulator, this should return true
        assertTrue("Should detect that we're running on an emulator", result);
    }

    @Test
    public void testCheckBuild() throws Exception {
        // Test the build properties check using reflection to access private method
        Method checkBuildMethod = AntiDetectionUtils.class.getDeclaredMethod("checkBuild");
        checkBuildMethod.setAccessible(true);
        boolean result = (boolean) checkBuildMethod.invoke(null);

        System.out.println("[DEBUG_LOG] checkBuild result: " + result);
        System.out.println("[DEBUG_LOG] Build.FINGERPRINT: " + Build.FINGERPRINT);
        System.out.println("[DEBUG_LOG] Build.MODEL: " + Build.MODEL);
        System.out.println("[DEBUG_LOG] Build.MANUFACTURER: " + Build.MANUFACTURER);
        System.out.println("[DEBUG_LOG] Build.BRAND: " + Build.BRAND);
        System.out.println("[DEBUG_LOG] Build.DEVICE: " + Build.DEVICE);
        System.out.println("[DEBUG_LOG] Build.PRODUCT: " + Build.PRODUCT);
    }

    @Test
    public void testCheckFiles() throws Exception {
        // Test the emulator-specific files check using reflection
        Method checkFilesMethod = AntiDetectionUtils.class.getDeclaredMethod("checkFiles");
        checkFilesMethod.setAccessible(true);
        boolean result = (boolean) checkFilesMethod.invoke(null);

        System.out.println("[DEBUG_LOG] checkFiles result: " + result);
    }

    @Test
    public void testCheckPackages() throws Exception {
        // Test the emulator packages check using reflection
        Method checkPackagesMethod = AntiDetectionUtils.class.getDeclaredMethod("checkPackages", Context.class);
        checkPackagesMethod.setAccessible(true);
        boolean result = (boolean) checkPackagesMethod.invoke(null, context);

        System.out.println("[DEBUG_LOG] checkPackages result: " + result);
    }

    @Test
    public void testCheckTelephony() throws Exception {
        // Test the telephony check using reflection
        Method checkTelephonyMethod = AntiDetectionUtils.class.getDeclaredMethod("checkTelephony", Context.class);
        checkTelephonyMethod.setAccessible(true);
        boolean result = (boolean) checkTelephonyMethod.invoke(null, context);

        System.out.println("[DEBUG_LOG] checkTelephony result: " + result);
    }

    @Test
    public void testCheckDebugger() throws Exception {
        // Test the debugger check using reflection
        Method checkDebuggerMethod = AntiDetectionUtils.class.getDeclaredMethod("checkDebugger");
        checkDebuggerMethod.setAccessible(true);
        boolean result = (boolean) checkDebuggerMethod.invoke(null);

        System.out.println("[DEBUG_LOG] checkDebugger result: " + result);
    }

    @Test
    public void testCheckSensors() throws Exception {
        // Test the sensors check using reflection
        Method checkSensorsMethod = AntiDetectionUtils.class.getDeclaredMethod("checkSensors", Context.class);
        checkSensorsMethod.setAccessible(true);
        boolean result = (boolean) checkSensorsMethod.invoke(null, context);

        System.out.println("[DEBUG_LOG] checkSensors result: " + result);
    }

    @Test
    public void testAllDetectionMethods() throws Exception {
        // Test all detection methods and log their results
        Method checkBuildMethod = AntiDetectionUtils.class.getDeclaredMethod("checkBuild");
        Method checkFilesMethod = AntiDetectionUtils.class.getDeclaredMethod("checkFiles");
        Method checkPackagesMethod = AntiDetectionUtils.class.getDeclaredMethod("checkPackages", Context.class);
        Method checkTelephonyMethod = AntiDetectionUtils.class.getDeclaredMethod("checkTelephony", Context.class);
        Method checkDebuggerMethod = AntiDetectionUtils.class.getDeclaredMethod("checkDebugger");
        Method checkSensorsMethod = AntiDetectionUtils.class.getDeclaredMethod("checkSensors", Context.class);

        checkBuildMethod.setAccessible(true);
        checkFilesMethod.setAccessible(true);
        checkPackagesMethod.setAccessible(true);
        checkTelephonyMethod.setAccessible(true);
        checkDebuggerMethod.setAccessible(true);
        checkSensorsMethod.setAccessible(true);

        boolean buildResult = (boolean) checkBuildMethod.invoke(null);
        boolean filesResult = (boolean) checkFilesMethod.invoke(null);
        boolean packagesResult = (boolean) checkPackagesMethod.invoke(null, context);
        boolean telephonyResult = (boolean) checkTelephonyMethod.invoke(null, context);
        boolean debuggerResult = (boolean) checkDebuggerMethod.invoke(null);
        boolean sensorsResult = (boolean) checkSensorsMethod.invoke(null, context);

        System.out.println("[DEBUG_LOG] All detection methods results:");
        System.out.println("[DEBUG_LOG] checkBuild: " + buildResult);
        System.out.println("[DEBUG_LOG] checkFiles: " + filesResult);
        System.out.println("[DEBUG_LOG] checkPackages: " + packagesResult);
        System.out.println("[DEBUG_LOG] checkTelephony: " + telephonyResult);
        System.out.println("[DEBUG_LOG] checkDebugger: " + debuggerResult);
        System.out.println("[DEBUG_LOG] checkSensors: " + sensorsResult);

        // At least one method should detect the emulator
        boolean anyMethodDetected = buildResult || filesResult || packagesResult ||
                                   telephonyResult || debuggerResult || sensorsResult;
        assertTrue("At least one detection method should identify the emulator", anyMethodDetected);
    }
}
