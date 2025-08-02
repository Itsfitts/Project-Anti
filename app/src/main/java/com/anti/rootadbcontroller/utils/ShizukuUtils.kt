package com.anti.rootadbcontroller.utils

import java.io.InputStreamReader
class ShizukuUtils private constructor() {
                Log.i(TAG, "Shizuku permission " + if (granted) "granted" else "denied")

     * Initializes Shizuku integration.
            Shizuku.addRequestPermissionResultListener(permissionResultListener)
            Log.e(TAG, "Failed to initialize Shizuku", e)
     */
        }
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
     * Requests Shizuku permission.
                Log.i(TAG, "Should show permission rationale")
     * Executes a shell command with result.
        return try {
    }
        return result.isSuccess && result.output.contains("Success")
        val result = executeShellCommandWithResult("pm uninstall $packageName")
    fun grantPermission(packageName: String, permission: String): Boolean {
     */
     * Enables/disables an app component.

    }
        val error = StringBuilder()
            }
            val exitCode = process.waitFor()

                Shizuku.removeRequestPermissionResultListener(permissionResultListener)
            }
    data class CommandResult(
    companion object {

}
import java.io.IOException
 */
                val granted = grantResult == PackageManager.PERMISSION_GRANTED
    }
    /**
        return try {
        } catch (e: Exception) {
     * Checks if Shizuku is available and running.
            false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    /**
            if (Shizuku.shouldShowRequestPermissionRationale()) {
    /**
        }
        }
        val result = executeShellCommandWithResult("pm install -r $apkPath")
    fun uninstallPackage(packageName: String): Boolean {
     */
     * Revokes a permission from a package.
    /**
    }
        return result.isSuccess
        val output = StringBuilder()
                }
            }
    }
            try {
                Log.e(TAG, "Error during cleanup", e)
     */

        private var instance: ShizukuUtils? = null
    }
import java.io.BufferedReader
 * Provides methods to interact with system APIs through Shizuku.
            if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
        Log.i(TAG, "Shizuku binder received")

        }
            true
    /**
            Log.d(TAG, "Shizuku not available: ${e.message}")
    fun hasShizukuPermission(): Boolean {

        ) {

            return CommandResult(false, "", "Shizuku not available or permission denied", -1)
            CommandResult(false, "", e.message ?: "Unknown error", -1)
    fun installApk(apkPath: String): Boolean {
     */
     * Grants a permission to a package.
    /**

        return result.isSuccess
        val result = executeShellCommandWithResult("screencap -p $outputPath")
    private fun readProcessOutput(process: Process): CommandResult {
                    output.append(line).append("\n")
                }
        }
        if (isInitialized) {
            } catch (e: Exception) {
     * Command execution result.
    )
        @Volatile
            }
import android.util.Log
 * Utility class for Shizuku integration.
            Log.d(TAG, "Permission result: $requestCode = $grantResult")
    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
    }
            return true
            Log.i(TAG, "Shizuku initialized successfully")

        } catch (e: Exception) {
     */
    }
            Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED
    }
            Log.w(TAG, "Shizuku not available or permission denied")
            Log.e(TAG, "Failed to execute command via Shizuku: $command", e)
     */
     * Uninstalls a package via Shizuku.
    /**

    }
        val result = executeShellCommandWithResult("pm $state $packageName/$componentName")
    fun takeScreenshot(outputPath: String): Boolean {
     */
                while (reader.readLine().also { line = it } != null) {
                    error.append(line).append("\n")
            CommandResult(false, "", e.message ?: "Unknown error", -1)
    fun cleanup() {
                Log.i(TAG, "Shizuku cleanup completed")
    /**
        val exitCode: Int

                instance ?: ShizukuUtils().also { instance = it }
import android.os.Build
/**
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->

        isInitialized = false
        if (isInitialized) {
            isInitialized = true
    }
            Shizuku.pingBinder()
     * Checks if we have Shizuku permission.
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
        }
        if (!isShizukuAvailable || !hasShizukuPermission()) {
        } catch (e: Exception) {
     * Installs an APK via Shizuku.
    /**

    }
        return result.isSuccess
        val state = if (enabled) "enable" else "disable"
     */
     * Reads process output.
                var line: String?
                while (reader.readLine().also { line = it } != null) {
            Log.e(TAG, "Error reading process output", e)
     */
                isInitialized = false

        val error: String,
        private var isInitialized = false
            instance ?: synchronized(this) {
import android.content.pm.PackageManager

    private val permissionResultListener =
        }
        Log.w(TAG, "Shizuku binder died")
    fun initialize(): Boolean {
            Shizuku.addBinderDeadListener(binderDeadListener)
        }
        get() = try {
    /**
            true
    fun requestShizukuPermission() {
            Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE)
    fun executeShellCommandWithResult(command: String): CommandResult {
            readProcessOutput(process)
    /**

    }
        return result.isSuccess
        val result = executeShellCommandWithResult("pm revoke $packageName $permission")
    fun setComponentEnabled(packageName: String, componentName: String, enabled: Boolean): Boolean {
     * Takes a screenshot via Shizuku.
    /**
            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
        } catch (e: IOException) {
     * Cleans up resources.
                Shizuku.removeBinderDeadListener(binderDeadListener)
    }
        val output: String,
        private const val SHIZUKU_PERMISSION_REQUEST_CODE = 1001
        fun getInstance(): ShizukuUtils =
import rikka.shizuku.Shizuku

            }
    private val binderDeadListener = Shizuku.OnBinderDeadListener {
     */
            Shizuku.addBinderReceivedListener(binderReceivedListener)
            false
    val isShizukuAvailable: Boolean

        } else {
     */
            }
     */
            val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)

    }
        return result.isSuccess && result.output.contains("Success")
        val result = executeShellCommandWithResult("pm grant $packageName $permission")
    fun revokePermission(packageName: String, permission: String): Boolean {
     */
    /**

        return try {
            BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
            CommandResult(exitCode == 0, output.toString().trim(), error.toString().trim(), exitCode)
    /**
                Shizuku.removeBinderReceivedListener(binderReceivedListener)
        }
        val isSuccess: Boolean,
        private const val TAG = "ShizukuUtils"
        @JvmStatic
