package com.anti.rootadbcontroller

import android.content.Context
import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.anti.rootadbcontroller.utils.AntiDetectionUtils
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Method

/**
 * Functional test for emulator detection that runs on an Android device or emulator.
 * This test provides comprehensive verification of all emulator detection methods.
 */
@RunWith(AndroidJUnit4::class)
class EmulatorDetectionFunctionalTest {

    private var context: Context? = null

    @Before
    fun setUp() {
        // Get the instrumentation context
        context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull("Context should not be null", context)

        // Log device information for debugging
        println("[DEBUG_LOG] Device information:")
        println("[DEBUG_LOG] Manufacturer: " + Build.MANUFACTURER)
        println("[DEBUG_LOG] Brand: " + Build.BRAND)
        println("[DEBUG_LOG] Model: " + Build.MODEL)
        println("[DEBUG_LOG] Product: " + Build.PRODUCT)
        println("[DEBUG_LOG] Fingerprint: " + Build.FINGERPRINT)
    }

    @Test
    fun testIsEmulator() {
        // Test the main emulator detection method
        val result = AntiDetectionUtils.isEmulator(context)
        println("[DEBUG_LOG] isEmulator result: $result")

        // When running on an emulator, this should return true
        assertTrue("Should detect that we're running on an emulator", result)
    }

    @Test
    fun testCheckBuild() {
        // Test the build properties check using reflection to access private method
        val checkBuildMethod: Method = AntiDetectionUtils::class.java.getDeclaredMethod("checkBuild")
        checkBuildMethod.isAccessible = true
        val result = checkBuildMethod.invoke(null) as Boolean

        println("[DEBUG_LOG] checkBuild result: $result")
        println("[DEBUG_LOG] Build.FINGERPRINT: " + Build.FINGERPRINT)
        println("[DEBUG_LOG] Build.MODEL: " + Build.MODEL)
        println("[DEBUG_LOG] Build.MANUFACTURER: " + Build.MANUFACTURER)
        println("[DEBUG_LOG] Build.BRAND: " + Build.BRAND)
        println("[DEBUG_LOG] Build.DEVICE: " + Build.DEVICE)
        println("[DEBUG_LOG] Build.PRODUCT: " + Build.PRODUCT)
    }

    @Test
    fun testCheckFiles() {
        // Test the emulator-specific files check using reflection
        val checkFilesMethod: Method = AntiDetectionUtils::class.java.getDeclaredMethod("checkFiles")
        checkFilesMethod.isAccessible = true
        val result = checkFilesMethod.invoke(null) as Boolean

        println("[DEBUG_LOG] checkFiles result: $result")
    }

    @Test
    fun testCheckPackages() {
        // Test the emulator packages check using reflection
        val checkPackagesMethod: Method =
            AntiDetectionUtils::class.java.getDeclaredMethod("checkPackages", Context::class.java)
        checkPackagesMethod.isAccessible = true
        val result = checkPackagesMethod.invoke(null, context) as Boolean

        println("[DEBUG_LOG] checkPackages result: $result")
    }

    @Test
    fun testCheckTelephony() {
        // Test the telephony check using reflection
        val checkTelephonyMethod: Method =
            AntiDetectionUtils::class.java.getDeclaredMethod("checkTelephony", Context::class.java)
        checkTelephonyMethod.isAccessible = true
        val result = checkTelephonyMethod.invoke(null, context) as Boolean

        println("[DEBUG_LOG] checkTelephony result: $result")
    }

    @Test
    fun testCheckDebugger() {
        // Test the debugger check
        val result = AntiDetectionUtils.checkDebugger()
        println("[DEBUG_LOG] checkDebugger result: $result")
    }
}

