package com.anti.rootadbcontroller.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.anti.rootadbcontroller.R

/**
 * A service that creates a system overlay window, which is drawn on top of other applications.
 * This is used to demonstrate overlay attacks. The service requires the "draw over other apps"
 * permission to function correctly.
 */
class OverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as? WindowManager ?: return
        overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

        val layoutParamsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutParamsType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
            x = 0
            y = 0
        }

        overlayView?.let { view ->
            windowManager.addView(view, params)
            
            val closeButton = view.findViewById<Button>(R.id.overlay_button)
            closeButton.setOnClickListener { stopSelf() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { view ->
            windowManager.removeView(view)
        }
    }
}
