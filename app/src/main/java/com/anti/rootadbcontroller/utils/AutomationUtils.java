package com.anti.rootadbcontroller.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.anti.rootadbcontroller.services.RemoteAdbService;
import com.anti.rootadbcontroller.services.ShizukuCallbacks;
import com.anti.rootadbcontroller.services.ShizukuManagerService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Utility class for automating features once access is gained
 */
public class AutomationUtils {
    private static final String TAG = "AutomationUtils";
    private static final String PREFS_NAME = "automation_prefs";
    private static final String KEY_AUTO_REMOTE_ADB = "auto_remote_adb";
    private static final String KEY_AUTO_SYSTEM_INFO = "auto_system_info";
    private static final String KEY_AUTO_PACKAGE_LIST = "auto_package_list";
    private static final String KEY_AUTO_NETWORK_INFO = "auto_network_info";

    private static final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Execute automated tasks when root access is gained
     *
     * @param context Application context
     */
    public static void executeRootAutomations(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Run automations in background
        executor.execute(() -> {
            Log.d(TAG, "Executing root automations");

            // Auto Remote ADB
            if (prefs.getBoolean(KEY_AUTO_REMOTE_ADB, false)) {
                startRemoteAdbService(context);
            }

            // Auto System Info
            if (prefs.getBoolean(KEY_AUTO_SYSTEM_INFO, false)) {
                collectSystemInfo(context);
            }

            // Auto Package List
            if (prefs.getBoolean(KEY_AUTO_PACKAGE_LIST, false)) {
                collectPackageList(context);
            }

            // Auto Network Info
            if (prefs.getBoolean(KEY_AUTO_NETWORK_INFO, false)) {
                collectNetworkInfo(context);
            }
        });
    }

    /**
     * Execute automated tasks when Shizuku access is gained
     *
     * @param context Application context
     * @param service ShizukuManagerService instance
     */
    public static void executeShizukuAutomations(Context context, ShizukuManagerService service) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Run automations in background
        executor.execute(() -> {
            Log.d(TAG, "Executing Shizuku automations");

            // Auto Remote ADB
            if (prefs.getBoolean(KEY_AUTO_REMOTE_ADB, false)) {
                startRemoteAdbService(context);
            }

            // Auto System Info
            if (prefs.getBoolean(KEY_AUTO_SYSTEM_INFO, false)) {
                if (service != null) {
                    service.getSystemInfo(new ShizukuCallbacks.SystemOperationCallback() {
                        @Override
                        public void onSystemOperationResult(boolean success, String operation, String result) {
                            if (success) {
                                saveToFile(context, "system_info.txt", result);
                            }
                        }
                    });
                }
            }

            // Auto Package List
            if (prefs.getBoolean(KEY_AUTO_PACKAGE_LIST, false)) {
                if (service != null) {
                    service.listPackages(true, new ShizukuCallbacks.SystemOperationCallback() {
                        @Override
                        public void onSystemOperationResult(boolean success, String operation, String result) {
                            if (success) {
                                saveToFile(context, "package_list.txt", result);
                            }
                        }
                    });
                }
            }

            // Auto Network Info
            if (prefs.getBoolean(KEY_AUTO_NETWORK_INFO, false)) {
                if (service != null) {
                    service.performNetworkOperation("netstat", new ShizukuCallbacks.NetworkCallback() {
                        @Override
                        public void onNetworkResult(boolean success, String data) {
                            if (success) {
                                saveToFile(context, "network_info.txt", data);
                            }
                        }
                    });
                }
            }
        });
    }

    private static void startRemoteAdbService(Context context) {
        Intent intent = new Intent(context, RemoteAdbService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        Log.i(TAG, "Started RemoteAdbService for automation.");
    }

    private static void collectSystemInfo(Context context) {
        executor.execute(() -> {
            try {
                String props = AdbUtils.executeCommand("getprop");
                saveToFile(context, "system_info_root.txt", props);
            } catch (Exception e) {
                Log.e(TAG, "Failed to collect system info with root", e);
            }
        });
    }

    private static void collectPackageList(Context context) {
        executor.execute(() -> {
            try {
                String packages = AdbUtils.executeCommand("pm list packages -f");
                saveToFile(context, "package_list_root.txt", packages);
            } catch (Exception e) {
                Log.e(TAG, "Failed to collect package list with root", e);
            }
        });
    }

    private static void collectNetworkInfo(Context context) {
        executor.execute(() -> {
            try {
                String netstat = AdbUtils.executeCommand("netstat -tuln");
                saveToFile(context, "network_info_root.txt", netstat);
            } catch (Exception e) {
                Log.e(TAG, "Failed to collect network info with root", e);
            }
        });
    }

    private static void saveToFile(Context context, String fileName, String content) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (dir == null) {
            Log.e(TAG, "Failed to get external files directory.");
            return;
        }
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        try (FileWriter writer = new FileWriter(file, true)) { // Append mode
            writer.append(content).append("\n\n");
            Log.i(TAG, "Saved data to " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to save data to file: " + fileName, e);
        }
    }

    /**
     * Set automation setting
     */
    public static void setAutomation(Context context, String key, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(key, enabled).apply();
    }

    /**
     * Get automation setting
     */
    public static boolean getAutomation(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    /**
     * Get all automation keys
     */
    public static List<String> getAllAutomationKeys() {
        List<String> keys = new ArrayList<>();
        keys.add(KEY_AUTO_REMOTE_ADB);
        keys.add(KEY_AUTO_SYSTEM_INFO);
        keys.add(KEY_AUTO_PACKAGE_LIST);
        keys.add(KEY_AUTO_NETWORK_INFO);
        return keys;
    }

    /**
     * Get key name for display
     */
    public static String getKeyDisplayName(String key) {
        switch (key) {
            case KEY_AUTO_REMOTE_ADB:
                return "Remote ADB Service";
            case KEY_AUTO_SYSTEM_INFO:
                return "System Information Collection";
            case KEY_AUTO_PACKAGE_LIST:
                return "Package List Collection";
            case KEY_AUTO_NETWORK_INFO:
                return "Network Information Collection";
            default:
                return key;
        }
    }
}
