package com.anti.rootadbcontroller.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowSettings;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AntiDetectionUtils}
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28})
public class AntiDetectionUtilsTest {

    @Mock
    private Context mockContext;

    @Mock
    private PackageManager mockPackageManager;

    @Mock
    private TelephonyManager mockTelephonyManager;

    @Mock
    private SensorManager mockSensorManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Setup context mocks
        when(mockContext.getPackageManager()).thenReturn(mockPackageManager);
        when(mockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
        when(mockContext.getSystemService(Context.SENSOR_SERVICE)).thenReturn(mockSensorManager);
    }

    @Test
    public void testIsEmulator_withEmulatorBuildProps_returnsTrue() {
        // Arrange
        // Create a spy of AntiDetectionUtils to mock the private methods
        AntiDetectionUtils antiDetectionUtilsSpy = spy(AntiDetectionUtils.class);

        // Mock the private methods
        doReturn(true).when(antiDetectionUtilsSpy).checkBuild();
        doReturn(false).when(antiDetectionUtilsSpy).checkFiles();
        doReturn(false).when(antiDetectionUtilsSpy).checkPackages(mockContext);
        doReturn(false).when(antiDetectionUtilsSpy).checkTelephony(mockContext);
        doReturn(false).when(antiDetectionUtilsSpy).checkDebugger();
        doReturn(false).when(antiDetectionUtilsSpy).checkSensors(mockContext);

        // Act
        boolean result = antiDetectionUtilsSpy.isEmulator(mockContext);

        // Assert
        assertTrue("Should return true when build properties indicate an emulator", result);
        System.out.println("[DEBUG_LOG] isEmulator with emulator build props: " + result);
    }

    @Test
    public void testIsEmulator_withEmulatorFiles_returnsTrue() {
        // Arrange
        // Create a spy of AntiDetectionUtils to mock the private methods
        AntiDetectionUtils antiDetectionUtilsSpy = spy(AntiDetectionUtils.class);

        // Mock the private methods
        doReturn(false).when(antiDetectionUtilsSpy).checkBuild();
        doReturn(true).when(antiDetectionUtilsSpy).checkFiles();
        doReturn(false).when(antiDetectionUtilsSpy).checkPackages(mockContext);
        doReturn(false).when(antiDetectionUtilsSpy).checkTelephony(mockContext);
        doReturn(false).when(antiDetectionUtilsSpy).checkDebugger();
        doReturn(false).when(antiDetectionUtilsSpy).checkSensors(mockContext);

        // Act
        boolean result = antiDetectionUtilsSpy.isEmulator(mockContext);

        // Assert
        assertTrue("Should return true when emulator files are detected", result);
        System.out.println("[DEBUG_LOG] isEmulator with emulator files: " + result);
    }

    @Test
    public void testIsEmulator_withNoEmulatorIndicators_returnsFalse() {
        // Arrange
        // Create a spy of AntiDetectionUtils to mock the private methods
        AntiDetectionUtils antiDetectionUtilsSpy = spy(AntiDetectionUtils.class);

        // Mock the private methods
        doReturn(false).when(antiDetectionUtilsSpy).checkBuild();
        doReturn(false).when(antiDetectionUtilsSpy).checkFiles();
        doReturn(false).when(antiDetectionUtilsSpy).checkPackages(mockContext);
        doReturn(false).when(antiDetectionUtilsSpy).checkTelephony(mockContext);
        doReturn(false).when(antiDetectionUtilsSpy).checkDebugger();
        doReturn(false).when(antiDetectionUtilsSpy).checkSensors(mockContext);

        // Act
        boolean result = antiDetectionUtilsSpy.isEmulator(mockContext);

        // Assert
        assertFalse("Should return false when no emulator indicators are detected", result);
        System.out.println("[DEBUG_LOG] isEmulator with no emulator indicators: " + result);
    }

    @Test
    public void testHasAnalysisTools_withAnalysisToolsInstalled_returnsTrue() throws Exception {
        // Arrange
        // Mock the package manager to return info for an analysis tool
        when(mockPackageManager.getPackageInfo(eq("com.topjohnwu.magisk"), anyInt()))
                .thenReturn(mock(android.content.pm.PackageInfo.class));

        // Act
        boolean result = AntiDetectionUtils.hasAnalysisTools(mockContext);

        // Assert
        assertTrue("Should return true when analysis tools are installed", result);
        System.out.println("[DEBUG_LOG] hasAnalysisTools with analysis tools installed: " + result);
    }

