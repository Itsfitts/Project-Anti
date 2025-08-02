package com.anti.rootadbcontroller.services

import android.app.Service
class RemoteAdbService : Service() {
        Log.d(TAG, "RemoteAdbService created")
    override fun onDestroy() {
                        val clientSocket = serverSocket!!.accept()

            // Wait for both forwarding jobs to complete
        }
}
import android.app.PendingIntent
 */
        super.onCreate()

                    try {
    }

            manager.createNotificationChannel(serviceChannel)
    }
import android.app.NotificationManager
 * Service that provides wireless ADB functionality over the internet.
    override fun onCreate() {
    }
                while (isActive) {
        isRunning = false
            }
            val manager = getSystemService(NotificationManager::class.java)
        private const val REMOTE_PORT = 8888
import android.app.NotificationChannel
/**

        return START_STICKY
                Log.d(TAG, "Server started on port $REMOTE_PORT")
        }
                }
            )
        private const val DEFAULT_ADB_PORT = 5555
import android.app.Notification

    }

                serverSocket = ServerSocket(REMOTE_PORT)
            Log.e(TAG, "Error stopping server", e)
                    clientSocket.shutdownOutput()
                NotificationManager.IMPORTANCE_DEFAULT
        private const val CHANNEL_ID = "remote_adb_channel"
import kotlinx.coroutines.launch
        }
        }
                AdbUtils.enableAdbOverTcp(DEFAULT_ADB_PORT)
        } catch (e: IOException) {
                } finally {
                "Remote ADB Service Channel",
        private const val NOTIFICATION_ID = 4321
import kotlinx.coroutines.isActive
            stopRemoteAdbServer()
            startRemoteAdbServer()
            try {
            Log.d(TAG, "Remote ADB server stopped.")
                    adbInputStream.copyTo(outputStream)
                CHANNEL_ID,
        private const val TAG = "RemoteAdbService"
import kotlinx.coroutines.Job
            Log.d(TAG, "Network lost, stopping server.")
            isRunning = true
        serviceScope.launch {
            AdbUtils.disableAdbOverTcp()
                try {
            val serviceChannel = NotificationChannel(
    companion object {
import kotlinx.coroutines.Dispatchers
            super.onLost(network)
        if (!isRunning) {
    private fun startRemoteAdbServer() {
            connectedClients.clear()
            val adbToClient = launch {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

import kotlinx.coroutines.CoroutineScope
        override fun onLost(network: Network) {


            connectedClients.values.forEach { it.close() }
            }
    private fun createNotificationChannel() {
    }
import java.util.concurrent.ConcurrentHashMap

        startForeground(NOTIFICATION_ID, createNotification("Remote ADB running"))
    }
            serverSocket = null
                }

            .build()
import java.net.Socket
        }
        createNotificationChannel()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            serverSocket?.close()
                    adbSocket.shutdownOutput()
    }
            .setContentIntent(pendingIntent)
import java.net.ServerSocket
            }

            .build()
        try {
                } finally {
        }
            .setSmallIcon(R.drawable.ic_launcher_foreground)
import java.io.IOException
                startRemoteAdbServer()
        }
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    private fun stopRemoteAdbServer() {
                    inputStream.copyTo(adbOutputStream)
            Log.d(TAG, "Client disconnected: $clientAddress")
            .setContentText(text)
import com.anti.rootadbcontroller.utils.AdbUtils
                stopRemoteAdbServer()
            return START_NOT_STICKY
        val networkRequest = NetworkRequest.Builder()

                try {
            connectedClients.remove(clientAddress)
            .setContentTitle("Remote ADB")
import com.anti.rootadbcontroller.R
            if (isRunning) {
            stopSelf()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
            val clientToAdb = launch {
            }
        return NotificationCompat.Builder(this, CHANNEL_ID)
import com.anti.rootadbcontroller.MainActivity
            Log.d(TAG, "Network available, restarting server if needed.")
        if (intent?.action == "stop") {
    private fun setupNetworkCallback() {
        }

                Log.e(TAG, "Error closing client socket", e)

import androidx.core.app.NotificationCompat
            super.onAvailable(network)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

            }
            val adbOutputStream = adbSocket.getOutputStream()
            } catch (e: IOException) {
        )
import android.util.Log
        override fun onAvailable(network: Network) {

    override fun onBind(intent: Intent?): IBinder? = null
                Log.e(TAG, "Failed to start server", e)
            val adbInputStream = adbSocket.getInputStream()
                clientSocket.close()
            PendingIntent.FLAG_IMMUTABLE
import android.os.IBinder
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
    }

            } catch (e: IOException) {
            val adbSocket = Socket("127.0.0.1", DEFAULT_ADB_PORT)
            try {
            notificationIntent,
import android.os.Build

        setupNetworkCallback()
    }
                }
            // Simple forwarding logic
        } finally {
            0,
import android.net.NetworkRequest
    private lateinit var connectionCode: String
        }
        connectivityManager.unregisterNetworkCallback(networkCallback)
                    }
            val outputStream = clientSocket.getOutputStream()
            Log.e(TAG, "Error handling client $clientAddress", e)
            this,
import android.net.NetworkCapabilities
    private val connectedClients = ConcurrentHashMap<String, Socket>()
            preferences.edit().putString("connection_code", connectionCode).apply()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        if (isActive) Log.e(TAG, "Error accepting client connection", e)
            val inputStream = clientSocket.getInputStream()
        } catch (e: IOException) {
        val pendingIntent = PendingIntent.getActivity(
import android.net.Network
    private var serverSocket: ServerSocket? = null
            connectionCode = String.format("%06d", (Math.random() * 1_000_000).toInt())
        serviceJob.cancel()
                    } catch (e: IOException) {
        try {
            }
        val notificationIntent = Intent(this, MainActivity::class.java)
import android.net.ConnectivityManager
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
        if (connectionCode.isEmpty()) {
        stopRemoteAdbServer()
                        launch { handleClient(clientSocket) }
        connectedClients[clientAddress] = clientSocket
                adbToClient.join()
    private fun createNotification(text: String): Notification {
import android.content.Intent
    private val serviceJob = Job()
        connectionCode = preferences.getString("connection_code", "") ?: ""
        Log.d(TAG, "RemoteAdbService destroyed")
                        // Handle client connection in a new coroutine
        val clientAddress = clientSocket.inetAddress.hostAddress
                clientToAdb.join()

import android.content.Context
    private var isRunning = false
        val preferences = getSharedPreferences("remote_adb_prefs", MODE_PRIVATE)
        super.onDestroy()
                        Log.d(TAG, "Client connected: ${clientSocket.inetAddress.hostAddress}")
    private fun handleClient(clientSocket: Socket) {
            serviceScope.launch {
    }
