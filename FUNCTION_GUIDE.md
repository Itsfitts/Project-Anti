# Project-Anti: Function Guide

This document provides a detailed overview of all the features available in the Project-Anti application, along with instructions on how to use them.

---

## Table of Contents

1.  [Core Concepts](#core-concepts)
    -   [Root Access](#root-access)
    -   [Accessibility Service](#accessibility-service)
2.  [Main Features (UI-Based)](#main-features-ui-based)
    -   [Keylogging](#keylogging)
    -   [Data Exfiltration](#data-exfiltration)
    -   [Silent APK Install](#silent-apk-install)
    -   [Network Monitor](#network-monitor)
    -   [File Access](#file-access)
    -   [System Control](#system-control)
    -   [Screenshot](#screenshot)
    -   [Log Access (Logcat)](#log-access-logcat)
    -   [Microphone Recorder](#microphone-recorder)
    -   [Location Tracker](#location-tracker)
    -   [Overlay Attack](#overlay-attack)
    -   [Hide App Icon](#hide-app-icon)
3.  [Counter-Malware & Detection Features](#counter-malware--detection-features)
    -   [Permissions Scanner](#permissions-scanner)
    -   [Overlay Detector](#overlay-detector)
    -   [Hidden App Finder](#hidden-app-finder)
    -   [Camera & Mic Detector](#camera--mic-detector)
4.  [Background & Automated Functions](#background--automated-functions)
    -   [Kill Switch](#kill-switch)
    -   [Stealth Camera](#stealth-camera)
5.  [Utility Functions (for Developers)](#utility-functions-for-developers)
    -   [RootUtils](#rootutils)
    -   [KeyloggerAccessibilityService Gestures](#keyloggeraccessibilityservice-gestures)

---

## 1. Core Concepts

### Root Access
-   **What it does**: Many features in this app require root (superuser) privileges to access protected system areas, execute privileged commands, and bypass standard Android security restrictions.
-   **How to use**:
    -   Ensure your device is properly rooted.
    -   When the app starts, it will request root access. Grant it via your root management app (e.g., Magisk).
    -   The Floating Action Button (FAB) at the bottom right will turn **green** if root access is available and **red** if it is not.

### Accessibility Service
-   **What it does**: The accessibility service is required for features that need to monitor user interactions, such as keylogging, and for simulating user input like taps and swipes.
-   **How to use**:
    -   Navigate to the "Keylogging" feature in the app.
    -   You will be prompted to open the device's Accessibility settings.
    -   Find "RootADB Controller" in the list of services and enable it.

---

## 2. Main Features (UI-Based)

These features are accessible directly from the main screen of the application.

### Keylogging
-   **What it does**: Monitors and logs all text input, focused fields, and clicked items across the entire system.
-   **How to use**:
    1.  Tap on "Keylogging" in the feature list.
    2.  Enable the Accessibility Service when prompted.
    3.  Once enabled, the service runs in the background, logging data. (Note: In this version, logs are sent to Logcat).

### Data Exfiltration
-   **What it does**: Extracts sensitive user data from the device and saves it to the `/sdcard/Download/` directory. Requires root.
-   **How to use**:
    1.  Tap on "Data Exfiltration."
    2.  A dialog will appear with data types to extract:
        -   **Contacts**: Dumps the contact list to `extracted_contacts.txt`.
        -   **Messages**: Dumps SMS messages to `extracted_messages.txt`.
        -   **Call Logs**: Dumps call history to `extracted_call_logs.txt`.
        -   **Photos**: Copies recent photos from the DCIM folder to the `extracted_photos` directory.
        -   **WhatsApp Data**: Copies WhatsApp database files to the `extracted_whatsapp` directory.
    3.  Select an option to begin the extraction.

### Silent APK Install
-   **What it does**: Finds and installs APK files from the device's storage without requiring any user interaction. Requires root.
-   **How to use**:
    1.  Tap on "Silent Install."
    2.  The app will search the `/sdcard/` directory for `.apk` files.
    3.  A dialog will show a list of found APKs.
    4.  Select an APK from the list to install it silently.

### Network Monitor
-   **What it does**: Displays a list of all active network connections on the device, similar to the `netstat` command. Requires root.
-   **How to use**:
    1.  Tap on "Network Monitor."
    2.  A dialog will appear showing the output of the `netstat -tunap` command, including protocols, addresses, and associated processes.

### File Access
-   **What it does**: Allows you to browse and view file listings in protected system directories. Requires root.
-   **How to use**:
    1.  Tap on "File Access."
    2.  Select a system directory to browse (e.g., `/data`, `/system`).
    3.  A dialog will display the contents of the selected directory.

### System Control
-   **What it does**: Provides control over core system functions. Requires root.
-   **How to use**:
    1.  Tap on "System Control."
    2.  Choose an action from the dialog:
        -   **Reboot**: Restarts the device.
        -   **Power Off**: Shuts the device down.
        -   **Airplane Mode Toggle**: Turns airplane mode on or off.
        -   **WiFi Toggle**: Turns WiFi on or off.
        -   **Bluetooth Toggle**: Turns Bluetooth on or off.

### Screenshot
-   **What it does**: Takes a screenshot of the current screen without any visual indication and saves it to the `/sdcard/Pictures/` directory. Requires root.
-   **How to use**:
    1.  Tap on "Screenshot."
    2.  The screenshot is captured and saved instantly. A toast message will confirm the file path.

### Log Access (Logcat)
-   **What it does**: Retrieves and displays the system logs (logcat). This is useful for debugging and monitoring system behavior. Requires root.
-   **How to use**:
    1.  Tap on "Log Access."
    2.  A dialog will appear containing a snapshot of the system logs.

### Microphone Recorder
-   **What it does**: Silently records audio from the device's microphone.
-   **How to use**:
    1.  Tap on "Mic Recorder" to start recording. The recording is saved to `/sdcard/Download/Recordings/`.
    2.  Tap it again to stop the recording.
    3.  The service runs in the background with no visible indicators.

### Location Tracker
-   **What it does**: Fetches the device's last known GPS location and saves it to `extracted_location.txt` in the Downloads folder.
-   **How to use**:
    1.  Tap on "Location Tracker."
    2.  The app will attempt to get the location and save it. Requires location permissions.

### Overlay Attack
-   **What it does**: Demonstrates a UI overlay attack by drawing a window on top of all other applications.
-   **How to use**:
    1.  Tap on "Overlay Attack."
    2.  Requires the "Draw over other apps" permission, which you may need to grant in settings.
    3.  A simple overlay with a button will appear. Tapping the button closes the overlay.

### Hide App Icon
-   **What it does**: Hides the application's icon from the device's launcher, making it less visible.
-   **How to use**:
    1.  Tap on "Hide App Icon."
    2.  The app icon will be hidden. To re-launch the app, you can use ADB, another app that can launch activities, or clear the app's data via device settings (which will also re-enable the icon).

---

## 3. Counter-Malware & Detection Features

These features are designed to help identify potentially malicious behavior on a device.

### Permissions Scanner
-   **What it does**: Scans all installed applications and lists those that have been granted potentially dangerous permissions (e.g., accessibility, device admin).
-   **How to use**: Tap "Permissions Scanner" to see a list of apps with high-risk permissions. *(Note: This feature is a placeholder).*

### Overlay Detector
-   **What it does**: Lists all applications that have the "Draw over other apps" permission, which can be used for UI-based attacks.
-   **How to use**: Tap "Overlay Detector" to view the list. *(Note: This feature is a placeholder).*

### Hidden App Finder
-   **What it does**: Finds installed applications that do not have a launcher icon. This is a common technique used by malware to remain hidden.
-   **How to use**: Tap "Hidden App Finder" to scan for and display hidden apps. *(Note: This feature is a placeholder).*

### Camera & Mic Detector
-   **What it does**: Detects if the camera or microphone is currently being used by any application.
-   **How to use**: Tap "Camera & Mic Detector" to check the status. *(Note: This feature is a placeholder).*

---

## 4. Background & Automated Functions

### Kill Switch
-   **What it does**: A self-destruct mechanism that automatically triggers after 7 days. It wipes all data collected by the app, stops its services, and disables its main activity.
-   **How to use**: This function is scheduled automatically when the app is first launched. No user interaction is required.

### Stealth Camera
-   **What it does**: A background service that takes a single picture from the front camera without showing a preview.
-   **How to use**: This service must be triggered programmatically via an Intent. It is not currently linked to a UI button. The captured image is saved to `/sdcard/Download/StealthCaptures/`.

---

## 5. Utility Functions (for Developers)

These functions are part of the app's internal API and can be used by developers extending the project.

### RootUtils
-   **Location**: `com.anti.rootadbcontroller.utils.RootUtils`
-   **Functions**:
    -   `isRootAvailable()`: Checks for root access.
    -   `executeRootCommand(String, Callback)`: Executes a single root command.
    -   `executeRootCommands(List<String>, Callback)`: Executes a list of commands.
    -   `silentInstall(String, Callback)`: Installs an APK.
    -   `silentUninstall(String, Callback)`: Uninstalls an app.
    -   `takeScreenshot(String, Callback)`: Captures the screen.
    -   `getSystemLogs(Callback)`: Reads logcat.

### KeyloggerAccessibilityService Gestures
-   **Location**: `com.anti.rootadbcontroller.services.KeyloggerAccessibilityService`
-   **Functions**:
    -   `simulateTap(float x, float y)`: Simulates a tap at given coordinates.
    -   `simulateSwipe(float x1, float y1, float x2, float y2, long duration)`: Simulates a swipe gesture.
-   **How to use**: Obtain an instance of the service and call these methods. Requires the Accessibility Service to be active.

