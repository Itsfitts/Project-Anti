package com.anti.rootadbcontroller.utils;

import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.annotation.NonNull;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuBinderWrapper;
import rikka.shizuku.SystemServiceHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

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
    public boolean initialize() {
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
    public boolean isShizukuAvailable() {
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
    public boolean hasShizukuPermission() {
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
     * Execute shell command with result
     */
    public CommandResult executeShellCommandWithResult(@NonNull String command) {
        if (!isShizukuAvailable() || !hasShizukuPermission()) {
            Log.w(TAG, "Shizuku not available or permission denied");
            return new CommandResult(false, "", "Shizuku not available or permission denied", -1);
        }

        try {
            Process process = Shizuku.newProcess(new String[]{"sh", "-c", command}, null, null);
            return readProcessOutput(process);
        } catch (Exception e) {
            Log.e(TAG, "Failed to execute command via Shizuku: " + command, e);
            return new CommandResult(false, "", e.getMessage(), -1);
        }
    }

    /**
     * Install APK via Shizuku
     */
    public boolean installApk(@NonNull String apkPath) {
        CommandResult result = executeShellCommandWithResult("pm install -r " + apkPath);
        return result != null && result.isSuccess() && result.output.contains("Success");
    }

    /**
     * Uninstall package via Shizuku
     */
    public boolean uninstallPackage(@NonNull String packageName) {
        CommandResult result = executeShellCommandWithResult("pm uninstall " + packageName);
        return result != null && result.isSuccess() && result.output.contains("Success");
    }

    /**
     * Grant permission to package
     */
    public boolean grantPermission(@NonNull String packageName, @NonNull String permission) {
        CommandResult result = executeShellCommandWithResult(
            "pm grant " + packageName + " " + permission);
        return result != null && result.isSuccess();
    }

    /**
     * Revoke permission from package
     */
    public boolean revokePermission(@NonNull String packageName, @NonNull String permission) {
        CommandResult result = executeShellCommandWithResult(
            "pm revoke " + packageName + " " + permission);
        return result != null && result.isSuccess();
    }

    /**
     * Enable/disable app component
     */
    public boolean setComponentEnabled(@NonNull String packageName, @NonNull String componentName, boolean enabled) {
        String state = enabled ? "enable" : "disable";
        CommandResult result = executeShellCommandWithResult(
            "pm " + state + " " + packageName + "/" + componentName);
        return result != null && result.isSuccess();
    }

    /**
     * Take screenshot via Shizuku
     */
    public boolean takeScreenshot(@NonNull String outputPath) {
        CommandResult result = executeShellCommandWithResult("screencap -p " + outputPath);
        return result != null && result.isSuccess();
    }

    /**
     * Read process output
     */
    private CommandResult readProcessOutput(Process process) {
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();

        try {
            // Read stdout
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            reader.close();

            // Read stderr
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                error.append(line).append("\n");
            }
            errorReader.close();

            int exitCode = process.waitFor();
            boolean success = exitCode == 0;

            return new CommandResult(success, output.toString().trim(), error.toString().trim(), exitCode);

        } catch (IOException | InterruptedException e) {
            Log.e(TAG, "Error reading process output", e);
            return new CommandResult(false, "", e.getMessage(), -1);
        }
    }

    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (isInitialized) {
            try {
                Shizuku.removeRequestPermissionResultListener(permissionResultListener);
                Shizuku.removeBinderReceivedListener(binderReceivedListener);
                Shizuku.removeBinderDeadListener(binderDeadListener);
                isInitialized = false;
                Log.i(TAG, "Shizuku cleanup completed");
            } catch (Exception e) {
                Log.e(TAG, "Error during cleanup", e);
            }
        }
    }

    /**
     * Command execution result
     */
    public static class CommandResult {
        private final boolean success;
        public final String output;
        public final String error;
        public final int exitCode;

        public CommandResult(boolean success, String output, String error, int exitCode) {
            this.success = success;
            this.output = output != null ? output : "";
            this.error = error != null ? error : "";
            this.exitCode = exitCode;
        }

        public boolean isSuccess() {
            return success;
        }

        @Override
        public String toString() {
            return "CommandResult{" +
                    "success=" + success +
                    ", output='" + output + '\'' +
                    ", error='" + error + '\'' +
                    ", exitCode=" + exitCode +
                    '}';
        }
    }
}
