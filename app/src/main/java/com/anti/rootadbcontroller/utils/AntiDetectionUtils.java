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
     */
    private static boolean checkSensors(Context context) {
        try {
            android.hardware.SensorManager sm = (android.hardware.SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);
            if (sm != null) {
                List<android.hardware.Sensor> sensors = sm.getSensorList(android.hardware.Sensor.TYPE_ALL);
                return sensors.size() < 5; // Real devices typically have more sensors
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
            "com.android.vending", // Play Store (sometimes removed in analysis)
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

            // Clear shared preferences
            File prefsDir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
            deleteRecursively(prefsDir);

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
     */
    public static boolean isBeingAnalyzed(Context context) {
        return isEmulator(context) ||
               hasAnalysisTools(context) ||
               isDeveloperModeEnabled(context) ||
               checkDebugger();
    }
}
