package com.anti.rootadbcontroller.utils

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.telephony.TelephonyManager
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.spy
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for [AntiDetectionUtils]
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AntiDetectionUtilsTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockPackageManager: PackageManager

    @Mock
    private lateinit var mockTelephonyManager: TelephonyManager

    @Mock
    private lateinit var mockSensorManager: SensorManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Setup context mocks
        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
        `when`(mockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager)
        `when`(mockContext.getSystemService(Context.SENSOR_SERVICE)).thenReturn(mockSensorManager)
    }

    @Test
    fun `isEmulator with emulator build props returns true`() {
        // Arrange
        val antiDetectionUtilsSpy = spy(AntiDetectionUtils::class.java)

        // Mock the private methods
        `when`(antiDetectionUtilsSpy.isEmulator(mockContext)).thenCallRealMethod()
        val checkBuildMethod = AntiDetectionUtils::class.java.getDeclaredMethod("checkBuild")
        checkBuildMethod.isAccessible = true
        `when`(checkBuildMethod.invoke(antiDetectionUtilsSpy)).thenReturn(true)


        // Act
        val result = antiDetectionUtilsSpy.isEmulator(mockContext)

        // Assert
        assertTrue("Should return true when build properties indicate an emulator", result)
        println("[DEBUG_LOG] isEmulator with emulator build props: $result")
    }

    @Test
    fun `isEmulator with emulator files returns true`() {
        // Arrange
        val antiDetectionUtilsSpy = spy(AntiDetectionUtils::class.java)

        // Mock the private methods
        `when`(antiDetectionUtilsSpy.isEmulator(mockContext)).thenCallRealMethod()
        val checkFilesMethod = AntiDetectionUtils::class.java.getDeclaredMethod("checkFiles")
        checkFilesMethod.isAccessible = true
        `when`(checkFilesMethod.invoke(antiDetectionUtilsSpy)).thenReturn(true)

        // Act
        val result = antiDetectionUtilsSpy.isEmulator(mockContext)

        // Assert
        assertTrue("Should return true when emulator files are present", result)
        println("[DEBUG_LOG] isEmulator with emulator files: $result")
    }
}

