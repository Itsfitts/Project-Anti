package com.anti.rootadbcontroller.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

import com.anti.rootadbcontroller.utils.ShizukuUtils;
import com.anti.rootadbcontroller.services.ShizukuCallbacks.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service for managing Shizuku operations and providing system-level functionality
 */
public class ShizukuManagerService extends Service {
    private static final String TAG = "ShizukuManagerService";

    private final IBinder binder = new ShizukuManagerBinder();
    private ShizukuUtils shizukuUtils;
    private ExecutorService executor;

    public class ShizukuManagerBinder extends Binder {
        public ShizukuManagerService getService() {
            return ShizukuManagerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ShizukuManagerService created");

        shizukuUtils = ShizukuUtils.getInstance();
        shizukuUtils.initialize();
        executor = Executors.newCachedThreadPool();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ShizukuManagerService destroyed");

        if (shizukuUtils != null) {
            shizukuUtils.cleanup();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Check if Shizuku is available and ready
     */
    public boolean isShizukuReady() {
        return shizukuUtils != null &&
               shizukuUtils.isShizukuAvailable() &&
               shizukuUtils.hasShizukuPermission();
    }

    /**
     * Request Shizuku permission
     */
    public void requestShizukuPermission() {
        if (shizukuUtils != null) {
            shizukuUtils.requestShizukuPermission();
        }
    }

    /**
     * Execute shell command asynchronously
     */
    public void executeCommandAsync(String command, CommandCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                ShizukuUtils.CommandResult result = shizukuUtils.executeShellCommandWithResult(command);
                if (callback != null) {
                    callback.onResult(result);
                }
            });
        }
    }

