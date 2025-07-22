package com.anti.rootadbcontroller.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.util.Log
import com.anti.rootadbcontroller.services.RemoteAdbService
import com.anti.rootadbcontroller.services.ShizukuCallbacks
import com.anti.rootadbcontroller.services.ShizukuManagerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * Utility class for automating features once access is gained.
 */
object AutomationUtils {
    private const val TAG = "AutomationUtils"
    private const val PREFS_NAME = "automation_prefs"
    private const val KEY_AUTO_REMOTE_ADB = "auto_remote_adb"
    private const val KEY_AUTO_SYSTEM_INFO = "auto_system_info"
    private const val KEY_AUTO_PACKAGE_LIST = "auto_package_list"
    private const val KEY_AUTO_NETWORK_INFO = "auto_network_info"

    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Executes automated tasks when root access is gained.
     *
     * @param context Application context.
     */
    fun executeRootAutomations(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        scope.launch {
            Log.d(TAG, "Executing root automations")
            if (prefs.getBoolean(KEY_AUTO_REMOTE_ADB, false)) {
                startRemoteAdbService(context)
            }
            if (prefs.getBoolean(KEY_AUTO_SYSTEM_INFO, false)) {
                collectSystemInfo(context)
            }
            if (prefs.getBoolean(KEY_AUTO_PACKAGE_LIST, false)) {
                collectPackageList(context)
            }
            if (prefs.getBoolean(KEY_AUTO_NETWORK_INFO, false)) {
                collectNetworkInfo(context)
            }
        }
    }

    /**
     * Executes automated tasks when Shizuku access is gained.
     *
     * @param context Application context.
     * @param service ShizukuManagerService instance.
     */
    fun executeShizukuAutomations(context: Context, service: ShizukuManagerService?) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        scope.launch {
            Log.d(TAG, "Executing Shizuku automations")
            if (prefs.getBoolean(KEY_AUTO_REMOTE_ADB, false)) {
                startRemoteAdbService(context)
            }
            if (prefs.getBoolean(KEY_AUTO_SYSTEM_INFO, false)) {
                service?.getSystemInfo { success, _, result ->
                    if (success) {
                        saveToFile(context, "system_info.txt", result)
                    }
                }
            }
            if (prefs.getBoolean(KEY_AUTO_PACKAGE_LIST, false)) {
                service?.listPackages(true) { success, _, result ->
                    if (success) {
                        saveToFile(context, "package_list.txt", result)
                    }
                }
            }
            if (prefs.getBoolean(KEY_AUTO_NETWORK_INFO, false)) {
                service?.performNetworkOperation("netstat") { success, data ->
                    if (success) {
                        saveToFile(context, "network_info.txt", data)
                    }
                }
            }
        }
    }

    private fun startRemoteAdbService(context: Context) {
        val intent = Intent(context, RemoteAdbService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        Log.i(TAG, "Started RemoteAdbService for automation.")
    }

    private fun collectSystemInfo(context: Context) {
        scope.launch {
            try {
                val props = AdbUtils.executeCommand("getprop")
                saveToFile(context, "system_info_root.txt", props)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to collect system info with root", e)
            }
        }
    }

    private fun collectPackageList(context: Context) {
        scope.launch {
            try {
                val packages = AdbUtils.executeCommand("pm list packages -f")
                saveToFile(context, "package_list_root.txt", packages)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to collect package list with root", e)
            }
        }
    }

    private fun collectNetworkInfo(context: Context) {
        scope.launch {
            try {
                val netstat = AdbUtils.executeCommand("netstat -tuln")
                saveToFile(context, "network_info_root.txt", netstat)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to collect network info with root", e)
            }
        }
    }

    private fun saveToFile(context: Context, fileName: String, content: String) {
        val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (dir == null) {
            Log.e(TAG, "Failed to get external files directory.")
            return
        }
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, fileName)
        try {
            FileWriter(file, true).use { writer -> // Append mode
                writer.append(content).append("\n\n")
            }
            Log.i(TAG, "Saved data to ${file.absolutePath}")
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save data to file: $fileName", e)
        }
    }

    /**
     * Sets an automation setting.
     */
    fun setAutomation(context: Context, key: String, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, enabled).apply()
    }

    /**
     * Gets an automation setting.
     */
    fun getAutomation(context: Context, key: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, false)
    }

    /**
     * Gets all automation keys.
     */
    fun getAllAutomationKeys(): List<String> {
        return listOf(
            KEY_AUTO_REMOTE_ADB,
            KEY_AUTO_SYSTEM_INFO,
            KEY_AUTO_PACKAGE_LIST,
            KEY_AUTO_NETWORK_INFO
        )
    }

    /**
     * Gets the key name for display.
     */
    fun getKeyDisplayName(key: String): String {
        return when (key) {
            KEY_AUTO_REMOTE_ADB -> "Remote ADB Service"
            KEY_AUTO_SYSTEM_INFO -> "System Information Collection"
            KEY_AUTO_PACKAGE_LIST -> "Package List Collection"
            KEY_AUTO_NETWORK_INFO -> "Network Information Collection"
            else -> key
        }
    }
}

