package com.anti.rootadbcontroller.utils

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
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
     * Comprehensive emulator detection using multiple techniques.
     * @param context Application context.
     * @return true if running in an emulator, false otherwise.
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
     * Checks build properties for emulator characteristics.
     */
    private fun checkBuild(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
                "google_sdk" == Build.PRODUCT)
    }

    /**
     * Checks for emulator-specific files.
     */
    private fun checkFiles(): Boolean {
        return (KNOWN_PIPES.any { File(it).exists() } ||
                KNOWN_FILES.any { File(it).exists() } ||
                KNOWN_GENY_FILES.any { File(it).exists() })
    }

    /**
     * Checks for emulator packages.
     */
    private fun checkPackages(context: Context): Boolean {
        val pm = context.packageManager
        return KNOWN_PACKAGES.any {
            try {
                pm.getPackageInfo(it, 0)
                true
            } catch (_: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    /**
     * Checks telephony for emulator characteristics.
     */
    private fun checkTelephony(context: Context): Boolean {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return "Android".equals(tm?.networkOperatorName, ignoreCase = true)
    }

    /**
     * Checks if a debugger is attached.
     */
    fun checkDebugger(): Boolean {
        return android.os.Debug.isDebuggerConnected()
    }

    /**
     * Checks for the presence of sensors, which are often missing in emulators.
     */
    private fun checkSensors(context: Context): Boolean {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        return sm?.getSensorList(Sensor.TYPE_ALL)?.isNotEmpty() ?: false
    }

    /**
     * Detects common analysis tools and frameworks.
     * @param context Application context.
     * @return true if an analysis tool is detected.
     */
    fun detectAnalysisTools(context: Context): Boolean {
        val analysisPackages = listOf(
            "de.robv.android.xposed.installer",
            "io.va.exposed",
            "com.saurik.substrate",
            "com.topjohnwu.magisk"
        )
        val pm = context.packageManager
        return analysisPackages.any {
            try {
                pm.getPackageInfo(it, 0)
                Log.w(TAG, "Detected analysis tool: $it")
                true
            } catch (_: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    /**
     * Performs anti-forensics by clearing logs.
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
     * Checks if the device is in developer mode.
     * @param context Application context.
     * @return true if developer mode is enabled.
     */
    fun isDeveloperModeEnabled(context: Context): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) != 0
    }
}

