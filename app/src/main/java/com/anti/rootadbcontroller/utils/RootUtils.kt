package com.anti.rootadbcontroller.utils

import android.util.Log
import eu.chainfire.libsuperuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A utility class for executing root commands and performing various system-level operations
 * that require elevated privileges. This class provides a simplified interface for interacting
 * with the system shell as the root user.
 */
object RootUtils {
    private const val TAG = "RootUtils"
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Checks if root access is available on the device.
     * @return true if root is available, false otherwise.
     */
    fun isRootAvailable(): Boolean {
        return Shell.SU.available()
    }

    /**
     * Executes a single command with root privileges.
     * @param command The command to execute.
     * @param callback A callback to handle the command's output or any errors.
     */
    fun executeRootCommand(command: String, callback: CommandCallback) {
        scope.launch {
            try {
                val output = Shell.SU.run(command)
                if (output != null) {
                    callback.onSuccess(output)
                } else {
                    callback.onFailure("Command execution failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error executing root command", e)
                callback.onFailure(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Executes multiple commands in a single root shell session.
     * @param commands A list of commands to execute.
     * @param callback A callback to handle the output or any errors.
     */
    fun executeRootCommands(commands: List<String>, callback: CommandCallback) {
        scope.launch {
            try {
                val output = Shell.SU.run(commands)
                if (output != null) {
                    callback.onSuccess(output)
                } else {
                    callback.onFailure("Commands execution failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error executing root commands", e)
                callback.onFailure(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Installs an APK silently in the background without user interaction.
     * @param apkPath The file path to the APK to be installed.
     * @param callback A callback to handle the installation result.
     */
    fun silentInstall(apkPath: String, callback: CommandCallback) {
        val command = "pm install -r $apkPath"
        executeRootCommand(command, callback)
    }

    /**
     * Uninstalls an application silently in the background without user interaction.
     * @param packageName The package name of the application to uninstall.
     * @param callback A callback to handle the uninstallation result.
     */
    fun silentUninstall(packageName: String, callback: CommandCallback) {
        val command = "pm uninstall $packageName"
        executeRootCommand(command, callback)
    }

    /**
     * Clears the data of a specific application, effectively resetting it.
     * @param packageName The package name of the application to clear.
     * @param callback A callback to handle the result.
     */
    fun clearAppData(packageName: String, callback: CommandCallback) {
        val command = "pm clear $packageName"
        executeRootCommand(command, callback)
    }

    /**
     * Reboots the device.
     * @param callback A callback to handle the result.
     */
    fun rebootDevice(callback: CommandCallback) {
        executeRootCommand("reboot", callback)
    }

    /**
     * Takes a screenshot and saves it to the specified path.
     * @param path The path to save the screenshot.
     * @param callback A callback to handle the result.
     */
    fun takeScreenshot(path: String, callback: CommandCallback) {
        val command = "screencap -p $path"
        executeRootCommand(command, callback)
    }

    /**
     * A callback interface for handling the results of root command execution.
     */
    fun interface CommandCallback {
        fun onSuccess(output: List<String>)
        fun onFailure(error: String)
    }
}

