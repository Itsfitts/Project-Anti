package com.anti.rootadbcontroller.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.anti.rootadbcontroller.utils.ShizukuUtils
import com.anti.rootadbcontroller.services.ShizukuCallbacks.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Service for managing Shizuku operations and providing system-level functionality
 */
class ShizukuManagerService : Service() {
    private val binder = ShizukuManagerBinder()
    private var shizukuUtils: ShizukuUtils? = null
    private var executor: ExecutorService? = null

    inner class ShizukuManagerBinder : Binder() {
        fun getService(): ShizukuManagerService = this@ShizukuManagerService
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "ShizukuManagerService created")

        shizukuUtils = ShizukuUtils.getInstance()
        shizukuUtils?.initialize()
        executor = Executors.newCachedThreadPool()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ShizukuManagerService destroyed")
        shizukuUtils?.cleanup()
        executor?.shutdown()
    }

    override fun onBind(intent: Intent): IBinder = binder

    /**
     * Check if Shizuku is available and ready
     */
    fun isShizukuReady(): Boolean =
        shizukuUtils?.isShizukuAvailable == true && shizukuUtils?.hasShizukuPermission() == true

    /**
     * Request Shizuku permission
     */
    fun requestShizukuPermission() {
        shizukuUtils.requestShizukuPermission()
    }

    /**
     * Execute shell command asynchronously
     */
    fun executeCommandAsync(command: String, callback: CommandCallback?) {
        executor.execute {
            val result = shizukuUtils.executeShellCommandWithResult(command)
            callback?.onResult(result)
        }
    }

    /**
     * Install APK file
     */
    fun installApkAsync(apkPath: String, callback: InstallCallback?) {
        executor.execute {
            val success = shizukuUtils.installApk(apkPath)
            callback?.onInstallResult(success, apkPath)
        }
    }

    /**
     * Uninstall package
     */
    fun uninstallPackageAsync(packageName: String, callback: UninstallCallback?) {
        executor.execute {
            val success = shizukuUtils.uninstallPackage(packageName)
            callback?.onUninstallResult(success, packageName)
        }
    }

    /**
     * Grant permission to package
     */
    fun grantPermissionAsync(packageName: String, permission: String, callback: PermissionCallback?) {
        executor.execute {
            val success = shizukuUtils.grantPermission(packageName, permission)
            callback?.onPermissionResult(success, packageName, permission, true)
        }
    }

    /**
     * Revoke permission from package
     */
    fun revokePermissionAsync(packageName: String, permission: String, callback: PermissionCallback?) {
        executor.execute {
            val success = shizukuUtils.revokePermission(packageName, permission)
            callback?.onPermissionResult(success, packageName, permission, false)
        }
    }

    /**
     * Enable/disable app component
     */
    fun setComponentEnabledAsync(
        packageName: String,
        componentName: String,
        enabled: Boolean,
        callback: ComponentCallback?
    ) {
        executor.execute {
            val success = shizukuUtils.setComponentEnabled(packageName, componentName, enabled)
            callback?.onComponentResult(success, packageName, componentName, enabled)
        }
    }

    /**
     * Advanced system operations
     */
    fun performSystemOperation(operation: SystemOperation, callback: SystemOperationCallback?) {
        executor.execute {
            var success = false
            var result = ""

            when (operation.type) {
                SystemOperation.Type.DISABLE_PACKAGE -> {
                    success = shizukuUtils.setComponentEnabled(operation.packageName!!, operation.packageName, false)
                    result = "Package " + if (success) "disabled" else "disable failed"
                }
                SystemOperation.Type.ENABLE_PACKAGE -> {
                    success = shizukuUtils.setComponentEnabled(operation.packageName!!, operation.packageName, true)
                    result = "Package " + if (success) "enabled" else "enable failed"
                }
                SystemOperation.Type.CLEAR_APP_DATA -> {
                    val cmdResult = shizukuUtils.executeShellCommandWithResult("pm clear ${operation.packageName}")
                    success = cmdResult.isSuccess
                    result = if (success) "App data cleared" else "Failed to clear app data"
                }
                SystemOperation.Type.FORCE_STOP_APP -> {
                    val cmdResult = shizukuUtils.executeShellCommandWithResult("am force-stop ${operation.packageName}")
                    success = cmdResult.isSuccess
                    result = if (success) "App force stopped" else "Failed to force stop app"
                }
                SystemOperation.Type.GET_APP_INFO -> {
                    val cmdResult = shizukuUtils.executeShellCommandWithResult("dumpsys package ${operation.packageName}")
                    success = cmdResult.isSuccess
                    result = if (success) cmdResult.output else "Failed to get app info"
                }
                SystemOperation.Type.SET_SYSTEM_PROPERTY -> {
                    val cmdResult = shizukuUtils.executeShellCommandWithResult("setprop ${operation.property} ${operation.value}")
                    success = cmdResult.isSuccess
                    result = if (success) "Property set successfully" else "Failed to set property"
                }
                SystemOperation.Type.GET_SYSTEM_PROPERTY -> {
                    val cmdResult = shizukuUtils.executeShellCommandWithResult("getprop ${operation.property}")
                    success = cmdResult.isSuccess
                    result = if (success) cmdResult.output else "Failed to get property"
                }
                SystemOperation.Type.CUSTOM_COMMAND -> {
                    val cmdResult = shizukuUtils.executeShellCommandWithResult(operation.customCommand!!)
                    success = cmdResult.isSuccess
                    result = if (success) cmdResult.output else "Command execution failed"
                }
            }
            callback?.onSystemOperationResult(success, operation.type.name, result)
        }
    }

    /**
     * Get system information
     */
    fun getSystemInfo(callback: SystemOperationCallback?) {
        executor.execute {
            val info = StringBuilder()
            var result = shizukuUtils.executeShellCommandWithResult("getprop ro.build.version.release")
            if (result.isSuccess) {
                info.append("Android Version: ").append(result.output.trim()).append("\n")
            }
            result = shizukuUtils.executeShellCommandWithResult("getprop ro.product.model")
            if (result.isSuccess) {
                info.append("Device Model: ").append(result.output.trim()).append("\n")
            }
            result = shizukuUtils.executeShellCommandWithResult("getprop ro.build.version.sdk")
            if (result.isSuccess) {
                info.append("SDK Level: ").append(result.output.trim()).append("\n")
            }
            callback?.onSystemOperationResult(true, "GET_SYSTEM_INFO", info.toString())
        }
    }

    /**
     * List installed packages
     */
    fun listPackages(includeSystem: Boolean, callback: SystemOperationCallback?) {
        executor.execute {
            val command = if (includeSystem) "pm list packages" else "pm list packages -3"
            val result = shizukuUtils.executeShellCommandWithResult(command)
            callback?.onSystemOperationResult(
                result.isSuccess, "LIST_PACKAGES",
                if (result.isSuccess) result.output else "Failed to list packages"
            )
        }
    }

    /**
     * File operations via Shizuku
     */
    fun performFileOperation(filePath: String, operation: String, callback: FileOperationCallback?) {
        executor.execute {
            val command = when (operation.lowercase()) {
                "read" -> "cat $filePath"
                "delete" -> "rm $filePath"
                "list" -> "ls -la $filePath"
                "copy" -> "cp $filePath" // Assumes filePath contains both source and destination
                else -> "$operation $filePath"
            }
            val result = shizukuUtils.executeShellCommandWithResult(command)
            callback?.onFileOperationResult(result.isSuccess, filePath, operation)
        }
    }

    /**
     * Network operations
     */
    fun performNetworkOperation(operation: String, callback: NetworkCallback?) {
        executor.execute {
            var success: Boolean
            var data: String
            when (operation.lowercase()) {
                "netstat" -> {
                    val result = shizukuUtils.executeShellCommandWithResult("netstat -tuln")
                    success = result.isSuccess
                    data = if (success) result.output else "Failed to get network connections"
                }
                "ifconfig" -> {
                    val result = shizukuUtils.executeShellCommandWithResult("ip addr")
                    success = result.isSuccess
                    data = if (success) result.output else "Failed to get network interfaces"
                }
                "ping" -> {
                    val result = shizukuUtils.executeShellCommandWithResult("ping -c 4 8.8.8.8")
                    success = result.isSuccess
                    data = if (success) result.output else "Ping failed"
                }
                else -> {
                    success = false
                    data = "Unknown network operation: $operation"
                }
            }
            callback?.onNetworkResult(success, data)
        }
    }

    companion object {
        private const val TAG = "ShizukuManagerService"
    }
}

