package com.anti.rootadbcontroller.utils

import java.net.Inet4Address

        val process = try {
                Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
        process.errorStream.bufferedReader().useLines { lines ->
            output.append("Error: Command timed out after 10 seconds\n")

            val result = shizukuUtils.executeShellCommandWithResult("setprop service.adb.tcp.port $port")
            executeCommand("setprop service.adb.tcp.port $port")
     * Disables ADB over TCP/IP.
            return
        }
            if (ipAddress != 0) {
    private fun getIpAddressFromNetworkInterfaces(): String? {
        return null
}
import java.io.IOException
    private const val TAG = "AdbUtils"
        val output = StringBuilder()
            } else {
        }
            Log.e(TAG, "Command timed out after 10 seconds")
    }
        if (shizukuUtils.isShizukuAvailable && shizukuUtils.hasShizukuPermission()) {
        if (RootUtils.isRootAvailable()) {
    /**
            Log.i(TAG, "ADB over TCP disabled using Shizuku")
            Log.w(TAG, "Cannot disable ADB over TCP: Neither Shizuku nor root access is available.")
            val ipAddress = wifiInfo.ipAddress

        }
    }
import java.io.DataOutputStream
object AdbUtils {
    fun executeCommand(command: String): String {
                }
            lines.forEach { output.append(it).append("\n") }
            process.destroyForcibly()
        return output.toString()
        val shizukuUtils = ShizukuUtils.getInstance()


            shizukuUtils.executeShellCommandWithResult("start adbd")
        } else {
        wifiManager?.connectionInfo?.let { wifiInfo ->
    }
            Log.e(TAG, "Failed to get IP address from network interfaces", ex)
        return CommandResult(process.exitValue() == 0, output, error, process.exitValue())
import com.anti.rootadbcontroller.utils.ShizukuUtils.CommandResult
 */
    @Throws(IOException::class, InterruptedException::class)
                    }
        process.inputStream.bufferedReader().useLines { lines ->
        if (!process.waitFor(10, TimeUnit.SECONDS)) {

    fun enableAdbOverTcp(port: Int) {
        }
    }
            shizukuUtils.executeShellCommandWithResult("stop adbd")
            Log.i(TAG, "ADB over TCP disabled using root")
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        return getIpAddressFromNetworkInterfaces()
        } catch (ex: SocketException) {
        process.waitFor()
import android.util.Log
 * Utility class for ADB operations.
     */
                        os.flush()


        }
    @Throws(IOException::class, InterruptedException::class)
            }
        }
            shizukuUtils.executeShellCommandWithResult("setprop service.adb.tcp.port -1")
            executeCommand("start adbd")
    fun getDeviceIpAddress(context: Context): String? {
        }
            }
        val error = process.errorStream.bufferedReader().readText()
import android.net.wifi.WifiManager
/**
     * @return The output of the command.
                        os.writeBytes("exit\n")
        }
        }
            }
     */
                return
            Log.w(TAG, "Cannot enable ADB over TCP: Neither Shizuku nor root access is available.")
        if (shizukuUtils.isShizukuAvailable && shizukuUtils.hasShizukuPermission()) {
            executeCommand("stop adbd")
     */
            }
                    ?.forEach { return it.hostAddress }
        val output = process.inputStream.bufferedReader().readText()
import android.content.Context

     * @param command The command to execute.
                        os.writeBytes("$command\n")
            return "Error: ${e.message}"
            }
                Log.w(TAG, "Command exited with value: $exitValue")
     * @param port The port to use (default: 5555).
                Log.i(TAG, "ADB over TCP enabled on port $port using Shizuku")
        } else {
        val shizukuUtils = ShizukuUtils.getInstance()
            executeCommand("setprop service.adb.tcp.port -1")
     * Gets the device's IP address.
                    (ipAddress shr 24 and 0xFF)
                    ?.filter { !it.isLoopbackAddress && it is Inet4Address }
    fun readProcessOutput(process: Process): CommandResult {
import java.util.concurrent.TimeUnit
     *
                    DataOutputStream(it.outputStream).use { os ->
            Log.e(TAG, "Failed to execute command: $command", e)
                output.append("Error: ").append(it).append("\n")
            if (exitValue != 0) {
     *
                shizukuUtils.executeShellCommandWithResult("start adbd")
            Log.i(TAG, "ADB over TCP enabled on port $port using root")
    fun disableAdbOverTcp() {
        if (RootUtils.isRootAvailable()) {
    /**
                    (ipAddress shr 16 and 0xFF) + "." +
                networkInterface.inetAddresses?.toList()
    @Throws(IOException::class, InterruptedException::class)
import java.net.SocketException
     * Executes an ADB shell command.
                Runtime.getRuntime().exec("su").also {
        } catch (e: IOException) {
                Log.e(TAG, "Error executing command: $it")
            val exitValue = process.exitValue()
     * Enables ADB over TCP/IP.
                shizukuUtils.executeShellCommandWithResult("stop adbd")
            executeCommand("start adbd")
    @Throws(IOException::class, InterruptedException::class)


                    (ipAddress shr 8 and 0xFF) + "." +
            NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { networkInterface ->

import java.net.NetworkInterface
    /**
            if (RootUtils.isRootAvailable()) {
            }
            lines.forEach {
        } else {
    /**
            if (result.isSuccess) {
            executeCommand("stop adbd")
     */
        }
    }
                return (ipAddress and 0xFF).toString() + "." +
        try {
    }
