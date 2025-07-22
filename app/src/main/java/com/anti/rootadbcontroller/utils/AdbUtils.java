package com.anti.rootadbcontroller.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.anti.rootadbcontroller.utils.ShizukuUtils.CommandResult;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for ADB operations
 */
public class AdbUtils {
    private static final String TAG = "AdbUtils";

    /**
     * Execute an ADB shell command
     *
     * @param command The command to execute
     * @return The output of the command
     */
    public static String executeCommand(String command) throws IOException, InterruptedException {
        Process process = null;
        StringBuilder output = new StringBuilder();

        try {
            // Try to use root if available
            if (RootUtils.isRootAvailable()) {
                process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(command + "\n");
                os.writeBytes("exit\n");
                os.flush();
            } else {
                // Fall back to regular shell if root is not available
                process = Runtime.getRuntime().exec("sh -c " + command);
            }

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Read any errors
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                Log.e(TAG, "Error executing command: " + line);
                output.append("Error: ").append(line).append("\n");
            }

            // Wait for the process to complete
            if (process.waitFor(10, TimeUnit.SECONDS)) {
                int exitValue = process.exitValue();
                if (exitValue != 0) {
                    Log.w(TAG, "Command exited with value: " + exitValue);
                }
            } else {
                process.destroyForcibly();
                Log.e(TAG, "Command timed out after 10 seconds");
                output.append("Error: Command timed out after 10 seconds\n");
            }

        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return output.toString();
    }

    /**
     * Enable ADB over TCP/IP
     *
     * @param port The port to use (default: 5555)
     */
    public static void enableAdbOverTcp(int port) throws IOException, InterruptedException {
        // Try to use Shizuku first if available
        ShizukuUtils shizukuUtils = ShizukuUtils.getInstance();
        if (shizukuUtils.isShizukuAvailable() && shizukuUtils.hasShizukuPermission()) {
            CommandResult result = shizukuUtils.executeShellCommandWithResult(
                    "setprop service.adb.tcp.port " + port);

            if (result.isSuccess()) {
                shizukuUtils.executeShellCommandWithResult("stop adbd");
                shizukuUtils.executeShellCommandWithResult("start adbd");
                Log.i(TAG, "ADB over TCP enabled on port " + port + " using Shizuku");
                return;
            }
        }

        // Fallback to root if Shizuku is not available
        if (RootUtils.isRootAvailable()) {
            executeCommand("setprop service.adb.tcp.port " + port);
            executeCommand("stop adbd");
            executeCommand("start adbd");
            Log.i(TAG, "ADB over TCP enabled on port " + port + " using root");
        } else {
            Log.w(TAG, "Cannot enable ADB over TCP: Neither Shizuku nor root access is available.");
        }
    }

    /**
     * Disable ADB over TCP/IP
     */
    public static void disableAdbOverTcp() throws IOException, InterruptedException {
        ShizukuUtils shizukuUtils = ShizukuUtils.getInstance();
        if (shizukuUtils.isShizukuAvailable() && shizukuUtils.hasShizukuPermission()) {
            shizukuUtils.executeShellCommandWithResult("setprop service.adb.tcp.port -1");
            shizukuUtils.executeShellCommandWithResult("stop adbd");
            shizukuUtils.executeShellCommandWithResult("start adbd");
            Log.i(TAG, "ADB over TCP disabled using Shizuku");
            return;
        }

        if (RootUtils.isRootAvailable()) {
            executeCommand("setprop service.adb.tcp.port -1");
            executeCommand("stop adbd");
            executeCommand("start adbd");
            Log.i(TAG, "ADB over TCP disabled using root");
        } else {
            Log.w(TAG, "Cannot disable ADB over TCP: Neither Shizuku nor root access is available.");
        }
    }

    /**
     * Get the device's IP address
     */
    public static String getDeviceIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                int ipAddress = wifiInfo.getIpAddress();
                if (ipAddress != 0) {
                    return (ipAddress & 0xFF) + "." +
                           ((ipAddress >> 8) & 0xFF) + "." +
                           ((ipAddress >> 16) & 0xFF) + "." +
                           ((ipAddress >> 24) & 0xFF);
                }
            }
        }
        return getIpAddressFromNetworkInterfaces();
    }

    private static String getIpAddressFromNetworkInterfaces() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, "Failed to get IP address from network interfaces", ex);
        }
        return null;
    }

    public static ShizukuUtils.CommandResult readProcessOutput(Process process) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        String s;
        while ((s = stdInput.readLine()) != null) {
            output.append(s).append("\n");
        }

        while ((s = stdError.readLine()) != null) {
            error.append(s).append("\n");
        }

        process.waitFor();
        return new ShizukuUtils.CommandResult(process.exitValue() == 0, output.toString(), error.toString(), process.exitValue());
    }
}
