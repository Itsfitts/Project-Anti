package com.anti.rootadbcontroller.utils;

import android.util.Log;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import eu.chainfire.libsuperuser.Shell;

/**
 * A utility class for executing root commands and performing various system-level operations
 * that require elevated privileges. This class provides a simplified interface for interacting
 * with the system shell as the root user.
 */
public class RootUtils {
    private static final String TAG = "RootUtils";
    private static final Executor executor = Executors.newSingleThreadExecutor();

    /**
     * Checks if root access is available on the device.
     * @return true if root is available, false otherwise.
     */
    public static boolean isRootAvailable() {
        return Shell.SU.available();
    }

    /**
     * Executes a single command with root privileges.
     * @param command The command to execute.
     * @param callback A callback to handle the command's output or any errors.
     */
    public static void executeRootCommand(String command, CommandCallback callback) {
        executor.execute(() -> {
            try {
                List<String> output = Shell.SU.run(command);
                if (output != null) {
                    callback.onSuccess(output);
                } else {
                    callback.onFailure("Command execution failed");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error executing root command", e);
                callback.onFailure(e.getMessage());
            }
        });
    }

    /**
     * Executes multiple commands in a single root shell session.
     * @param commands A list of commands to execute.
     * @param callback A callback to handle the output or any errors.
     */
    public static void executeRootCommands(List<String> commands, CommandCallback callback) {
        executor.execute(() -> {
            try {
                List<String> output = Shell.SU.run(commands);
                if (output != null) {
                    callback.onSuccess(output);
                } else {
                    callback.onFailure("Commands execution failed");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error executing root commands", e);
                callback.onFailure(e.getMessage());
            }
        });
    }

    /**
     * Installs an APK silently in the background without user interaction.
     * @param apkPath The file path to the APK to be installed.
     * @param callback A callback to handle the installation result.
     */
    public static void silentInstall(String apkPath, CommandCallback callback) {
        String command = "pm install -r " + apkPath;
        executeRootCommand(command, callback);
    }

    /**
     * Uninstalls an application silently in the background without user interaction.
     * @param packageName The package name of the application to uninstall.
     * @param callback A callback to handle the uninstallation result.
     */
    public static void silentUninstall(String packageName, CommandCallback callback) {
        String command = "pm uninstall " + packageName;
        executeRootCommand(command, callback);
    }

    /**
     * Clears the data of a specific application, effectively resetting it.
     * @param packageName The package name of the application to clear.
     * @param callback A callback to handle the result.
     */
    public static void clearAppData(String packageName, CommandCallback callback) {
        String command = "pm clear " + packageName;
        executeRootCommand(command, callback);
    }

    /**
     * Reboots the device.
     * @param callback A callback to handle the result.
     */
    public static void rebootDevice(CommandCallback callback) {
        executeRootCommand("reboot", callback);
    }

    /**
     * Takes a screenshot and saves it to the specified path.
     * @param path The path to save the screenshot.
     * @param callback A callback to handle the result.
     */
    public static void takeScreenshot(String path, CommandCallback callback) {
        String command = "screencap -p " + path;
        executeRootCommand(command, callback);
    }

    /**
     * A callback interface for handling the results of root command execution.
     */
    public interface CommandCallback {
        /**
         * Called when the command executes successfully.
         * @param output The output of the command.
         */
        void onSuccess(List<String> output);

        /**
         * Called when the command fails to execute.
         * @param error The error message.
         */
        void onFailure(String error);
    }
}
