package com.anti.rootadbcontroller.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.anti.rootadbcontroller.MainActivity;
import com.anti.rootadbcontroller.R;
import com.anti.rootadbcontroller.utils.AdbUtils;
import com.anti.rootadbcontroller.utils.ShizukuUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service that provides wireless ADB functionality over internet
 */
public class RemoteAdbService extends Service {
    private static final String TAG = "RemoteAdbService";
    private static final int NOTIFICATION_ID = 4321;
    private static final String CHANNEL_ID = "remote_adb_channel";
    private static final int DEFAULT_ADB_PORT = 5555;
    private static final int REMOTE_PORT = 8888;

    private boolean isRunning = false;
    private ExecutorService executor;
    private ServerSocket serverSocket;
    private final Map<String, Socket> connectedClients = new HashMap<>();
    private SharedPreferences preferences;
    private String connectionCode = "";

    // Network callback to handle connectivity changes
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "RemoteAdbService created");

        executor = Executors.newCachedThreadPool();
        preferences = getSharedPreferences("remote_adb_prefs", MODE_PRIVATE);

        // Generate a random 6-digit connection code if one doesn't exist
        connectionCode = preferences.getString("connection_code", "");
        if (connectionCode.isEmpty()) {
            connectionCode = String.format("%06d", (int)(Math.random() * 1000000));
            preferences.edit().putString("connection_code", connectionCode).apply();
        }

        // Register for network callbacks to handle connectivity changes
        setupNetworkCallback();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "stop".equals(intent.getAction())) {
            stopSelf();
            return START_NOT_STICKY;
        }

        // Create notification channel for Android 8.0+
        createNotificationChannel();

        // Start foreground service with notification
        startForeground(NOTIFICATION_ID, createNotification("Remote ADB running"));

        // Start the remote ADB service
        if (!isRunning) {
            isRunning = true;
            startRemoteAdbServer();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "RemoteAdbService destroyed");

        isRunning = false;

        // Unregister network callback
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }

        // Close all client connections
        synchronized (connectedClients) {
            for (Socket socket : connectedClients.values()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing client socket", e);
                }
            }
            connectedClients.clear();
        }

        // Close server socket
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing server socket", e);
            }
        }

        // Shutdown executor
        if (executor != null) {
            executor.shutdown();
        }

        // Disable ADB over network if it was enabled
        disableAdbOverNetwork();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Setup network callback to handle connectivity changes
     */
    private void setupNetworkCallback() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                Log.d(TAG, "Network available");
                updateNotification("Network connected. Remote ADB running.");
                // Re-enable ADB over network with the new network
                enableAdbOverNetwork();
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                Log.d(TAG, "Network lost");
                updateNotification("Network disconnected. Waiting for connection...");
            }
        };

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    /**
     * Start the remote ADB server
     */
    private void startRemoteAdbServer() {
        executor.execute(() -> {
            try {
                // First enable ADB over network on the device
                enableAdbOverNetwork();

                // Open server socket to accept client connections
                serverSocket = new ServerSocket(REMOTE_PORT);
                Log.d(TAG, "Server started on port " + REMOTE_PORT);
                updateNotification("Waiting for connections. Code: " + connectionCode);

                while (isRunning) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        String clientAddress = clientSocket.getInetAddress().getHostAddress();
                        Log.d(TAG, "New connection from: " + clientAddress);

                        // Handle client connection in a separate thread
                        handleClientConnection(clientSocket);

                    } catch (IOException e) {
                        if (isRunning) {
                            Log.e(TAG, "Error accepting client connection", e);
                        }
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error starting server", e);
                updateNotification("Error starting server: " + e.getMessage());
            }
        });
    }

    /**
     * Handle client connection
     */
    private void handleClientConnection(Socket clientSocket) {
        executor.execute(() -> {
            String clientAddress = clientSocket.getInetAddress().getHostAddress();

            try {
                // Add to connected clients
                synchronized (connectedClients) {
                    connectedClients.put(clientAddress, clientSocket);
                }

                updateNotification("Connected to: " + clientAddress);

                // Create streams for communication
                byte[] buffer = new byte[1024];
                int bytesRead;

                // Simple protocol implementation
                // First, verify connection code
                String receivedCode = "";
                bytesRead = clientSocket.getInputStream().read(buffer);
                if (bytesRead > 0) {
                    receivedCode = new String(buffer, 0, bytesRead).trim();
                }

                if (!connectionCode.equals(receivedCode)) {
                    Log.e(TAG, "Invalid connection code from: " + clientAddress);
                    clientSocket.getOutputStream().write("INVALID_CODE".getBytes());
                    clientSocket.close();

                    synchronized (connectedClients) {
                        connectedClients.remove(clientAddress);
                    }
                    return;
                }

                // Send acknowledgment
                clientSocket.getOutputStream().write("CONNECTED".getBytes());

                // Process commands from client
                while (isRunning && clientSocket.isConnected()) {
                    bytesRead = clientSocket.getInputStream().read(buffer);
                    if (bytesRead <= 0) break;

                    String command = new String(buffer, 0, bytesRead).trim();
                    Log.d(TAG, "Received command: " + command);

                    // Execute ADB command
                    String response = executeAdbCommand(command);

                    // Send response back to client
                    clientSocket.getOutputStream().write(response.getBytes());
                }

            } catch (IOException e) {
                Log.e(TAG, "Error handling client: " + clientAddress, e);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing client socket", e);
                }

                synchronized (connectedClients) {
                    connectedClients.remove(clientAddress);
                }

                updateNotification("Client disconnected: " + clientAddress);
            }
        });
    }

    /**
     * Execute ADB command using available methods (Shizuku or root)
     */
    private String executeAdbCommand(String command) {
        ShizukuUtils shizukuUtils = ShizukuUtils.getInstance();

        // Try using Shizuku first if available
        if (shizukuUtils.isShizukuAvailable() && shizukuUtils.hasShizukuPermission()) {
            ShizukuUtils.CommandResult result = shizukuUtils.executeShellCommandWithResult(command);
            return result.output + "\n" + result.error;
        }

        // Fall back to AdbUtils if Shizuku is not available
        try {
            return AdbUtils.executeCommand(command);
        } catch (Exception e) {
            Log.e(TAG, "Error executing ADB command", e);
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Enable ADB over network
     */
    private void enableAdbOverNetwork() {
        ShizukuUtils shizukuUtils = ShizukuUtils.getInstance();

        if (shizukuUtils.isShizukuAvailable() && shizukuUtils.hasShizukuPermission()) {
            // Using Shizuku to enable ADB over network
            ShizukuUtils.CommandResult result = shizukuUtils.executeShellCommandWithResult(
                    "setprop service.adb.tcp.port " + DEFAULT_ADB_PORT);

            if (result.isSuccess()) {
                // Restart ADB daemon to apply changes
                shizukuUtils.executeShellCommandWithResult("stop adbd");
                shizukuUtils.executeShellCommandWithResult("start adbd");

                Log.d(TAG, "ADB over network enabled successfully");

                // Get the device's IP address
                ShizukuUtils.CommandResult ipResult = shizukuUtils.executeShellCommandWithResult(
                        "ip -f inet addr show wlan0 | grep -Po 'inet \\K[\\d.]+'");

                String ipAddress = ipResult.output.trim();
                if (!ipAddress.isEmpty()) {
                    updateNotification("ADB enabled on " + ipAddress + ":" + DEFAULT_ADB_PORT +
                            " - Code: " + connectionCode);
                }
            } else {
                Log.e(TAG, "Failed to enable ADB over network: " + result.error);
                updateNotification("Failed to enable ADB over network");
            }
        } else {
            // Fall back to AdbUtils
            try {
                AdbUtils.enableAdbOverTcp(DEFAULT_ADB_PORT);
                Log.d(TAG, "ADB over network enabled using AdbUtils");

                String ipAddress = AdbUtils.getDeviceIpAddress(this);
                if (ipAddress != null && !ipAddress.isEmpty()) {
                    updateNotification("ADB enabled on " + ipAddress + ":" + DEFAULT_ADB_PORT +
                            " - Code: " + connectionCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error enabling ADB over network", e);
                updateNotification("Error enabling ADB over network: " + e.getMessage());
            }
        }
    }

    /**
     * Disable ADB over network
     */
    private void disableAdbOverNetwork() {
        ShizukuUtils shizukuUtils = ShizukuUtils.getInstance();

        if (shizukuUtils.isShizukuAvailable() && shizukuUtils.hasShizukuPermission()) {
            // Using Shizuku to disable ADB over network
            ShizukuUtils.CommandResult result = shizukuUtils.executeShellCommandWithResult(
                    "setprop service.adb.tcp.port -1");

            if (result.isSuccess()) {
                // Restart ADB daemon to apply changes
                shizukuUtils.executeShellCommandWithResult("stop adbd");
                shizukuUtils.executeShellCommandWithResult("start adbd");

                Log.d(TAG, "ADB over network disabled successfully");
            } else {
                Log.e(TAG, "Failed to disable ADB over network: " + result.error);
            }
        } else {
            // Fall back to AdbUtils
            try {
                AdbUtils.disableAdbOverTcp();
                Log.d(TAG, "ADB over network disabled using AdbUtils");
            } catch (Exception e) {
                Log.e(TAG, "Error disabling ADB over network", e);
            }
        }
    }

    /**
     * Create notification channel (required for Android 8.0+)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Remote ADB Service";
            String description = "Notifications for the Remote ADB Service";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Create service notification
     */
    private Notification createNotification(String content) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent stopIntent = new Intent(this, RemoteAdbService.class);
        stopIntent.setAction("stop");
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Remote ADB Service")
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
                .build();
    }

    /**
     * Update service notification
     */
    private void updateNotification(String content) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, createNotification(content));
    }

    /**
     * Get the connection information for clients
     */
    public static class ConnectionInfo {
        public final String ipAddress;
        public final int port;
        public final String connectionCode;

        public ConnectionInfo(String ipAddress, int port, String connectionCode) {
            this.ipAddress = ipAddress;
            this.port = port;
            this.connectionCode = connectionCode;
        }
    }

    /**
     * Get current connection information
     */
    public static ConnectionInfo getConnectionInfo(Context context) {
        String ipAddress = AdbUtils.getDeviceIpAddress(context);
        int port = DEFAULT_ADB_PORT;
        String code = context.getSharedPreferences("remote_adb_prefs", MODE_PRIVATE)
                .getString("connection_code", "");

        return new ConnectionInfo(ipAddress, port, code);
    }
}