    @Test
    public void testHasAnalysisTools_withNoAnalysisToolsInstalled_returnsFalse() throws Exception {
        // Arrange
        // Mock the package manager to throw NameNotFoundException for all analysis tools
        when(mockPackageManager.getPackageInfo(anyString(), anyInt()))
                .thenThrow(new PackageManager.NameNotFoundException());

        // Act
        boolean result = AntiDetectionUtils.hasAnalysisTools(mockContext);

        // Assert
        assertFalse("Should return false when no analysis tools are installed", result);
        System.out.println("[DEBUG_LOG] hasAnalysisTools with no analysis tools installed: " + result);
    }

    @Test
    public void testIsDeveloperModeEnabled_withAdbEnabled_returnsTrue() throws Exception {
        // Arrange
        // Create a mock ContentResolver
        android.content.ContentResolver mockResolver = mock(android.content.ContentResolver.class);
        when(mockContext.getContentResolver()).thenReturn(mockResolver);

        // Mock Settings.Secure.getInt to return 1 for ADB_ENABLED
        Settings.Secure.putInt(mockResolver, Settings.Secure.ADB_ENABLED, 1);

        // Act
        boolean result = AntiDetectionUtils.isDeveloperModeEnabled(mockContext);

        // Assert
        assertTrue("Should return true when ADB is enabled", result);
        System.out.println("[DEBUG_LOG] isDeveloperModeEnabled with ADB enabled: " + result);
    }

    @Test
    public void testIsDeveloperModeEnabled_withDevSettingsEnabled_returnsTrue() throws Exception {
        // Arrange
        // Create a mock ContentResolver
        android.content.ContentResolver mockResolver = mock(android.content.ContentResolver.class);
        when(mockContext.getContentResolver()).thenReturn(mockResolver);

        // Mock Settings.Global.getInt to return 1 for DEVELOPMENT_SETTINGS_ENABLED
        Settings.Global.putInt(mockResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 1);

        // Act
        boolean result = AntiDetectionUtils.isDeveloperModeEnabled(mockContext);

        // Assert
        assertTrue("Should return true when development settings are enabled", result);
        System.out.println("[DEBUG_LOG] isDeveloperModeEnabled with dev settings enabled: " + result);
    }

    @Test
    public void testIsDeveloperModeEnabled_withNothingEnabled_returnsFalse() throws Exception {
        // Arrange
        // Create a mock ContentResolver
        android.content.ContentResolver mockResolver = mock(android.content.ContentResolver.class);
        when(mockContext.getContentResolver()).thenReturn(mockResolver);

        // Mock Settings.Secure.getInt and Settings.Global.getInt to return 0
        Settings.Secure.putInt(mockResolver, Settings.Secure.ADB_ENABLED, 0);
        Settings.Global.putInt(mockResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

        // Act
        boolean result = AntiDetectionUtils.isDeveloperModeEnabled(mockContext);

        // Assert
        assertFalse("Should return false when neither ADB nor dev settings are enabled", result);
        System.out.println("[DEBUG_LOG] isDeveloperModeEnabled with nothing enabled: " + result);
    }

    @Test
    public void testIsBeingAnalyzed_withMultipleIndicators_returnsTrue() {
        // Arrange
        // Create a spy of AntiDetectionUtils to mock the methods
        AntiDetectionUtils antiDetectionUtilsSpy = spy(AntiDetectionUtils.class);

        // Mock the methods
        doReturn(true).when(antiDetectionUtilsSpy).isEmulator(mockContext);
        doReturn(true).when(antiDetectionUtilsSpy).hasAnalysisTools(mockContext);
        doReturn(false).when(antiDetectionUtilsSpy).checkDebugger();
        doReturn(true).when(antiDetectionUtilsSpy).isDeveloperModeEnabled(mockContext);

        // Act
        boolean result = antiDetectionUtilsSpy.isBeingAnalyzed(mockContext);

        // Assert
        assertTrue("Should return true when multiple analysis indicators are detected", result);
        System.out.println("[DEBUG_LOG] isBeingAnalyzed with multiple indicators: " + result);
    }

    @Test
    public void testIsBeingAnalyzed_withSingleIndicator_returnsFalse() {
        // Arrange
        // Create a spy of AntiDetectionUtils to mock the methods
        AntiDetectionUtils antiDetectionUtilsSpy = spy(AntiDetectionUtils.class);

        // Mock the methods
        doReturn(true).when(antiDetectionUtilsSpy).isEmulator(mockContext);
        doReturn(false).when(antiDetectionUtilsSpy).hasAnalysisTools(mockContext);
        doReturn(false).when(antiDetectionUtilsSpy).checkDebugger();
        doReturn(false).when(antiDetectionUtilsSpy).isDeveloperModeEnabled(mockContext);

        // Act
        boolean result = antiDetectionUtilsSpy.isBeingAnalyzed(mockContext);

        // Assert
        assertFalse("Should return false when only one analysis indicator is detected", result);
        System.out.println("[DEBUG_LOG] isBeingAnalyzed with single indicator: " + result);
    }
}
