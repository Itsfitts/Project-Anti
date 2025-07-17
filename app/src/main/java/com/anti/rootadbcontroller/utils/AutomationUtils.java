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
                    service.getDeviceInfoAsync(new ShizukuCallbacks.CommandCallback() {
                        @Override
                        public void onResult(ShizukuUtils.CommandResult result) {
                            saveToFile(context, "system_info.txt", result.output);
                        }
                    });
                }
            }

            // Auto Package List
            if (prefs.getBoolean(KEY_AUTO_PACKAGE_LIST, false)) {
                if (service != null) {
                    service.getInstalledPackagesAsync(new ShizukuCallbacks.CommandCallback() {
                        @Override
                        public void onResult(ShizukuUtils.CommandResult result) {
                            saveToFile(context, "package_list.txt", result.output);
                        }
                    });
                }
            }

            // Auto Network Info
            if (prefs.getBoolean(KEY_AUTO_NETWORK_INFO, false)) {
                if (service != null) {
                    service.executeCommandAsync("ip addr && netstat -tunap", new ShizukuCallbacks.CommandCallback() {
                        @Override
                        public void onResult(ShizukuUtils.CommandResult result) {
                            saveToFile(context, "network_info.txt", result.output);
                        }
                    });
                }
            }
        });
    }

    /**
     * Start Remote ADB service
     */
    private static void startRemoteAdbService(Context context) {
        Log.d(TAG, "Auto-starting Remote ADB service");
        Intent serviceIntent = new Intent(context, RemoteAdbService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    /**
     * Collect system information using root commands
     */
    private static void collectSystemInfo(Context context) {
        Log.d(TAG, "Auto-collecting system information (root)");

        List<String> commands = new ArrayList<>();
        commands.add("getprop");
        commands.add("cat /proc/cpuinfo");
        commands.add("cat /proc/meminfo");
        commands.add("dumpsys battery");

        RootUtils.executeRootCommands(commands, output -> {
            saveToFile(context, "system_info.txt", String.join("\n", output));
        });
    }

    /**
     * Collect package list using root commands
     */
    private static void collectPackageList(Context context) {
        Log.d(TAG, "Auto-collecting package list (root)");

        RootUtils.executeRootCommand("pm list packages -f", output -> {
            saveToFile(context, "package_list.txt", String.join("\n", output));
        });
    }

    /**
     * Collect network information using root commands
     */
    private static void collectNetworkInfo(Context context) {
        Log.d(TAG, "Auto-collecting network information (root)");

        List<String> commands = new ArrayList<>();
        commands.add("ip addr");
        commands.add("netstat -tunap");
        commands.add("cat /proc/net/route");

        RootUtils.executeRootCommands(commands, output -> {
            saveToFile(context, "network_info.txt", String.join("\n", output));
        });
    }

    /**
     * Save data to a file in Downloads directory
     */
    private static void saveToFile(Context context, String fileName, String data) {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, fileName);

        try {
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();

            Log.d(TAG, "Successfully saved data to " + file.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "Failed to save data to file " + fileName, e);
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
