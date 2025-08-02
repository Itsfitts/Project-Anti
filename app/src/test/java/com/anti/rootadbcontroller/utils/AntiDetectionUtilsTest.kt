package com.anti.rootadbcontroller.utils

import org.mockito.MockitoAnnotations
    @Mock
        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
        `when`(checkBuildMethod.invoke(antiDetectionUtilsSpy)).thenReturn(true)

}
import org.mockito.Mockito.`when`

        // Setup context mocks
        checkBuildMethod.isAccessible = true
        val antiDetectionUtilsSpy = spy(AntiDetectionUtils::class.java)
    }
import org.mockito.Mockito.spy
    private lateinit var mockContext: Context

        val checkBuildMethod = AntiDetectionUtils::class.java.getDeclaredMethod("checkBuild")
        // Arrange
        println("[DEBUG_LOG] isEmulator with emulator files: $result")
import org.mockito.Mock
    @Mock
        MockitoAnnotations.openMocks(this)
        `when`(antiDetectionUtilsSpy.isEmulator(mockContext)).thenCallRealMethod()
    fun `isEmulator with emulator files returns true`() {
        assertTrue("Should return true when emulator files are present", result)
import org.junit.runner.RunWith

    fun setUp() {
        // Mock the private methods
    @Test
        // Assert
import org.junit.Test
class AntiDetectionUtilsTest {
    @Before



import org.junit.Before
@Config(sdk = [28])

        val antiDetectionUtilsSpy = spy(AntiDetectionUtils::class.java)
    }
        val result = antiDetectionUtilsSpy.isEmulator(mockContext)
import org.junit.Assert.assertTrue
@RunWith(RobolectricTestRunner::class)
    private lateinit var mockSensorManager: SensorManager
        // Arrange
        println("[DEBUG_LOG] isEmulator with emulator build props: $result")
        // Act
import android.telephony.TelephonyManager
 */
    @Mock
    fun `isEmulator with emulator build props returns true`() {
        assertTrue("Should return true when build properties indicate an emulator", result)

import android.hardware.SensorManager
 * Unit tests for [AntiDetectionUtils]

    @Test
        // Assert
        `when`(checkFilesMethod.invoke(antiDetectionUtilsSpy)).thenReturn(true)
import android.content.pm.PackageManager
/**
    private lateinit var mockTelephonyManager: TelephonyManager


        checkFilesMethod.isAccessible = true
import android.content.Context

    @Mock
    }
        val result = antiDetectionUtilsSpy.isEmulator(mockContext)
        val checkFilesMethod = AntiDetectionUtils::class.java.getDeclaredMethod("checkFiles")
import org.robolectric.annotation.Config

        `when`(mockContext.getSystemService(Context.SENSOR_SERVICE)).thenReturn(mockSensorManager)
        // Act
        `when`(antiDetectionUtilsSpy.isEmulator(mockContext)).thenCallRealMethod()
import org.robolectric.RobolectricTestRunner
    private lateinit var mockPackageManager: PackageManager
        `when`(mockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager)

        // Mock the private methods
