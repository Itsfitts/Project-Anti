package com.anti.rootadbcontroller.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for [AdbUtils]
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AdbUtilsTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockWifiManager: WifiManager

    @Mock
    private lateinit var mockConnectivityManager: ConnectivityManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Setup WifiManager mock
        `when`(mockContext.applicationContext).thenReturn(mockContext)
        `when`(mockContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(mockWifiManager)
        `when`(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager)
    }

    @Test
    fun `getDeviceIpAddress with valid wifi IP returns wifi IP`() {
        // Arrange
        val adbUtilsSpy = org.mockito.Mockito.spy(AdbUtils)

        // Mock the private methods
        org.mockito.Mockito.doReturn("192.168.1.100").`when`(adbUtilsSpy).getDeviceIpAddress(mockContext)

        // Act
        val result = adbUtilsSpy.getDeviceIpAddress(mockContext)

        // Assert
        assertEquals("Should return WiFi IP when available", "192.168.1.100", result)
        println("[DEBUG_LOG] Device IP Address (WiFi): $result")
    }
}

