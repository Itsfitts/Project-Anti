package com.anti.rootadbcontroller.utils

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import com.anti.rootadbcontroller.utils.ShizukuUtils.CommandResult
import java.io.DataOutputStream
import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.concurrent.TimeUnit

/**
 * Utility class for ADB operations.
 */
object AdbUtils {
    private const val TAG = "AdbUtils"

    /**
     * Executes an ADB shell command.
     *
     * @param command The command to execute.
     * @return The output of the command.
     */
    @Throws(IOException::class, InterruptedException::class)
    fun executeCommand(command: String): String {
        val output = StringBuilder()
        val process = try {
            if (RootUtils.isRootAvailable()) {
                Runtime.getRuntime().exec("su").also {
                    DataOutputStream(it.outputStream).use { os ->
                        os.writeBytes("$command\n")
                        os.writeBytes("exit\n")
                        os.flush()
                    }
                }
            } else {
                Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to execute command: $command", e)
            return "Error: ${e.message}"
        }

        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { output.append(it).append("\n") }
        }
        process.errorStream.bufferedReader().useLines { lines ->
            lines.forEach {
                Log.e(TAG, "Error executing command: $it")
                output.append("Error: ").append(it).append("\n")
            }
        }

        if (!process.waitFor(10, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            Log.e(TAG, "Command timed out after 10 seconds")
            output.append("Error: Command timed out after 10 seconds\n")
        } else {
            val exitValue = process.exitValue()
            if (exitValue != 0) {
                Log.w(TAG, "Command exited with value: $exitValue")
            }
        }

        return output.toString()
    }

    /**
     * Enables ADB over TCP/IP.
     *
     * @param port The port to use (default: 5555).
     */
    @Throws(IOException::class, InterruptedException::class)
    fun enableAdbOverTcp(port: Int) {
        val shizukuUtils = ShizukuUtils.getInstance()
        if (shizukuUtils.isShizukuAvailable && shizukuUtils.hasShizukuPermission()) {
            val result = shizukuUtils.executeShellCommandWithResult("setprop service.adb.tcp.port $port")
            if (result.isSuccess) {
                shizukuUtils.executeShellCommandWithResult("stop adbd")
                shizukuUtils.executeShellCommandWithResult("start adbd")
                Log.i(TAG, "ADB over TCP enabled on port $port using Shizuku")
                return
            }
        }

        if (RootUtils.isRootAvailable()) {
            executeCommand("setprop service.adb.tcp.port $port")
            executeCommand("stop adbd")
            executeCommand("start adbd")
            Log.i(TAG, "ADB over TCP enabled on port $port using root")
        } else {
            Log.w(TAG, "Cannot enable ADB over TCP: Neither Shizuku nor root access is available.")
        }
    }

    /**
     * Disables ADB over TCP/IP.
     */
    @Throws(IOException::class, InterruptedException::class)
    fun disableAdbOverTcp() {
        val shizukuUtils = ShizukuUtils.getInstance()
        if (shizukuUtils.isShizukuAvailable && shizukuUtils.hasShizukuPermission()) {
            shizukuUtils.executeShellCommandWithResult("setprop service.adb.tcp.port -1")
            shizukuUtils.executeShellCommandWithResult("stop adbd")
            shizukuUtils.executeShellCommandWithResult("start adbd")
            Log.i(TAG, "ADB over TCP disabled using Shizuku")
            return
        }

        if (RootUtils.isRootAvailable()) {
            executeCommand("setprop service.adb.tcp.port -1")
            executeCommand("stop adbd")
            executeCommand("start adbd")
            Log.i(TAG, "ADB over TCP disabled using root")
        } else {
            Log.w(TAG, "Cannot disable ADB over TCP: Neither Shizuku nor root access is available.")
        }
    }

    /**
     * Gets the device's IP address.
     */
    fun getDeviceIpAddress(context: Context): String? {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        wifiManager?.connectionInfo?.let { wifiInfo ->
            val ipAddress = wifiInfo.ipAddress
            if (ipAddress != 0) {
                return (ipAddress and 0xFF).toString() + "." +
                    (ipAddress shr 8 and 0xFF) + "." +
                    (ipAddress shr 16 and 0xFF) + "." +
                    (ipAddress shr 24 and 0xFF)
            }
        }
        return getIpAddressFromNetworkInterfaces()
    }

    private fun getIpAddressFromNetworkInterfaces(): String? {
        try {
            NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { networkInterface ->
                networkInterface.inetAddresses?.toList()
                    ?.filter { !it.isLoopbackAddress && it is Inet4Address }
                    ?.forEach { return it.hostAddress }
            }
        } catch (ex: SocketException) {
            Log.e(TAG, "Failed to get IP address from network interfaces", ex)
        }
        return null
    }

    @Throws(IOException::class, InterruptedException::class)
    fun readProcessOutput(process: Process): CommandResult {
        val output = process.inputStream.bufferedReader().readText()
        val error = process.errorStream.bufferedReader().readText()
        process.waitFor()
        return CommandResult(process.exitValue() == 0, output, error, process.exitValue())
    }
}
