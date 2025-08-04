package com.anti.rootadbcontroller.services

import com.anti.rootadbcontroller.utils.ShizukuUtils

/**
 * Callback interfaces for Shizuku operations
 */
object ShizukuCallbacks {

    /**
     * Callback for shell command execution
     */
    fun interface CommandCallback {
        fun onResult(result: ShizukuUtils.CommandResult)
    }

    /**
     * Callback for APK installation
     */
    fun interface InstallCallback {
        fun onInstallResult(success: Boolean, apkPath: String)
    }

    /**
     * Callback for package uninstallation
     */
    fun interface UninstallCallback {
        fun onUninstallResult(success: Boolean, packageName: String)
    }

    /**
     * Callback for permission operations
     */
    fun interface PermissionCallback {
        fun onPermissionResult(success: Boolean, packageName: String, permission: String, granted: Boolean)
    }

    /**
     * Callback for component state changes
     */
    fun interface ComponentCallback {
        fun onComponentResult(success: Boolean, packageName: String, componentName: String, enabled: Boolean)
    }

    /**
     * Callback for system operations
     */
    fun interface SystemOperationCallback {
        fun onSystemOperationResult(success: Boolean, operation: String, result: String)
    }

    /**
     * Callback for file operations
     */
    fun interface FileOperationCallback {
        fun onFileOperationResult(success: Boolean, filePath: String, operation: String)
    }

    /**
     * Callback for network operations
     */
    fun interface NetworkCallback {
        fun onNetworkResult(success: Boolean, data: String)
    }

    /**
     * System operation types and data
     */
    data class SystemOperation(
        val type: Type,
        val packageName: String? = null,
        val componentName: String? = null,
        val property: String? = null,
        val value: String? = null,
        val customCommand: String? = null
    ) {
        enum class Type {
            DISABLE_PACKAGE,
            ENABLE_PACKAGE,
            CLEAR_APP_DATA,
            FORCE_STOP_APP,
            GET_APP_INFO,
            SET_SYSTEM_PROPERTY,
            GET_SYSTEM_PROPERTY,
            CUSTOM_COMMAND
        }

        companion object {
            fun customCommand(command: String) = SystemOperation(
                type = Type.CUSTOM_COMMAND,
                customCommand = command
            )
        }
    }
}
