package com.anti.rootadbcontroller.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A service for recording audio from the microphone. It can be started and stopped,
 * and it saves the recordings to the device's public "Downloads" directory.
 * Each recording is saved as a .3gp file with a timestamp.
 */
public class MicRecorderService extends Service {
    private static final String TAG = "MicRecorderService";
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private String outputFile;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRecording) {
            stopRecording();
        } else {
            startRecording();
        }
        return START_NOT_STICKY;
    }

    /**
     * Starts the audio recording. It sets up the MediaRecorder, prepares it, and starts recording.
     * The output file is created in the "Recordings" subdirectory of the "Downloads" folder.
     */
    private void startRecording() {
        outputFile = getOutputFilePath();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(outputFile);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            Log.d(TAG, "Recording started, saving to: " + outputFile);
        } catch (IOException e) {
            Log.e(TAG, "startRecording: Failed", e);
        }
    }

    /**
     * Stops the audio recording and releases the MediaRecorder resources.
     */
    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (RuntimeException e) {
                Log.w(TAG, "stopRecording: No recording to stop or already stopped.", e);
            } finally {
                mediaRecorder = null;
                isRecording = false;
                Log.d(TAG, "Recording stopped. File saved at: " + outputFile);
            }
        }
    }

    /**
     * Generates a file path for the new recording. The file is named with a timestamp
     * and stored in the "Recordings" directory.
     * @return The absolute path for the output file.
     */
    private String getOutputFilePath() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Recordings");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(dir, "REC_" + timeStamp + ".3gp").getAbsolutePath();
    }

    @Override
    public void onDestroy() {
        if (isRecording) {
            stopRecording();
        }
        super.onDestroy();
    }
}
