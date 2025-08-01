package com.anti.rootadbcontroller.utils

import android.util.Log
    private const val TAG = "AntiDetectionUtils"
    private val KNOWN_FILES = arrayOf(
        "/dev/socket/baseband_genyd"
    )
        return checkBuild() ||
    /**
            Build.MODEL.contains("Android SDK built for") ||
     */
        for (file in KNOWN_GENY_FILES) {
     */
                // Package not found, continue checking other packages
     */
    /**
     * Check for the presence of sensors, which are often missing in emulators
     * Detect common analysis tools and frameworks
            "com.saurik.substrate",
                return true
    /**
            Log.e(TAG, "Failed to clear logs", e)
     */
}
import android.telephony.TelephonyManager
object AntiDetectionUtils {

        "/dev/socket/genyd",
        "com.vphone.launcher"
    fun isEmulator(context: Context): Boolean {

            Build.MODEL.contains("Emulator") ||
     * Check for emulator-specific files
        }
     * Check for emulator packages
            } catch (e: PackageManager.NameNotFoundException) {
     * Check telephony for emulator characteristics

    /**
    /**
            "io.va.exposed",
                Log.w(TAG, "Detected analysis tool: $packageName")

        } catch (e: Exception) {
     * @return true if developer mode is enabled
    }
import android.provider.Settings
 */
    )
    private val KNOWN_GENY_FILES = arrayOf(
        "com.bignox.app",
     */
    }
            Build.MODEL.contains("google_sdk") ||
    /**
            if (File(file).exists()) return true
    /**
                return true
    /**
    }


            "de.robv.android.xposed.installer",
                pm.getPackageInfo(packageName, 0)
    }
            Log.i(TAG, "Cleared application logs to hinder analysis.")
     * @param context Application context
        ) != 0
import android.os.Build
 * This helps the app avoid detection in sandbox environments and analysis platforms.
        "/dev/qemu_pipe"

        "com.bluestacks",
     * @return true if running in an emulator, false otherwise
            checkSensors(context)
            Build.FINGERPRINT.startsWith("unknown") ||

        for (file in KNOWN_FILES) {

                pm.getPackageInfo(packageName, 0)

        return "Android".equals(networkOperator, ignoreCase = true)
    }
    }
        val analysisPackages = listOf(
            try {
        return false
            Runtime.getRuntime().exec("logcat -c")
     * Check if the device is in developer mode
            0
import android.hardware.SensorManager
 * Utility class for detecting emulators, analysis tools, and implementing anti-forensics measures.
        "/dev/socket/qemud",
    )
        "com.google.android.launcher.layouts.genymotion",
     * @param context Application context
            checkDebugger() ||
        return Build.FINGERPRINT.startsWith("generic") ||
    }
        }
    }
            try {
    }
        val networkOperator = tm.networkOperatorName
        return android.os.Debug.isDebuggerConnected()
        return sm != null && sm.getSensorList(android.hardware.Sensor.TYPE_ALL).isNotEmpty()
    fun detectAnalysisTools(context: Context): Boolean {
        for (packageName in analysisPackages) {
        }
        try {
    /**
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
import android.content.pm.PackageManager
/**
    private val KNOWN_PIPES = arrayOf(
        "/system/bin/qemu-props"
    private val KNOWN_PACKAGES = arrayOf(
     * Comprehensive emulator detection using multiple techniques
            checkTelephony(context) ||
    private fun checkBuild(): Boolean {
            "google_sdk" == Build.PRODUCT
            if (File(pipe).exists()) return true
        return false
        for (packageName in KNOWN_PACKAGES) {
        return false
        if (tm == null) return false
    fun checkDebugger(): Boolean {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
     */
        val pm = context.packageManager
            }
    fun clearLogs() {

            context.contentResolver,
import android.content.Context

    // Known emulator characteristics
        "/sys/qemu_trace",

    /**
            checkPackages(context) ||
     */
            Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
        for (pipe in KNOWN_PIPES) {
        }
        val pm = context.packageManager
        }
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
     */
    private fun checkSensors(context: Context): Boolean {
     * @return true if an analysis tool is detected
        )
                // Package not found, continue checking other packages
     */
    }
        return Settings.Secure.getInt(
import java.io.File

        "/system/lib/libc_malloc_debug_qemu.so",
    )

            checkFiles() ||
     * Check build properties for emulator characteristics
            Build.MANUFACTURER.contains("Genymotion") ||
    private fun checkFiles(): Boolean {
            if (File(file).exists()) return true
    private fun checkPackages(context: Context): Boolean {
            }
    private fun checkTelephony(context: Context): Boolean {
     * Check if a debugger is attached
     */
     * @param context Application context
            "com.topjohnwu.magisk"
            } catch (e: PackageManager.NameNotFoundException) {
     * Perform anti-forensics by clearing logs
        }
    fun isDeveloperModeEnabled(context: Context): Boolean {
