package com.anti.rootadbcontroller.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import java.io.File

/**
 * Utility class for detecting emulators, analysis tools, and implementing anti-forensics measures.
 * This helps the app avoid detection in sandbox environments and analysis platforms.
 */
object AntiDetectionUtils {
    private const val TAG = "AntiDetectionUtils"

    // Known emulator characteristics
    private val KNOWN_PIPES = arrayOf(
        "/dev/socket/qemud", "/dev/qemu_pipe"
    )

    private val KNOWN_FILES = arrayOf(
        "/system/lib/libc_malloc_debug_qemu.so",
        "/sys/qemu_trace", "/system/bin/qemu-props"
    )

    private val KNOWN_GENY_FILES = arrayOf(
        "/dev/socket/genyd", "/dev/socket/baseband_genyd"
    )

    private val KNOWN_PACKAGES = arrayOf(
        "com.google.android.launcher.layouts.genymotion",
        "com.bluestacks", "com.bignox.app", "com.vphone.launcher"
    )

    /**
     * Comprehensive emulator detection using multiple techniques
     * @param context Application context
     * @return true if running in an emulator, false otherwise
     */
    fun isEmulator(context: Context): Boolean {
        return checkBuild() ||
                checkFiles() ||
                checkPackages(context) ||
                checkTelephony(context) ||
                checkDebugger() ||
                checkSensors(context)
    }

    /**
     * Check build properties for emulator characteristics
     */
    private fun checkBuild(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
                "google_sdk" == Build.PRODUCT
    }

    /**
     * Check for emulator-specific files
     */
    private fun checkFiles(): Boolean {
        for (pipe in KNOWN_PIPES) {
            if (File(pipe).exists()) return true
        }
        for (file in KNOWN_FILES) {
            if (File(file).exists()) return true
        }
        for (file in KNOWN_GENY_FILES) {
            if (File(file).exists()) return true
        }
        return false
    }

    /**
     * Check for emulator packages
     */
    private fun checkPackages(context: Context): Boolean {
        val pm = context.packageManager
        for (packageName in KNOWN_PACKAGES) {
            try {
                pm.getPackageInfo(packageName, 0)
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                // Package not found, continue checking other packages
            }
        }
        return false
    }

    /**
     * Check telephony for emulator characteristics
     */
    private fun checkTelephony(context: Context): Boolean {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        if (tm == null) return false
        val networkOperator = tm.networkOperatorName
        return "Android".equals(networkOperator, ignoreCase = true)
    }

    /**
     * Check if a debugger is attached
     */
    fun checkDebugger(): Boolean {
        return android.os.Debug.isDebuggerConnected()
    }

    /**
     * Check for the presence of sensors, which are often missing in emulators
     */
    private fun checkSensors(context: Context): Boolean {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        return sm != null && sm.getSensorList(android.hardware.Sensor.TYPE_ALL).isNotEmpty()
    }

    /**
     * Detect common analysis tools and frameworks
     * @param context Application context
     * @return true if an analysis tool is detected
     */
    fun detectAnalysisTools(context: Context): Boolean {
        val analysisPackages = listOf(
            "de.robv.android.xposed.installer",
            "io.va.exposed",
            "com.saurik.substrate",
            "com.topjohnwu.magisk"
        )
        val pm = context.packageManager
        for (packageName in analysisPackages) {
            try {
                pm.getPackageInfo(packageName, 0)
                Log.w(TAG, "Detected analysis tool: $packageName")
                return true
            } catch (e: PackageManager.NameNotFoundException) {
                // Package not found, continue checking other packages
            }
        }
        return false
    }

    /**
     * Perform anti-forensics by clearing logs
     */
    fun clearLogs() {
        try {
            Runtime.getRuntime().exec("logcat -c")
            Log.i(TAG, "Cleared application logs to hinder analysis.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear logs", e)
        }
    }

    /**
     * Check if the device is in developer mode
     * @param context Application context
     * @return true if developer mode is enabled
     */
    fun isDeveloperModeEnabled(context: Context): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) != 0
    }
}