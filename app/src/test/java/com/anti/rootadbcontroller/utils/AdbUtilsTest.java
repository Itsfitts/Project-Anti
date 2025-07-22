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
import org.robolectric.shadows.ShadowProcess;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
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

    @Mock
    private ShizukuUtils mockShizukuUtils;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Setup WifiManager mock
        when(mockContext.getApplicationContext()).thenReturn(mockContext);
        when(mockContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(mockWifiManager);
        when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
    }

    @Test
    public void testGetDeviceIpAddress_withValidWifiIp_returnsWifiIp() throws Exception {
        // Arrange
        // Setup a spy of AdbUtils to mock the private methods
        AdbUtils adbUtilsSpy = spy(AdbUtils.class);

        // Use reflection to access private methods
        Method getWifiIpMethod = AdbUtils.class.getDeclaredMethod("getWifiIpAddress", Context.class);
        Method getMobileIpMethod = AdbUtils.class.getDeclaredMethod("getMobileIpAddress");
        Method getAnyIpMethod = AdbUtils.class.getDeclaredMethod("getAnyIpAddress");

        getWifiIpMethod.setAccessible(true);
        getMobileIpMethod.setAccessible(true);
        getAnyIpMethod.setAccessible(true);

        // Mock the private methods
        doReturn("192.168.1.100").when(adbUtilsSpy).getWifiIpAddress(mockContext);
        doReturn(null).when(adbUtilsSpy).getMobileIpAddress();
        doReturn(null).when(adbUtilsSpy).getAnyIpAddress();

        // Act
        String result = adbUtilsSpy.getDeviceIpAddress(mockContext);

        // Assert
        assertEquals("Should return WiFi IP when available", "192.168.1.100", result);
        System.out.println("[DEBUG_LOG] Device IP Address (WiFi): " + result);
    }

    @Test
    public void testGetDeviceIpAddress_withNoWifiIpButValidMobileIp_returnsMobileIp() throws Exception {
        // Arrange
        // Setup a spy of AdbUtils to mock the private methods
        AdbUtils adbUtilsSpy = spy(AdbUtils.class);

        // Use reflection to access private methods
        Method getWifiIpMethod = AdbUtils.class.getDeclaredMethod("getWifiIpAddress", Context.class);
        Method getMobileIpMethod = AdbUtils.class.getDeclaredMethod("getMobileIpAddress");
        Method getAnyIpMethod = AdbUtils.class.getDeclaredMethod("getAnyIpAddress");

        getWifiIpMethod.setAccessible(true);
        getMobileIpMethod.setAccessible(true);
        getAnyIpMethod.setAccessible(true);

        // Mock the private methods
        doReturn(null).when(adbUtilsSpy).getWifiIpAddress(mockContext);
        doReturn("10.0.0.1").when(adbUtilsSpy).getMobileIpAddress();
        doReturn(null).when(adbUtilsSpy).getAnyIpAddress();

        // Act
        String result = adbUtilsSpy.getDeviceIpAddress(mockContext);

        // Assert
        assertEquals("Should return Mobile IP when WiFi IP is not available", "10.0.0.1", result);
        System.out.println("[DEBUG_LOG] Device IP Address (Mobile): " + result);
    }

    @Test
    public void testGetDeviceIpAddress_withNoWifiOrMobileIp_returnsAnyIp() throws Exception {
        // Arrange
        // Setup a spy of AdbUtils to mock the private methods
        AdbUtils adbUtilsSpy = spy(AdbUtils.class);

        // Use reflection to access private methods
        Method getWifiIpMethod = AdbUtils.class.getDeclaredMethod("getWifiIpAddress", Context.class);
        Method getMobileIpMethod = AdbUtils.class.getDeclaredMethod("getMobileIpAddress");
        Method getAnyIpMethod = AdbUtils.class.getDeclaredMethod("getAnyIpAddress");

        getWifiIpMethod.setAccessible(true);
        getMobileIpMethod.setAccessible(true);
        getAnyIpMethod.setAccessible(true);

        // Mock the private methods
        doReturn(null).when(adbUtilsSpy).getWifiIpAddress(mockContext);
        doReturn(null).when(adbUtilsSpy).getMobileIpAddress();
        doReturn("172.16.0.1").when(adbUtilsSpy).getAnyIpAddress();

        // Act
        String result = adbUtilsSpy.getDeviceIpAddress(mockContext);

        // Assert
        assertEquals("Should return Any IP when WiFi and Mobile IPs are not available", "172.16.0.1", result);
        System.out.println("[DEBUG_LOG] Device IP Address (Any): " + result);
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

    @Test
    public void testIsAdbOverTcpEnabled_withShizukuAvailable_returnsTrue() throws Exception {
        // Arrange
        ShizukuUtils.CommandResult mockResult = new ShizukuUtils.CommandResult();
        mockResult.output = "5555";
        mockResult.exitCode = 0;

        // Mock ShizukuUtils singleton
        ShizukuUtils mockShizukuUtils = mock(ShizukuUtils.class);
        when(mockShizukuUtils.isShizukuAvailable()).thenReturn(true);
        when(mockShizukuUtils.hasShizukuPermission()).thenReturn(true);
        when(mockShizukuUtils.executeShellCommandWithResult(anyString())).thenReturn(mockResult);

        // Use reflection to set the singleton instance
        Method getInstance = ShizukuUtils.class.getDeclaredMethod("getInstance");
        getInstance.setAccessible(true);

        // Store the original instance to restore later
        ShizukuUtils originalInstance = (ShizukuUtils) getInstance.invoke(null);

        // Set our mock instance using reflection
        java.lang.reflect.Field instanceField = ShizukuUtils.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockShizukuUtils);

        try {
            // Act
            boolean result = AdbUtils.isAdbOverTcpEnabled();

            // Assert
            assertTrue("Should return true when ADB over TCP is enabled", result);
            System.out.println("[DEBUG_LOG] isAdbOverTcpEnabled with Shizuku: " + result);
        } finally {
            // Restore the original instance
            instanceField.set(null, originalInstance);
        }
    }

    @Test
    public void testIsAdbOverTcpEnabled_withShizukuAvailableButDisabled_returnsFalse() throws Exception {
        // Arrange
        ShizukuUtils.CommandResult mockResult = new ShizukuUtils.CommandResult();
        mockResult.output = "-1";
        mockResult.exitCode = 0;

        // Mock ShizukuUtils singleton
        ShizukuUtils mockShizukuUtils = mock(ShizukuUtils.class);
        when(mockShizukuUtils.isShizukuAvailable()).thenReturn(true);
        when(mockShizukuUtils.hasShizukuPermission()).thenReturn(true);
        when(mockShizukuUtils.executeShellCommandWithResult(anyString())).thenReturn(mockResult);

        // Use reflection to set the singleton instance
        Method getInstance = ShizukuUtils.class.getDeclaredMethod("getInstance");
        getInstance.setAccessible(true);

        // Store the original instance to restore later
        ShizukuUtils originalInstance = (ShizukuUtils) getInstance.invoke(null);

        // Set our mock instance using reflection
        java.lang.reflect.Field instanceField = ShizukuUtils.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockShizukuUtils);

        try {
            // Act
            boolean result = AdbUtils.isAdbOverTcpEnabled();

            // Assert
            assertFalse("Should return false when ADB over TCP is disabled", result);
            System.out.println("[DEBUG_LOG] isAdbOverTcpEnabled with Shizuku (disabled): " + result);
        } finally {
            // Restore the original instance
            instanceField.set(null, originalInstance);
        }
    }

    @Test
    public void testGetAdbOverTcpPort_withShizukuAvailable_returnsPort() throws Exception {
        // Arrange
        ShizukuUtils.CommandResult mockResult = new ShizukuUtils.CommandResult();
        mockResult.output = "5555";
        mockResult.exitCode = 0;

        // Mock ShizukuUtils singleton
        ShizukuUtils mockShizukuUtils = mock(ShizukuUtils.class);
        when(mockShizukuUtils.isShizukuAvailable()).thenReturn(true);
        when(mockShizukuUtils.hasShizukuPermission()).thenReturn(true);
        when(mockShizukuUtils.executeShellCommandWithResult(anyString())).thenReturn(mockResult);

        // Use reflection to set the singleton instance
        Method getInstance = ShizukuUtils.class.getDeclaredMethod("getInstance");
        getInstance.setAccessible(true);

        // Store the original instance to restore later
        ShizukuUtils originalInstance = (ShizukuUtils) getInstance.invoke(null);

        // Set our mock instance using reflection
        java.lang.reflect.Field instanceField = ShizukuUtils.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockShizukuUtils);

        try {
            // Act
            int result = AdbUtils.getAdbOverTcpPort();

            // Assert
            assertEquals("Should return the correct port number", 5555, result);
            System.out.println("[DEBUG_LOG] getAdbOverTcpPort with Shizuku: " + result);
        } finally {
            // Restore the original instance
            instanceField.set(null, originalInstance);
        }
    }

    @Test
    public void testGetAdbOverTcpPort_withShizukuAvailableButDisabled_returnsMinusOne() throws Exception {
        // Arrange
        ShizukuUtils.CommandResult mockResult = new ShizukuUtils.CommandResult();
        mockResult.output = "-1";
        mockResult.exitCode = 0;

        // Mock ShizukuUtils singleton
        ShizukuUtils mockShizukuUtils = mock(ShizukuUtils.class);
        when(mockShizukuUtils.isShizukuAvailable()).thenReturn(true);
        when(mockShizukuUtils.hasShizukuPermission()).thenReturn(true);
        when(mockShizukuUtils.executeShellCommandWithResult(anyString())).thenReturn(mockResult);

        // Use reflection to set the singleton instance
        Method getInstance = ShizukuUtils.class.getDeclaredMethod("getInstance");
        getInstance.setAccessible(true);

        // Store the original instance to restore later
        ShizukuUtils originalInstance = (ShizukuUtils) getInstance.invoke(null);

        // Set our mock instance using reflection
        java.lang.reflect.Field instanceField = ShizukuUtils.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, mockShizukuUtils);

        try {
            // Act
            int result = AdbUtils.getAdbOverTcpPort();

            // Assert
            assertEquals("Should return -1 when ADB over TCP is disabled", -1, result);
            System.out.println("[DEBUG_LOG] getAdbOverTcpPort with Shizuku (disabled): " + result);
        } finally {
            // Restore the original instance
            instanceField.set(null, originalInstance);
        }
    }
}
