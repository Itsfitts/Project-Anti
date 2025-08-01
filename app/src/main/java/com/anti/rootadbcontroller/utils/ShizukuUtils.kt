package com.anti.rootadbcontroller.utils

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import rikka.shizuku.Shizuku
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Utility class for Shizuku integration.
 * Provides methods to interact with system APIs through Shizuku.
 */
class ShizukuUtils private constructor() {

    private val permissionResultListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            Log.d(TAG, "Permission result: $requestCode = $grantResult")
            if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
                val granted = grantResult == PackageManager.PERMISSION_GRANTED
                Log.i(TAG, "Shizuku permission " + if (granted) "granted" else "denied")
            }
        }

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        Log.i(TAG, "Shizuku binder received")
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        Log.w(TAG, "Shizuku binder died")
        isInitialized = false
    }

    /**
     * Initializes Shizuku integration.
     */
    fun initialize(): Boolean {
        if (isInitialized) {
            return true
        }
        return try {
            Shizuku.addRequestPermissionResultListener(permissionResultListener)
            Shizuku.addBinderReceivedListener(binderReceivedListener)
            Shizuku.addBinderDeadListener(binderDeadListener)
            isInitialized = true
            Log.i(TAG, "Shizuku initialized successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Shizuku", e)
            false
        }
    }

    /**
     * Checks if Shizuku is available and running.
     */
    val isShizukuAvailable: Boolean
        get() = try {
            Shizuku.pingBinder()
        } catch (e: Exception) {
            Log.d(TAG, "Shizuku not available: ${e.message}")
            false
        }

    /**
     * Checks if we have Shizuku permission.
     */
    fun hasShizukuPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Requests Shizuku permission.
     */
    fun requestShizukuPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED
        ) {
            if (Shizuku.shouldShowRequestPermissionRationale()) {
                Log.i(TAG, "Should show permission rationale")
            }
            Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * Executes a shell command with result.
     */
    fun executeShellCommandWithResult(command: String): CommandResult {
        if (!isShizukuAvailable || !hasShizukuPermission()) {
            Log.w(TAG, "Shizuku not available or permission denied")
            return CommandResult(false, "", "Shizuku not available or permission denied", -1)
        }
        return try {
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
            readProcessOutput(process)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute command via Shizuku: $command", e)
            CommandResult(false, "", e.message ?: "Unknown error", -1)
        }
    }

    /**
     * Installs an APK via Shizuku.
     */
    fun installApk(apkPath: String): Boolean {
        val result = executeShellCommandWithResult("pm install -r $apkPath")
        return result.isSuccess && result.output.contains("Success")
    }

    /**
     * Uninstalls a package via Shizuku.
     */
    fun uninstallPackage(packageName: String): Boolean {
        val result = executeShellCommandWithResult("pm uninstall $packageName")
        return result.isSuccess && result.output.contains("Success")
    }

    /**
     * Grants a permission to a package.
     */
    fun grantPermission(packageName: String, permission: String): Boolean {
        val result = executeShellCommandWithResult("pm grant $packageName $permission")
        return result.isSuccess
    }

    /**
     * Revokes a permission from a package.
     */
    fun revokePermission(packageName: String, permission: String): Boolean {
        val result = executeShellCommandWithResult("pm revoke $packageName $permission")
        return result.isSuccess
    }

    /**
     * Enables/disables an app component.
     */
    fun setComponentEnabled(packageName: String, componentName: String, enabled: Boolean): Boolean {
        val state = if (enabled) "enable" else "disable"
        val result = executeShellCommandWithResult("pm $state $packageName/$componentName")
        return result.isSuccess
    }

    /**
     * Takes a screenshot via Shizuku.
     */
    fun takeScreenshot(outputPath: String): Boolean {
        val result = executeShellCommandWithResult("screencap -p $outputPath")
        return result.isSuccess
    }

    /**
     * Reads process output.
     */
    private fun readProcessOutput(process: Process): CommandResult {
        val output = StringBuilder()
        val error = StringBuilder()
        return try {
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    output.append(line).append("\n")
                }
            }
            BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    error.append(line).append("\n")
                }
            }
            val exitCode = process.waitFor()
            CommandResult(exitCode == 0, output.toString().trim(), error.toString().trim(), exitCode)
        } catch (e: IOException) {
            Log.e(TAG, "Error reading process output", e)
            CommandResult(false, "", e.message ?: "Unknown error", -1)
        }
    }

    /**
     * Cleans up resources.
     */
    fun cleanup() {
        if (isInitialized) {
            try {
                Shizuku.removeRequestPermissionResultListener(permissionResultListener)
                Shizuku.removeBinderReceivedListener(binderReceivedListener)
                Shizuku.removeBinderDeadListener(binderDeadListener)
                isInitialized = false
                Log.i(TAG, "Shizuku cleanup completed")
            } catch (e: Exception) {
                Log.e(TAG, "Error during cleanup", e)
            }
        }
    }

    /**
     * Command execution result.
     */
    data class CommandResult(
        val isSuccess: Boolean,
        val output: String,
        val error: String,
        val exitCode: Int,
    )

    companion object {
        private const val TAG = "ShizukuUtils"
        private const val SHIZUKU_PERMISSION_REQUEST_CODE = 1001
        private var isInitialized = false

        @Volatile
        private var instance: ShizukuUtils? = null

        @JvmStatic
        fun getInstance(): ShizukuUtils =
            instance ?: synchronized(this) {
                instance ?: ShizukuUtils().also { instance = it }
            }
    }
}
