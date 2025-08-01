package com.anti.rootadbcontroller.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for detecting emulators, analysis tools, and implementing anti-forensics measures.
 * This helps the app avoid detection in sandbox environments and analysis platforms.
 */
public class AntiDetectionUtils {
    private static final String TAG = "AntiDetectionUtils";

    // Known emulator characteristics
    private static final String[] KNOWN_PIPES = {
        "/dev/socket/qemud", "/dev/qemu_pipe"
    };

    private static final String[] KNOWN_FILES = {
        "/system/lib/libc_malloc_debug_qemu.so",
        "/sys/qemu_trace", "/system/bin/qemu-props"
    };

    private static final String[] KNOWN_GENY_FILES = {
        "/dev/socket/genyd", "/dev/socket/baseband_genyd"
    };

    private static final String[] KNOWN_PACKAGES = {
        "com.google.android.launcher.layouts.genymotion",
        "com.bluestacks", "com.bignox.app", "com.vphone.launcher"
    };

    /**
     * Comprehensive emulator detection using multiple techniques
     * @param context Application context
     * @return true if running in an emulator, false otherwise
     */
    public static boolean isEmulator(Context context) {
        return checkBuild() ||
               checkFiles() ||
               checkPackages(context) ||
               checkTelephony(context) ||
               checkDebugger() ||
               checkSensors(context);
    }

    /**
     * Check build properties for emulator characteristics
     */
    private static boolean checkBuild() {
        return Build.FINGERPRINT.startsWith("generic") ||
               Build.FINGERPRINT.startsWith("unknown") ||
               Build.MODEL.contains("google_sdk") ||
               Build.MODEL.contains("Emulator") ||
               Build.MODEL.contains("Android SDK built for") ||
               Build.MANUFACTURER.contains("Genymotion") ||
               Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
               "google_sdk".equals(Build.PRODUCT);
    }

    /**
     * Check for emulator-specific files
     */
    private static boolean checkFiles() {
        for (String pipe : KNOWN_PIPES) {
            if (new File(pipe).exists()) return true;
        }
        for (String file : KNOWN_FILES) {
            if (new File(file).exists()) return true;
        }
        for (String file : KNOWN_GENY_FILES) {
            if (new File(file).exists()) return true;
        }
        return false;
    }

    /**
     * Check for emulator packages
     */
    private static boolean checkPackages(Context context) {
        PackageManager pm = context.getPackageManager();
        for (String packageName : KNOWN_PACKAGES) {
            try {
                pm.getPackageInfo(packageName, 0);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                // Package not found, continue checking other packages
            }
        }
        return false;
    }

    /**
     * Check telephony for emulator characteristics
     */
    private static boolean checkTelephony(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) return false;
        String networkOperator = tm.getNetworkOperatorName();
        return "Android".equalsIgnoreCase(networkOperator);
    }

    /**
     * Check if a debugger is attached
     */
    public static boolean checkDebugger() {
        return android.os.Debug.isDebuggerConnected();
    }

    /**
     * Check for the presence of sensors, which are often missing in emulators
     */
    private static boolean checkSensors(Context context) {
        android.hardware.SensorManager sm = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return sm != null && !sm.getSensorList(android.hardware.Sensor.TYPE_ALL).isEmpty();
    }

    /**
     * Detect common analysis tools and frameworks
     * @param context Application context
     * @return true if an analysis tool is detected
     */
    public static boolean detectAnalysisTools(Context context) {
        List<String> analysisPackages = Arrays.asList(
            "de.robv.android.xposed.installer",
            "io.va.exposed",
            "com.saurik.substrate",
            "com.topjohnwu.magisk"
        );
        PackageManager pm = context.getPackageManager();
        for (String packageName : analysisPackages) {
            try {
                pm.getPackageInfo(packageName, 0);
                Log.w(TAG, "Detected analysis tool: " + packageName);
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                // Package not found, continue checking other packages
            }
        }
        return false;
    }

    /**
     * Perform anti-forensics by clearing logs
     */
    public static void clearLogs() {
        try {
            Runtime.getRuntime().exec("logcat -c");
            Log.i(TAG, "Cleared application logs to hinder analysis.");
        } catch (Exception e) {
            Log.e(TAG, "Failed to clear logs", e);
        }
    }

    /**
     * Check if the device is in developer mode
     * @param context Application context
     * @return true if developer mode is enabled
     */
    public static boolean isDeveloperModeEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0;
    }
}
