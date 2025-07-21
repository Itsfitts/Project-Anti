package com.anti.rootadbcontroller.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AdbUtils}
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class AdbUtilsTest {

    @Mock
    private Context mockContext;

    @Mock
    private WifiManager mockWifiManager;

    @Mock
    private ConnectivityManager mockConnectivityManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Setup WifiManager mock
        when(mockContext.getApplicationContext()).thenReturn(mockContext);
        when(mockContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(mockWifiManager);
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
    }

    @Test
    public void testHasInternetConnection_withInternetCapability_returnsTrue() {
        // Arrange
        Network mockNetwork = mock(Network.class);
        NetworkCapabilities mockCapabilities = mock(NetworkCapabilities.class);

        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
        when(mockCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true);

        // Act
        boolean result = AdbUtils.hasInternetConnection(mockContext);

        // Assert
        assertTrue("Should return true when internet capability is available", result);
    }

    @Test
    public void testHasInternetConnection_withoutInternetCapability_returnsFalse() {
        // Arrange
        Network mockNetwork = mock(Network.class);
        NetworkCapabilities mockCapabilities = mock(NetworkCapabilities.class);

        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockNetwork)).thenReturn(mockCapabilities);
        when(mockCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(false);

        // Act
        boolean result = AdbUtils.hasInternetConnection(mockContext);

        // Assert
        assertFalse("Should return false when internet capability is not available", result);
    }

    @Test
    public void testHasInternetConnection_withNullNetwork_returnsFalse() {
        // Arrange
        when(mockConnectivityManager.getActiveNetwork()).thenReturn(null);

        // Act
        boolean result = AdbUtils.hasInternetConnection(mockContext);

        // Assert
        assertFalse("Should return false when active network is null", result);
    }

    @Test
    public void testHasInternetConnection_withNullCapabilities_returnsFalse() {
        // Arrange
        Network mockNetwork = mock(Network.class);

        when(mockConnectivityManager.getActiveNetwork()).thenReturn(mockNetwork);
        when(mockConnectivityManager.getNetworkCapabilities(mockNetwork)).thenReturn(null);

        // Act
        boolean result = AdbUtils.hasInternetConnection(mockContext);

        // Assert
        assertFalse("Should return false when network capabilities are null", result);
    }

    @Test
    public void testGetWifiIpAddress_withValidIpAddress_returnsFormattedIp() {
        // Arrange
        WifiInfo mockWifiInfo = mock(WifiInfo.class);
        // IP address 192.168.1.100 as integer: 1681012736
        int ipAddressInt = 1681012736;

        when(mockWifiManager.getConnectionInfo()).thenReturn(mockWifiInfo);
        when(mockWifiInfo.getIpAddress()).thenReturn(ipAddressInt);

        // Act
        // Use reflection to access private method
        String result = null;
        try {
            java.lang.reflect.Method method = AdbUtils.class.getDeclaredMethod("getWifiIpAddress", Context.class);
            method.setAccessible(true);
            result = (String) method.invoke(null, mockContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Assert
        assertEquals("Should return correctly formatted IP address", "192.168.1.100", result);
    }

    @Test
    public void testGetWifiIpAddress_withNullWifiInfo_returnsNull() {
        // Arrange
        when(mockWifiManager.getConnectionInfo()).thenReturn(null);

        // Act
        // Use reflection to access private method
        String result = null;
        try {
            java.lang.reflect.Method method = AdbUtils.class.getDeclaredMethod("getWifiIpAddress", Context.class);
            method.setAccessible(true);
            result = (String) method.invoke(null, mockContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Assert
        assertEquals("Should return null when WifiInfo is null", null, result);
    }
}
