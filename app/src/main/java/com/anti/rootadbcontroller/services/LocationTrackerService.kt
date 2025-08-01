package com.anti.rootadbcontroller.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Environment
import android.os.IBinder
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

/**
 * A service that tracks the device's location. It fetches the last known GPS location
 * and saves it to a file. This service is designed to run, fetch the location, and then
 * stop itself immediately.
 */
class LocationTrackerService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fetchLocation()
        stopSelf() // Stop the service after fetching the location
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        if (locationManager == null) {
            Log.e(TAG, "Location Manager not found")
            return
        }

        try {
            // Get last known location from GPS_PROVIDER
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                val locationString = "Lat: ${lastKnownLocation.latitude}, Lon: ${lastKnownLocation.longitude}"
                Log.d(TAG, "Location: $locationString")
                saveLocationToFile(locationString)
            } else {
                Log.d(TAG, "Last known location is not available.")
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Location permission not granted", e)
        }
    }

    /**
     * Saves the location data to a text file in the "Downloads" directory.
     * Each entry is timestamped.
     * @param locationData The location data string to be saved.
     */
    private fun saveLocationToFile(locationData: String) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputFile = File(downloadsDir, "extracted_location.txt")
        try {
            FileOutputStream(outputFile, true).use { fos -> // append
                fos.write("${Date()}: $locationData\n".toByteArray())
            }
            Log.d(TAG, "Location saved to ${outputFile.absolutePath}")
        } catch (e: IOException) {
            Log.e(TAG, "Failed to save location to file", e)
        }
    }

    companion object {
        private const val TAG = "LocationTrackerService"
    }
}
