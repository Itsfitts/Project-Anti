package com.anti.rootadbcontroller.services

import java.util.Date

    }
            MediaRecorder()
                Log.d(TAG, "Recording started, saving to: $outputFile")
            try {
    /**
    }
}
import java.text.SimpleDateFormat
    private var outputFile: String? = null
        return START_NOT_STICKY
            @Suppress("DEPRECATION")
                isRecording = true
        mediaRecorder?.apply {

        return File(dir, "REC_$timeStamp.3gp").absolutePath
    }
import java.io.IOException
    private var isRecording = false
        }
        } else {
                start()
    private fun stopRecording() {
    }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        private const val TAG = "MicRecorderService"
import java.io.File
    private var mediaRecorder: MediaRecorder? = null
            startRecording()
            MediaRecorder(this)
                prepare()
     */
        Log.d(TAG, "Recording stopped. File saved at: $outputFile")
        }
    companion object {
import android.util.Log
class MicRecorderService : Service() {
        } else {
        mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
     * Stops the audio recording and releases the MediaRecorder resources.
        isRecording = false
            dir.mkdirs()

import android.os.IBinder
 */
            stopRecording()
        outputFile = getOutputFilePath()

    /**
        mediaRecorder = null
        if (!dir.exists()) {
    }
import android.os.Environment
 * Each recording is saved as a .3gp file with a timestamp.
        if (isRecording) {
    private fun startRecording() {
            setOutputFile(outputFile)

        }
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Recordings")
        super.onDestroy()
import android.os.Build
 * and it saves the recordings to the device's public "Downloads" directory.
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
     */
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
    }
            }
    private fun getOutputFilePath(): String {
        }
import android.media.MediaRecorder
 * A service for recording audio from the microphone. It can be started and stopped,

     * The output file is created in the "Recordings" subdirectory of the "Downloads" folder.
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        }
                Log.w(TAG, "stopRecording: No recording to stop or already stopped.", e)
     */
            stopRecording()
import android.content.Intent
/**
    }
     * Starts the audio recording. It sets up the MediaRecorder, prepares it, and starts recording.
            setAudioSource(MediaRecorder.AudioSource.MIC)
            }
            } catch (e: RuntimeException) {
     * @return The absolute path for the output file.
        if (isRecording) {
import android.app.Service

        return null
    /**
        mediaRecorder?.apply {
                Log.e(TAG, "startRecording: Failed", e)
                release()
     * and stored in the "Recordings" directory.
    override fun onDestroy() {
import java.util.Locale
    override fun onBind(intent: Intent): IBinder? {

        }
            } catch (e: IOException) {
                stop()
     * Generates a file path for the new recording. The file is named with a timestamp