    /**
     * Install APK file
     */
    public void installApkAsync(String apkPath, InstallCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                boolean success = shizukuUtils.installApk(apkPath);
                if (callback != null) {
                    callback.onInstallResult(success, apkPath);
                }
            });
        }
    }

    /**
     * Uninstall package
     */
    public void uninstallPackageAsync(String packageName, UninstallCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                boolean success = shizukuUtils.uninstallPackage(packageName);
                if (callback != null) {
                    callback.onUninstallResult(success, packageName);
                }
            });
        }
    }

    /**
     * Grant permission to package
     */
    public void grantPermissionAsync(String packageName, String permission, PermissionCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                boolean success = shizukuUtils.grantPermission(packageName, permission);
                if (callback != null) {
                    callback.onPermissionResult(success, packageName, permission, true);
                }
            });
        }
    }

    /**
     * Revoke permission from package
     */
    public void revokePermissionAsync(String packageName, String permission, PermissionCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                boolean success = shizukuUtils.revokePermission(packageName, permission);
                if (callback != null) {
                    callback.onPermissionResult(success, packageName, permission, false);
                }
            });
        }
    }

    /**
     * Enable/disable app component
     */
    public void setComponentEnabledAsync(String packageName, String componentName, boolean enabled, ComponentCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                boolean success = shizukuUtils.setComponentEnabled(packageName, componentName, enabled);
                if (callback != null) {
                    callback.onComponentResult(success, packageName, componentName, enabled);
                }
            });
        }
    }

    /**
     * Advanced system operations
     */
    public void performSystemOperation(ShizukuCallbacks.SystemOperation operation, SystemOperationCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                boolean success = false;
                String result = "";

                switch (operation.type) {
                    case DISABLE_PACKAGE:
                        success = shizukuUtils.setComponentEnabled(operation.packageName, operation.packageName, false);
                        result = "Package " + (success ? "disabled" : "disable failed");
                        break;

                    case ENABLE_PACKAGE:
                        success = shizukuUtils.setComponentEnabled(operation.packageName, operation.packageName, true);
                        result = "Package " + (success ? "enabled" : "enable failed");
                        break;

                    case CLEAR_APP_DATA:
                        ShizukuUtils.CommandResult cmdResult = shizukuUtils.executeShellCommandWithResult(
                            "pm clear " + operation.packageName);
                        success = cmdResult.isSuccess();
                        result = success ? "App data cleared" : "Failed to clear app data";
                        break;

                    case FORCE_STOP_APP:
                        cmdResult = shizukuUtils.executeShellCommandWithResult(
                            "am force-stop " + operation.packageName);
                        success = cmdResult.isSuccess();
                        result = success ? "App force stopped" : "Failed to force stop app";
                        break;

                    case GET_APP_INFO:
                        cmdResult = shizukuUtils.executeShellCommandWithResult(
                            "dumpsys package " + operation.packageName);
                        success = cmdResult.isSuccess();
                        result = success ? cmdResult.output : "Failed to get app info";
                        break;

                    case SET_SYSTEM_PROPERTY:
                        cmdResult = shizukuUtils.executeShellCommandWithResult(
                            "setprop " + operation.property + " " + operation.value);
                        success = cmdResult.isSuccess();
                        result = success ? "Property set successfully" : "Failed to set property";
                        break;

                    case GET_SYSTEM_PROPERTY:
                        cmdResult = shizukuUtils.executeShellCommandWithResult(
                            "getprop " + operation.property);
                        success = cmdResult.isSuccess();
                        result = success ? cmdResult.output : "Failed to get property";
                        break;

                    case CUSTOM_COMMAND:
                        cmdResult = shizukuUtils.executeShellCommandWithResult(operation.customCommand);
                        success = cmdResult.isSuccess();
                        result = success ? cmdResult.output : "Command execution failed";
                        break;
                }

                if (callback != null) {
                    callback.onSystemOperationResult(success, operation.type.name(), result);
                }
            });
        }
    }

    /**
     * Get system information
     */
    public void getSystemInfo(SystemOperationCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                StringBuilder info = new StringBuilder();

                // Get Android version
                ShizukuUtils.CommandResult result = shizukuUtils.executeShellCommandWithResult("getprop ro.build.version.release");
                if (result.isSuccess()) {
                    info.append("Android Version: ").append(result.output.trim()).append("\n");
                }

                // Get device model
                result = shizukuUtils.executeShellCommandWithResult("getprop ro.product.model");
                if (result.isSuccess()) {
                    info.append("Device Model: ").append(result.output.trim()).append("\n");
                }

                // Get SDK level
                result = shizukuUtils.executeShellCommandWithResult("getprop ro.build.version.sdk");
                if (result.isSuccess()) {
                    info.append("SDK Level: ").append(result.output.trim()).append("\n");
                }

                if (callback != null) {
                    callback.onSystemOperationResult(true, "GET_SYSTEM_INFO", info.toString());
                }
            });
        }
    }

    /**
     * List installed packages
     */
    public void listPackages(boolean includeSystem, SystemOperationCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                String command = includeSystem ? "pm list packages" : "pm list packages -3";
                ShizukuUtils.CommandResult result = shizukuUtils.executeShellCommandWithResult(command);

                if (callback != null) {
                    callback.onSystemOperationResult(result.isSuccess(), "LIST_PACKAGES",
                        result.isSuccess() ? result.output : "Failed to list packages");
                }
            });
        }
    }

    /**
     * File operations via Shizuku
     */
    public void performFileOperation(String filePath, String operation, FileOperationCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                boolean success = false;
                String command = "";

                switch (operation.toLowerCase()) {
                    case "read":
                        command = "cat " + filePath;
                        break;
                    case "delete":
                        command = "rm " + filePath;
                        break;
                    case "list":
                        command = "ls -la " + filePath;
                        break;
                    case "copy":
                        // For copy operations, filePath should contain source and destination
                        command = "cp " + filePath;
                        break;
                    default:
                        command = operation + " " + filePath;
                        break;
                }

                ShizukuUtils.CommandResult result = shizukuUtils.executeShellCommandWithResult(command);
                success = result.isSuccess();

                if (callback != null) {
                    callback.onFileOperationResult(success, filePath, operation);
                }
            });
        }
    }

    /**
     * Network operations
     */
    public void performNetworkOperation(String operation, NetworkCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                boolean success = false;
                String data = "";

                switch (operation.toLowerCase()) {
                    case "netstat":
                        ShizukuUtils.CommandResult result = shizukuUtils.executeShellCommandWithResult("netstat -tuln");
                        success = result.isSuccess();
                        data = success ? result.output : "Failed to get network connections";
                        break;

                    case "ifconfig":
                        result = shizukuUtils.executeShellCommandWithResult("ip addr");
                        success = result.isSuccess();
                        data = success ? result.output : "Failed to get network interfaces";
                        break;

                    case "ping":
                        result = shizukuUtils.executeShellCommandWithResult("ping -c 4 8.8.8.8");
                        success = result.isSuccess();
                        data = success ? result.output : "Ping failed";
                        break;

                    default:
                        data = "Unknown network operation: " + operation;
                        break;
                }

                if (callback != null) {
                    callback.onNetworkResult(success, data);
                }
            });
        }
    }
}
