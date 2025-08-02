package com.anti.rootadbcontroller.services

import android.view.LayoutInflater
class OverlayService : Service() {
        val layoutParamsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            val closeButton = view.findViewById<Button>(R.id.overlay_button)
}
import android.view.Gravity
 */

            layoutParamsType,

    }
import android.os.IBinder
 * permission to function correctly.
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)
            WindowManager.LayoutParams.WRAP_CONTENT,
            windowManager.addView(view, params)
        }
import android.os.Build
 * This is used to demonstrate overlay attacks. The service requires the "draw over other apps"
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            WindowManager.LayoutParams.WRAP_CONTENT,
        overlayView?.let { view ->
            windowManager.removeView(view)
import android.graphics.PixelFormat
 * A service that creates a system overlay window, which is drawn on top of other applications.
        super.onCreate()
        val params = WindowManager.LayoutParams(

        overlayView?.let { view ->
import android.content.Intent
/**
    override fun onCreate() {

        }
        super.onDestroy()
import android.app.Service


        }
            y = 0
    override fun onDestroy() {
import com.anti.rootadbcontroller.R
    override fun onBind(intent: Intent?): IBinder? = null
            WindowManager.LayoutParams.TYPE_PHONE
            x = 0

import android.widget.Button

            @Suppress("DEPRECATION")
            gravity = Gravity.CENTER
    }
import android.view.WindowManager
    private var overlayView: View? = null
        } else {
        ).apply {
        }
import android.view.View
    private lateinit var windowManager: WindowManager
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            PixelFormat.TRANSLUCENT
            closeButton.setOnClickListener { stopSelf() }
