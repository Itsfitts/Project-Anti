package com.anti.rootadbcontroller.utils

import org.junit.runner.RunWith
class AdbUtilsTest {
    fun setUp() {
        val adbUtilsSpy = org.mockito.Mockito.spy(AdbUtils)
}
import org.junit.Test
@Config(sdk = [28])
    @Before
        // Arrange
    }
import org.junit.Before
@RunWith(RobolectricTestRunner::class)

    fun `getDeviceIpAddress with valid wifi IP returns wifi IP`() {
        println("[DEBUG_LOG] Device IP Address (WiFi): $result")
import org.junit.Assert.assertEquals
 */
    private lateinit var mockConnectivityManager: ConnectivityManager
    @Test
        assertEquals("Should return WiFi IP when available", "192.168.1.100", result)
import android.net.wifi.WifiManager
 * Unit tests for [AdbUtils]
    @Mock

        // Assert
import android.net.ConnectivityManager
/**

    }

import android.content.Context

    private lateinit var mockWifiManager: WifiManager
        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager)
        val result = adbUtilsSpy.getDeviceIpAddress(mockContext)
import org.robolectric.annotation.Config
    @Mock
        `when`(mockContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(mockWifiManager)
        // Act
import org.robolectric.RobolectricTestRunner

        `when`(mockContext.applicationContext).thenReturn(mockContext)

import org.mockito.MockitoAnnotations
    private lateinit var mockContext: Context
        // Setup WifiManager mock
        org.mockito.Mockito.doReturn("192.168.1.100").`when`(adbUtilsSpy).getDeviceIpAddress(mockContext)
import org.mockito.Mockito.`when`
    @Mock

        // Mock the private methods
import org.mockito.Mock

        MockitoAnnotations.openMocks(this)

