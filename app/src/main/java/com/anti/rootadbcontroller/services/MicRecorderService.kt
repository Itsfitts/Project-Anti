package com.anti.rootadbcontroller.services

import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * A service for recording audio from the microphone. It can be started and stopped,
 * and it saves the recordings to the device's public "Downloads" directory.
 * Each recording is saved as a .3gp file with a timestamp.
 */
class MicRecorderService : Service() {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var outputFile: String? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
        return START_NOT_STICKY
    }

    /**
     * Starts the audio recording. It sets up the MediaRecorder, prepares it, and starts recording.
     * The output file is created in the "Recordings" subdirectory of the "Downloads" folder.
     */
    private fun startRecording() {
        outputFile = getOutputFilePath()
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        mediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile)

            try {
                prepare()
                start()
                isRecording = true
                Log.d(TAG, "Recording started, saving to: $outputFile")
            } catch (e: IOException) {
                Log.e(TAG, "startRecording: Failed", e)
            }
        }
    }

    /**
     * Stops the audio recording and releases the MediaRecorder resources.
     */
    private fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
                release()
            } catch (e: RuntimeException) {
                Log.w(TAG, "stopRecording: No recording to stop or already stopped.", e)
            }
        }
        mediaRecorder = null
        isRecording = false
        Log.d(TAG, "Recording stopped. File saved at: $outputFile")
    }

    /**
     * Generates a file path for the new recording. The file is named with a timestamp
     * and stored in the "Recordings" directory.
     * @return The absolute path for the output file.
     */
    private fun getOutputFilePath(): String {
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Recordings")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(dir, "REC_$timeStamp.3gp").absolutePath
    }

    override fun onDestroy() {
        if (isRecording) {
            stopRecording()
        }
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MicRecorderService"
    }
}

