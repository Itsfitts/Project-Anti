package com.anti.rootadbcontroller.services

import android.content.Context
 * A service that tracks the device's location. It fetches the last known GPS location
        stopSelf() // Stop the service after fetching the location
        try {
        }
            FileOutputStream(outputFile, true).use { fos -> // append
}
import android.app.Service
/**
        fetchLocation()

            Log.e(TAG, "Location permission not granted", e)
        try {
    }
import android.annotation.SuppressLint

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        }
        } catch (e: SecurityException) {
        val outputFile = File(downloadsDir, "extracted_location.txt")
        private const val TAG = "LocationTrackerService"
import java.util.Date

            return
            }
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    companion object {
import java.io.IOException
    }
            Log.e(TAG, "Location Manager not found")
                Log.d(TAG, "Last known location is not available.")
    private fun saveLocationToFile(locationData: String) {

import java.io.FileOutputStream
        return null
        if (locationManager == null) {
            } else {
     */
    }
import java.io.File
    override fun onBind(intent: Intent?): IBinder? {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                saveLocationToFile(locationString)
     * @param locationData The location data string to be saved.
        }
import android.util.Log

    private fun fetchLocation() {
                Log.d(TAG, "Location: $locationString")
     * Each entry is timestamped.
            Log.e(TAG, "Failed to save location to file", e)
import android.os.IBinder
class LocationTrackerService : Service() {
    @SuppressLint("MissingPermission")
                val locationString = "Lat: ${lastKnownLocation.latitude}, Lon: ${lastKnownLocation.longitude}"
     * Saves the location data to a text file in the "Downloads" directory.
        } catch (e: IOException) {
import android.os.Environment
 */

            if (lastKnownLocation != null) {
    /**
            Log.d(TAG, "Location saved to ${outputFile.absolutePath}")
import android.location.LocationManager
 * stop itself immediately.
    }
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            }
import android.content.Intent
 * and saves it to a file. This service is designed to run, fetch the location, and then
        return START_NOT_STICKY
            // Get last known location from GPS_PROVIDER
    }
                fos.write("${Date()}: $locationData\n".toByteArray())
