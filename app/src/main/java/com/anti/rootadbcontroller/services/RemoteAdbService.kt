package com.anti.rootadbcontroller.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.anti.rootadbcontroller.MainActivity
import com.anti.rootadbcontroller.R
import com.anti.rootadbcontroller.utils.AdbUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

/**
 * Service that provides wireless ADB functionality over the internet.
 */
class RemoteAdbService : Service() {
    private var isRunning = false
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var serverSocket: ServerSocket? = null
    private val connectedClients = ConcurrentHashMap<String, Socket>()
    private lateinit var connectionCode: String

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d(TAG, "Network available, restarting server if needed.")
            if (isRunning) {
                stopRemoteAdbServer()
                startRemoteAdbServer()
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d(TAG, "Network lost, stopping server.")
            stopRemoteAdbServer()
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "RemoteAdbService created")
        val preferences = getSharedPreferences("remote_adb_prefs", MODE_PRIVATE)
        connectionCode = preferences.getString("connection_code", "") ?: ""
        if (connectionCode.isEmpty()) {
            connectionCode = String.format("%06d", (Math.random() * 1_000_000).toInt())
            preferences.edit().putString("connection_code", connectionCode).apply()
        }
        setupNetworkCallback()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "stop") {
            stopSelf()
            return START_NOT_STICKY
        }

        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Remote ADB running"))

        if (!isRunning) {
            isRunning = true
            startRemoteAdbServer()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "RemoteAdbService destroyed")
        stopRemoteAdbServer()
        serviceJob.cancel()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun setupNetworkCallback() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun startRemoteAdbServer() {
        serviceScope.launch {
            try {
                AdbUtils.enableAdbOverTcp(DEFAULT_ADB_PORT)
                serverSocket = ServerSocket(REMOTE_PORT)
                Log.d(TAG, "Server started on port $REMOTE_PORT")
                while (isActive) {
                    val socket = serverSocket ?: break
                    try {
                        val clientSocket = socket.accept()
                        Log.d(TAG, "Client connected: ${clientSocket.inetAddress.hostAddress}")
                        // Handle client connection in a new coroutine
                        launch { handleClient(clientSocket) }
                    } catch (e: IOException) {
                        if (isActive) Log.e(TAG, "Error accepting client connection", e)
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start server", e)
            }
        }
    }

    private fun stopRemoteAdbServer() {
        try {
            serverSocket?.close()
            serverSocket = null
            connectedClients.values.forEach { it.close() }
            connectedClients.clear()
            AdbUtils.disableAdbOverTcp()
            Log.d(TAG, "Remote ADB server stopped.")
        } catch (e: IOException) {
            Log.e(TAG, "Error stopping server", e)
        }
        isRunning = false
    }

    private fun handleClient(clientSocket: Socket) {
        val clientAddress = clientSocket.inetAddress.hostAddress
        connectedClients[clientAddress] = clientSocket
        try {
            val inputStream = clientSocket.getInputStream()
            val outputStream = clientSocket.getOutputStream()
            // Simple forwarding logic
            val adbSocket = Socket("127.0.0.1", DEFAULT_ADB_PORT)
            val adbInputStream = adbSocket.getInputStream()
            val adbOutputStream = adbSocket.getOutputStream()

            val clientToAdb = launch {
                try {
                    inputStream.copyTo(adbOutputStream)
                } finally {
                    adbSocket.shutdownOutput()
                }
            }
            val adbToClient = launch {
                try {
                    adbInputStream.copyTo(outputStream)
                } finally {
                    clientSocket.shutdownOutput()
                }
            }

            // Wait for both forwarding jobs to complete
            serviceScope.launch {
                clientToAdb.join()
                adbToClient.join()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error handling client $clientAddress", e)
        } finally {
            try {
                clientSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing client socket", e)
            }
            connectedClients.remove(clientAddress)
            Log.d(TAG, "Client disconnected: $clientAddress")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Remote ADB Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(text: String): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Remote ADB")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
    }

    companion object {
        private const val TAG = "RemoteAdbService"
        private const val NOTIFICATION_ID = 4321
        private const val CHANNEL_ID = "remote_adb_channel"
        private const val DEFAULT_ADB_PORT = 5555
        private const val REMOTE_PORT = 8888
    }
}
