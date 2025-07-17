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
                // Restart ADB to apply changes
                shizukuUtils.executeShellCommandWithResult("stop adbd");
                shizukuUtils.executeShellCommandWithResult("start adbd");
                Log.d(TAG, "Enabled ADB over TCP using Shizuku on port " + port);
                return;
            }
        }

        // Fall back to root method if Shizuku fails or is not available
        if (RootUtils.isRootAvailable()) {
            String response = executeCommand("setprop service.adb.tcp.port " + port);
            executeCommand("stop adbd");
            executeCommand("start adbd");
            Log.d(TAG, "Enabled ADB over TCP using root on port " + port + ": " + response);
            return;
        }

        // If all else fails, try to use regular shell (might not work)
        Log.w(TAG, "Enabling ADB over TCP without root or Shizuku may not work");
        executeCommand("setprop service.adb.tcp.port " + port);
        executeCommand("stop adbd");
        executeCommand("start adbd");
    }

    /**
     * Disable ADB over TCP/IP
     */
    public static void disableAdbOverTcp() throws IOException, InterruptedException {
        // Try to use Shizuku first if available
        ShizukuUtils shizukuUtils = ShizukuUtils.getInstance();
        if (shizukuUtils.isShizukuAvailable() && shizukuUtils.hasShizukuPermission()) {
            CommandResult result = shizukuUtils.executeShellCommandWithResult(
                    "setprop service.adb.tcp.port -1");

            if (result.isSuccess()) {
                // Restart ADB to apply changes
                shizukuUtils.executeShellCommandWithResult("stop adbd");
                shizukuUtils.executeShellCommandWithResult("start adbd");
                Log.d(TAG, "Disabled ADB over TCP using Shizuku");
                return;
            }
        }

        // Fall back to root method if Shizuku fails or is not available
        if (RootUtils.isRootAvailable()) {
            String response = executeCommand("setprop service.adb.tcp.port -1");
            executeCommand("stop adbd");
            executeCommand("start adbd");
            Log.d(TAG, "Disabled ADB over TCP using root: " + response);
            return;
        }

        // If all else fails, try to use regular shell (might not work)
        Log.w(TAG, "Disabling ADB over TCP without root or Shizuku may not work");
        executeCommand("setprop service.adb.tcp.port -1");
        executeCommand("stop adbd");
        executeCommand("start adbd");
    }

    /**
     * Get the device's IP address
     *
     * @param context The application context
     * @return The device's IP address or null if not available
     */
    public static String getDeviceIpAddress(Context context) {
        // Try to get Wi-Fi IP address first
        String wifiIp = getWifiIpAddress(context);
        if (wifiIp != null && !wifiIp.isEmpty() && !wifiIp.equals("0.0.0.0")) {
            return wifiIp;
        }

        // Try to get mobile data IP address
        String mobileIp = getMobileIpAddress();
        if (mobileIp != null && !mobileIp.isEmpty() && !mobileIp.equals("0.0.0.0")) {
            return mobileIp;
        }

        // If all else fails, try to get any available IP
        return getAnyIpAddress();
    }

    /**
     * Get the Wi-Fi IP address
     */
    private static String getWifiIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                return null;
            }

            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo == null) {
                return null;
            }

            int ipInt = wifiInfo.getIpAddress();
            return String.format("%d.%d.%d.%d",
                    (ipInt & 0xff), (ipInt >> 8 & 0xff),
                    (ipInt >> 16 & 0xff), (ipInt >> 24 & 0xff));
        } catch (Exception e) {
            Log.e(TAG, "Error getting Wi-Fi IP address", e);
            return null;
        }
    }

    /**
     * Get the mobile data IP address
     */
    private static String getMobileIpAddress() {
        try {
            // Iterate through network interfaces
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }

                String name = networkInterface.getName();
                if (name.startsWith("rmnet") || name.startsWith("pdp") || name.startsWith("ppp") ||
                        name.startsWith("data") || name.startsWith("wwan")) {

                    for (Enumeration<InetAddress> addresses = networkInterface.getInetAddresses(); addresses.hasMoreElements();) {
                        InetAddress address = addresses.nextElement();
                        if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                            return address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "Error getting mobile IP address", e);
        }

        return null;
    }

    /**
     * Get any available IP address
     */
    private static String getAnyIpAddress() {
        try {
            // Iterate through all network interfaces
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }

                for (Enumeration<InetAddress> addresses = networkInterface.getInetAddresses(); addresses.hasMoreElements();) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e(TAG, "Error getting any IP address", e);
        }

        return null;
    }

    /**
     * Check if the device has an internet connection
     *
     * @param context The application context
     * @return True if the device has an internet connection, false otherwise
     */
    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }

        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork == null) {
            return false;
        }

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    /**
     * Check if ADB over TCP is enabled
     *
     * @return True if ADB over TCP is enabled, false otherwise
     */
    public static boolean isAdbOverTcpEnabled() {
        try {
            ShizukuUtils shizukuUtils = ShizukuUtils.getInstance();
            if (shizukuUtils.isShizukuAvailable() && shizukuUtils.hasShizukuPermission()) {
                CommandResult result = shizukuUtils.executeShellCommandWithResult(
                        "getprop service.adb.tcp.port");

                String port = result.output.trim();
                return port != null && !port.isEmpty() && !port.equals("-1");
            }

            if (RootUtils.isRootAvailable()) {
                String response = executeCommand("getprop service.adb.tcp.port");
                String port = response.trim();
                return port != null && !port.isEmpty() && !port.equals("-1");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking ADB over TCP status", e);
        }

        return false;
    }

    /**
     * Get the current ADB over TCP port
     *
     * @return The current ADB over TCP port or -1 if not enabled
     */
    public static int getAdbOverTcpPort() {
        try {
            ShizukuUtils shizukuUtils = ShizukuUtils.getInstance();
            if (shizukuUtils.isShizukuAvailable() && shizukuUtils.hasShizukuPermission()) {
                CommandResult result = shizukuUtils.executeShellCommandWithResult(
                        "getprop service.adb.tcp.port");

                String port = result.output.trim();
                if (port != null && !port.isEmpty() && !port.equals("-1")) {
                    return Integer.parseInt(port);
                }
                return -1;
            }

            if (RootUtils.isRootAvailable()) {
                String response = executeCommand("getprop service.adb.tcp.port");
                String port = response.trim();
                if (port != null && !port.isEmpty() && !port.equals("-1")) {
                    return Integer.parseInt(port);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting ADB over TCP port", e);
        }

        return -1;
    }
}
