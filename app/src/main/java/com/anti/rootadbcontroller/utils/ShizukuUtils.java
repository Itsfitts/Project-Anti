package com.anti.rootadbcontroller.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuBinderWrapper;
import rikka.shizuku.SystemServiceHelper;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for Shizuku integration
 * Provides methods to interact with system APIs through Shizuku
 */
public class ShizukuUtils {
    private static final String TAG = "ShizukuUtils";
    private static final int SHIZUKU_PERMISSION_REQUEST_CODE = 1001;

    private static boolean isInitialized = false;
    private static ShizukuUtils instance;

    // Shizuku listeners
    private final Shizuku.OnRequestPermissionResultListener permissionResultListener =
        new Shizuku.OnRequestPermissionResultListener() {
            @Override
            public void onRequestPermissionResult(int requestCode, int grantResult) {
                Log.d(TAG, "Permission result: " + requestCode + " = " + grantResult);
                if (requestCode == SHIZUKU_PERMISSION_REQUEST_CODE) {
                    boolean granted = grantResult == PackageManager.PERMISSION_GRANTED;
                    Log.i(TAG, "Shizuku permission " + (granted ? "granted" : "denied"));
                }
            }
        };

    private final Shizuku.OnBinderReceivedListener binderReceivedListener =
        new Shizuku.OnBinderReceivedListener() {
            @Override
            public void onBinderReceived() {
                Log.i(TAG, "Shizuku binder received");
            }
        };

    private final Shizuku.OnBinderDeadListener binderDeadListener =
        new Shizuku.OnBinderDeadListener() {
            @Override
            public void onBinderDead() {
                Log.w(TAG, "Shizuku binder died");
                isInitialized = false;
            }
        };

    private ShizukuUtils() {}

    public static synchronized ShizukuUtils getInstance() {
        if (instance == null) {
            instance = new ShizukuUtils();
        }
        return instance;
    }

    /**
     * Initialize Shizuku integration
     */
    public void initialize() {
        if (isInitialized) {
            Log.d(TAG, "Shizuku already initialized");
            return;
        }

        try {
            // Add listeners
            Shizuku.addRequestPermissionResultListener(permissionResultListener);
            Shizuku.addBinderReceivedListener(binderReceivedListener);
            Shizuku.addBinderDeadListener(binderDeadListener);

            isInitialized = true;
            Log.i(TAG, "Shizuku initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Shizuku", e);
        }
    }

    /**
     * Clean up Shizuku listeners
     */
    public void cleanup() {
        try {
            Shizuku.removeRequestPermissionResultListener(permissionResultListener);
            Shizuku.removeBinderReceivedListener(binderReceivedListener);
            Shizuku.removeBinderDeadListener(binderDeadListener);
            isInitialized = false;
            Log.i(TAG, "Shizuku cleanup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error during Shizuku cleanup", e);
        }
    }

    /**
     * Check if Shizuku is available and running
     */
    public boolean isShizukuAvailable() {
        try {
            return Shizuku.pingBinder();
        } catch (Exception e) {
            Log.e(TAG, "Error checking Shizuku availability", e);
            return false;
        }
    }

    /**
     * Check if we have Shizuku permission
     */
    public boolean hasShizukuPermission() {
        if (!isShizukuAvailable()) {
            return false;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error checking Shizuku permission", e);
            return false;
        }
    }

