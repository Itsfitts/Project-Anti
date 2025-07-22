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
     * @param context Application context
     * @return true if initialization successful
     */
    public boolean initialize(@NonNull Context context) {
        if (isInitialized) {
            return true;
        }

        try {
            // Add listeners
            Shizuku.addRequestPermissionResultListener(permissionResultListener);
            Shizuku.addBinderReceivedListener(binderReceivedListener);
            Shizuku.addBinderDeadListener(binderDeadListener);

            isInitialized = true;
            Log.i(TAG, "Shizuku initialized successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Shizuku", e);
            return false;
        }
    }

    /**
     * Check if Shizuku is available and running
     * @return true if Shizuku is available
     */
    public static boolean isShizukuAvailable() {
        try {
            return Shizuku.pingBinder();
        } catch (Exception e) {
            Log.d(TAG, "Shizuku not available: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if we have Shizuku permission
     * @return true if permission is granted
     */
    public static boolean hasShizukuPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Request Shizuku permission
     */
    public void requestShizukuPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            if (Shizuku.shouldShowRequestPermissionRationale()) {
                Log.i(TAG, "Should show permission rationale");
            }
            Shizuku.requestPermission(SHIZUKU_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Execute privileged command through Shizuku
     * @param command Command to execute
     * @return Command result
     */
    @Nullable
    public CommandResult executePrivilegedCommand(@NonNull String command) {
        if (!isShizukuAvailable() || !hasShizukuPermission()) {
            Log.w(TAG, "Shizuku not available or permission denied");
            return null;
        }

        try {
            // Execute command using Shizuku's shell interface
            Process process = Shizuku.newProcess(new String[]{"sh", "-c", command}, null, null);
            return AdbUtils.readProcessOutput(process);
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute command via Shizuku: " + command, e);
            return null;
        }
    }

    /**
     * Install APK using Shizuku
     * @param apkPath Path to APK file
     * @return Installation result
     */
    @Nullable
    public CommandResult installApkWithShizuku(@NonNull String apkPath) {
        String command = "pm install -r \"" + apkPath + "\"";
        return executePrivilegedCommand(command);
    }

    /**
     * Uninstall package using Shizuku
     * @param packageName Package to uninstall
     * @return Uninstallation result
     */
    @Nullable
    public CommandResult uninstallPackageWithShizuku(@NonNull String packageName) {
        String command = "pm uninstall \"" + packageName + "\"";
        return executePrivilegedCommand(command);
    }

    /**
     * Grant permission to package using Shizuku
     * @param packageName Target package
     * @param permission Permission to grant
     * @return Operation result
     */
    @Nullable
    public CommandResult grantPermission(@NonNull String packageName, @NonNull String permission) {
        String command = "pm grant \"" + packageName + "\" \"" + permission + "\"";
        return executePrivilegedCommand(command);
    }

    /**
     * Revoke permission from package using Shizuku
     * @param packageName Target package
     * @param permission Permission to revoke
     * @return Operation result
     */
    @Nullable
    public CommandResult revokePermission(@NonNull String packageName, @NonNull String permission) {
        String command = "pm revoke \"" + packageName + "\" \"" + permission + "\"";
        return executePrivilegedCommand(command);
    }

    /**
     * Enable/disable package component using Shizuku
     * @param packageName Target package
     * @param componentName Component name
     * @param enable true to enable, false to disable
     * @return Operation result
     */
    @Nullable
    public CommandResult setComponentEnabled(@NonNull String packageName, @NonNull String componentName, boolean enable) {
        String action = enable ? "enable" : "disable";
        String command = "pm " + action + " \"" + packageName + "/" + componentName + "\"";
        return executePrivilegedCommand(command);
    }

    /**
     * Execute ADB shell command through Shizuku
     * @param shellCommand Shell command to execute
     * @return Command result
     */
    @Nullable
    public CommandResult executeShellCommand(@NonNull String shellCommand) {
        return executePrivilegedCommand(shellCommand);
    }

    /**
     * Cleanup Shizuku resources
     */
    public void cleanup() {
        try {
            Shizuku.removeRequestPermissionResultListener(permissionResultListener);
            Shizuku.removeBinderReceivedListener(binderReceivedListener);
            Shizuku.removeBinderDeadListener(binderDeadListener);
            isInitialized = false;
            Log.i(TAG, "Shizuku cleaned up");
        } catch (Exception e) {
            Log.e(TAG, "Error during cleanup", e);
        }
    }

    /**
     * Result class for command execution
     */
    public static class CommandResult {
        public final String output;
        public final String error;
        public final int exitCode;
        public final boolean success;

        public CommandResult(String output, String error, int exitCode) {
            this.output = output != null ? output : "";
            this.error = error != null ? error : "";
            this.exitCode = exitCode;
            this.success = exitCode == 0;
        }
    }
}
