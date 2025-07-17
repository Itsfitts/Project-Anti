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
    public void performSystemOperation(SystemOperation operation, SystemOperationCallback callback) {
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

                    case CUSTOM_COMMAND:
                        cmdResult = shizukuUtils.executeShellCommandWithResult(operation.customCommand);
                        success = cmdResult.isSuccess();
                        result = cmdResult.output;
                        break;

                    case SET_SYSTEM_PROPERTY:
                        success = shizukuUtils.setSystemProperty(operation.property, operation.value);
                        result = success ? "Property set: " + operation.property + "=" + operation.value : "Failed to set property";
                        break;

                    case GET_SYSTEM_PROPERTY:
                        String propValue = shizukuUtils.getSystemProperty(operation.property);
                        success = propValue != null;
                        result = success ? propValue : "Failed to get property";
                        break;
                }

                if (callback != null) {
                    callback.onSystemOperationResult(success, operation.type.name(), result);
                }
            });
        }
    }

    /**
     * Get list of installed packages
     */
    public void getInstalledPackagesAsync(CommandCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                ShizukuUtils.CommandResult result = shizukuUtils.executeShellCommandWithResult("pm list packages");
                if (callback != null) {
                    callback.onResult(result);
                }
            });
        }
    }

    /**
     * Get device information
     */
    public void getDeviceInfoAsync(CommandCallback callback) {
        if (executor != null) {
            executor.execute(() -> {
                StringBuilder deviceInfo = new StringBuilder();

                String model = shizukuUtils.getSystemProperty("ro.product.model");
                String manufacturer = shizukuUtils.getSystemProperty("ro.product.manufacturer");
                String version = shizukuUtils.getSystemProperty("ro.build.version.release");
                String buildId = shizukuUtils.getSystemProperty("ro.build.id");

                deviceInfo.append("Device: ").append(manufacturer).append(" ").append(model).append("\n");
                deviceInfo.append("Android Version: ").append(version).append("\n");
                deviceInfo.append("Build ID: ").append(buildId).append("\n");

                ShizukuUtils.CommandResult result = new ShizukuUtils.CommandResult(0, deviceInfo.toString(), "");
                if (callback != null) {
                    callback.onResult(result);
                }
            });
        }
    }
}
