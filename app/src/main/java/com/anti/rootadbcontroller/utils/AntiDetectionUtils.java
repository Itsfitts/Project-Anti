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
            } catch (PackageManager.NameNotFoundException ignored) {}
        }
        return false;
    }

    /**
     * Check telephony for emulator characteristics
     */
    private static boolean checkTelephony(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String networkOperator = tm.getNetworkOperatorName();
                return "Android".equals(networkOperator);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking telephony", e);
        }
        return false;
    }

    /**
     * Check if debugger is attached
     */
    private static boolean checkDebugger() {
        return android.os.Debug.isDebuggerConnected() ||
               android.os.Debug.waitingForDebugger();
    }

    /**
     * Check sensor availability (emulators often lack proper sensors)
     * This method checks for specific sensors that are commonly missing in emulators
     * but present in real devices
     */
    private static boolean checkSensors(Context context) {
        try {
            android.hardware.SensorManager sm = (android.hardware.SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);
            if (sm != null) {
                // Check for specific sensors that are typically present in real devices
                // but often missing or improperly implemented in emulators
                boolean hasAccelerometer = sm.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER) != null;
                boolean hasGyroscope = sm.getDefaultSensor(android.hardware.Sensor.TYPE_GYROSCOPE) != null;
                boolean hasProximity = sm.getDefaultSensor(android.hardware.Sensor.TYPE_PROXIMITY) != null;

                // Most real devices have at least accelerometer and proximity sensors
                // If both are missing, it's likely an emulator
                return !hasAccelerometer && !hasProximity;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking sensors", e);
        }
        return false;
    }

    /**
     * Detect analysis tools and security apps
     */
    public static boolean hasAnalysisTools(Context context) {
        String[] analysisApps = {
            // Removed Play Store as it's a legitimate app, not an analysis tool
            "de.robv.android.xposed.installer", // Xposed
            "com.topjohnwu.magisk", // Magisk
            "eu.chainfire.supersu", // SuperSU
            "com.noshufou.android.su", // Superuser
            "com.koushikdutta.superuser",
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine",
            "com.android.vending.billing.InAppBillingService.COIN"
        };

        PackageManager pm = context.getPackageManager();
        for (String app : analysisApps) {
            try {
                pm.getPackageInfo(app, 0);
                Log.d(TAG, "Found analysis tool: " + app);
                return true;
            } catch (PackageManager.NameNotFoundException ignored) {}
        }
        return false;
    }

    /**
     * Check for USB debugging and development settings
     */
    public static boolean isDeveloperModeEnabled(Context context) {
        try {
            int adbEnabled = Settings.Secure.getInt(context.getContentResolver(),
                Settings.Secure.ADB_ENABLED, 0);
            int devEnabled = Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

            return adbEnabled == 1 || devEnabled == 1;
        } catch (Exception e) {
            Log.e(TAG, "Error checking developer mode", e);
            return false;
        }
    }

    /**
     * Anti-forensics: Clear app traces and logs
     * This method clears temporary files and caches but preserves essential settings
     */
    public static void clearTraces(Context context) {
        try {
            // Clear app cache
            File cacheDir = context.getCacheDir();
            deleteRecursively(cacheDir);

            // Clear external cache
            File externalCache = context.getExternalCacheDir();
            if (externalCache != null) {
                deleteRecursively(externalCache);
            }

            // Don't clear shared preferences as they contain essential app settings
            // Instead, we could selectively clear specific non-essential preferences if needed
            // For example:
            // SharedPreferences prefs = context.getSharedPreferences("non_essential_prefs", Context.MODE_PRIVATE);
            // prefs.edit().clear().apply();

        } catch (Exception e) {
            Log.e(TAG, "Error clearing traces", e);
        }
    }

    /**
     * Recursively delete directory contents
     */
    private static void deleteRecursively(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children != null) {
                    for (File child : children) {
                        deleteRecursively(child);
                    }
                }
            }
            file.delete();
        }
    }

    /**
     * Detect if app is being analyzed
     * This method uses a more nuanced approach to determine if the app is likely
     * being analyzed in a security testing environment
     */
    public static boolean isBeingAnalyzed(Context context) {
        // Count the number of suspicious indicators
        int suspiciousFactors = 0;

        if (isEmulator(context)) suspiciousFactors++;
        if (hasAnalysisTools(context)) suspiciousFactors++;
        if (checkDebugger()) suspiciousFactors++;

        // Developer mode alone is not a strong indicator of analysis
        // Many legitimate users and developers have it enabled
        // We'll only count it if other factors are also present
        if (isDeveloperModeEnabled(context) && suspiciousFactors > 0) {
            suspiciousFactors++;
        }

        // Consider the app being analyzed if at least two suspicious factors are detected
        // This reduces false positives while still catching most analysis environments
        return suspiciousFactors >= 2;
    }
}
