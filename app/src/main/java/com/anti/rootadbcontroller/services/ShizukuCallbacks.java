package com.anti.rootadbcontroller.services;

import com.anti.rootadbcontroller.utils.ShizukuUtils;

/**
 * Callback interfaces for Shizuku operations
 */
public class ShizukuCallbacks {

    /**
     * Callback for shell command execution
     */
    public interface CommandCallback {
        void onResult(ShizukuUtils.CommandResult result);
    }

    /**
     * Callback for APK installation
     */
    public interface InstallCallback {
        void onInstallResult(boolean success, String apkPath);
    }

    /**
     * Callback for package uninstallation
     */
    public interface UninstallCallback {
        void onUninstallResult(boolean success, String packageName);
    }

    /**
     * Callback for permission operations
     */
    public interface PermissionCallback {
        void onPermissionResult(boolean success, String packageName, String permission, boolean granted);
    }

    /**
     * Callback for component state changes
     */
    public interface ComponentCallback {
        void onComponentResult(boolean success, String packageName, String componentName, boolean enabled);
    }

    /**
     * Callback for system operations
     */
    public interface SystemOperationCallback {
        void onSystemOperationResult(boolean success, String operation, String result);
    }

    /**
     * Callback for file operations
     */
    public interface FileOperationCallback {
        void onFileOperationResult(boolean success, String filePath, String operation);
    }

    /**
     * Callback for network operations
     */
    public interface NetworkCallback {
        void onNetworkResult(boolean success, String data);
    }

    /**
     * System operation types and data
     */
    public static class SystemOperation {
        public enum Type {
            DISABLE_PACKAGE,
            ENABLE_PACKAGE,
            CLEAR_APP_DATA,
            FORCE_STOP_APP,
            GET_APP_INFO,
            SET_SYSTEM_PROPERTY,
            GET_SYSTEM_PROPERTY,
            CUSTOM_COMMAND
        }

        public final Type type;
        public final String packageName;
        public final String componentName;
        public final String property;
        public final String value;
        public final String customCommand;

        public SystemOperation(Type type, String packageName) {
            this(type, packageName, null, null, null, null);
        }

        public SystemOperation(Type type, String packageName, String componentName) {
            this(type, packageName, componentName, null, null, null);
        }

        public SystemOperation(Type type, String property, String value) {
            this.type = type;
            this.packageName = null;
            this.componentName = null;
            this.property = property;
            this.value = value;
            this.customCommand = null;
        }

        // Constructor for custom commands
        public static SystemOperation customCommand(String command) {
            return new SystemOperation(Type.CUSTOM_COMMAND, null, null, null, null, command);
        }

        public SystemOperation(Type type, String packageName, String componentName, String property, String value, String customCommand) {
            this.type = type;
            this.packageName = packageName;
            this.componentName = componentName;
            this.property = property;
            this.value = value;
            this.customCommand = customCommand;
        }
    }
}