    /**
     * Request Shizuku permission
     */
    public void requestShizukuPermission() {
        if (hasShizukuPermission()) {
            Log.d(TAG, "Shizuku permission already granted");
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE);
                Log.i(TAG, "Shizuku permission requested");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error requesting Shizuku permission", e);
        }
    }

    /**
     * Execute shell command through Shizuku
     */
    public String executeShellCommand(String command) {
        if (!hasShizukuPermission()) {
            Log.w(TAG, "No Shizuku permission for command: " + command);
            return null;
        }

        try {
            Log.d(TAG, "Executing command: " + command);
            return Shizuku.newProcess(new String[]{"sh", "-c", command}, null, null)
                    .waitFor().toString();
        } catch (Exception e) {
            Log.e(TAG, "Error executing shell command: " + command, e);
            return null;
        }
    }

    /**
     * Execute shell command with result capture
     */
    public CommandResult executeShellCommandWithResult(String command) {
        if (!hasShizukuPermission()) {
            Log.w(TAG, "No Shizuku permission for command: " + command);
            return new CommandResult(-1, "No Shizuku permission", "");
        }

        try {
            Log.d(TAG, "Executing command with result: " + command);
            Process process = Shizuku.newProcess(new String[]{"sh", "-c", command}, null, null);

            // Read output and error streams
            StringBuilder output = new StringBuilder();
            StringBuilder error = new StringBuilder();

            // Create readers for the process streams
            java.io.BufferedReader stdoutReader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
            java.io.BufferedReader stderrReader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getErrorStream()));

            // Read the output stream
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Read the error stream
            while ((line = stderrReader.readLine()) != null) {
                error.append(line).append("\n");
            }

            // Wait for process completion
            int exitCode = process.waitFor();

            return new CommandResult(exitCode, output.toString(), error.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error executing shell command with result: " + command, e);
            return new CommandResult(-1, "", e.getMessage());
        }
    }

    /**
     * Install APK through Shizuku
     */
    public boolean installApk(String apkPath) {
        if (!hasShizukuPermission()) {
            Log.w(TAG, "No Shizuku permission for APK installation");
            return false;
        }

        try {
            String command = "pm install -r \"" + apkPath + "\"";
            CommandResult result = executeShellCommandWithResult(command);
            boolean success = result.exitCode == 0 && result.output.contains("Success");
            Log.i(TAG, "APK installation " + (success ? "successful" : "failed") + ": " + apkPath);
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error installing APK: " + apkPath, e);
            return false;
        }
    }

    /**
     * Uninstall package through Shizuku
     */
    public boolean uninstallPackage(String packageName) {
        if (!hasShizukuPermission()) {
            Log.w(TAG, "No Shizuku permission for package uninstallation");
            return false;
        }

        try {
            String command = "pm uninstall \"" + packageName + "\"";
            CommandResult result = executeShellCommandWithResult(command);
            boolean success = result.exitCode == 0 && result.output.contains("Success");
            Log.i(TAG, "Package uninstallation " + (success ? "successful" : "failed") + ": " + packageName);
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error uninstalling package: " + packageName, e);
            return false;
        }
    }

    /**
     * Grant permission to a package through Shizuku
     */
    public boolean grantPermission(String packageName, String permission) {
        if (!hasShizukuPermission()) {
            Log.w(TAG, "No Shizuku permission for granting permissions");
            return false;
        }

        try {
            String command = "pm grant \"" + packageName + "\" \"" + permission + "\"";
            CommandResult result = executeShellCommandWithResult(command);
            boolean success = result.exitCode == 0;
            Log.i(TAG, "Permission grant " + (success ? "successful" : "failed") +
                  ": " + permission + " to " + packageName);
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error granting permission: " + permission + " to " + packageName, e);
            return false;
        }
    }

    /**
     * Revoke permission from a package through Shizuku
     */
    public boolean revokePermission(String packageName, String permission) {
        if (!hasShizukuPermission()) {
            Log.w(TAG, "No Shizuku permission for revoking permissions");
            return false;
        }

        try {
            String command = "pm revoke \"" + packageName + "\" \"" + permission + "\"";
            CommandResult result = executeShellCommandWithResult(command);
            boolean success = result.exitCode == 0;
            Log.i(TAG, "Permission revoke " + (success ? "successful" : "failed") +
                  ": " + permission + " from " + packageName);
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error revoking permission: " + permission + " from " + packageName, e);
            return false;
        }
    }

    /**
     * Enable/disable a component through Shizuku
     */
    public boolean setComponentEnabled(String packageName, String componentName, boolean enabled) {
        if (!hasShizukuPermission()) {
            Log.w(TAG, "No Shizuku permission for component management");
            return false;
        }

        try {
            String state = enabled ? "enable" : "disable";
            String command = "pm " + state + " \"" + packageName + "/" + componentName + "\"";
            CommandResult result = executeShellCommandWithResult(command);
            boolean success = result.exitCode == 0;
            Log.i(TAG, "Component " + state + " " + (success ? "successful" : "failed") +
                  ": " + componentName);
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error setting component state: " + componentName, e);
            return false;
        }
    }

    /**
     * Get system property through Shizuku
     */
    public String getSystemProperty(String property) {
        if (!hasShizukuPermission()) {
            Log.w(TAG, "No Shizuku permission for system properties");
            return null;
        }

        try {
            String command = "getprop \"" + property + "\"";
            CommandResult result = executeShellCommandWithResult(command);
            if (result.exitCode == 0) {
                return result.output.trim();
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting system property: " + property, e);
            return null;
        }
    }

    /**
     * Set system property through Shizuku
     */
    public boolean setSystemProperty(String property, String value) {
        if (!hasShizukuPermission()) {
            Log.w(TAG, "No Shizuku permission for setting system properties");
            return false;
        }

        try {
            String command = "setprop \"" + property + "\" \"" + value + "\"";
            CommandResult result = executeShellCommandWithResult(command);
            boolean success = result.exitCode == 0;
            Log.i(TAG, "System property set " + (success ? "successful" : "failed") +
                  ": " + property + " = " + value);
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error setting system property: " + property, e);
            return false;
        }
    }

    /**
     * Result class for shell command execution
     */
    public static class CommandResult {
        public final int exitCode;
        public final String output;
        public final String error;

        public CommandResult(int exitCode, String output, String error) {
            this.exitCode = exitCode;
            this.output = output != null ? output : "";
            this.error = error != null ? error : "";
        }

        public boolean isSuccess() {
            return exitCode == 0;
        }

        @Override
        public String toString() {
            return "CommandResult{" +
                    "exitCode=" + exitCode +
                    ", output='" + output + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }
}
