# ADB Function Guide

This guide provides detailed documentation for all Android Debug Bridge (ADB) functions available in the application. All functions operate within Android's security framework and require appropriate permissions.

## 🔧 Core ADB Functions

### Device Management Functions

#### `getDeviceInfo()`
**Purpose**: Retrieves comprehensive device information using standard ADB commands
**ADB Command**: `adb shell getprop`
**Returns**: Device properties including model, Android version, build info
**Permissions**: None required
**Usage Example**:
```kotlin
val deviceInfo = AdbUtils.getDeviceInfo()
Log.d("Device", "Model: ${deviceInfo.model}")
```

#### `isDeviceConnected()`
**Purpose**: Checks if device is accessible via ADB
**ADB Command**: `adb devices`
**Returns**: Boolean indicating connection status
**Permissions**: USB Debugging must be enabled
**Usage Example**:
```kotlin
if (AdbUtils.isDeviceConnected()) {
    // Proceed with ADB operations
}
```

#### `rebootDevice(mode: String)`
**Purpose**: Restarts device using ADB reboot commands
**ADB Command**: `adb reboot [mode]`
**Parameters**: mode - "system", "recovery", "bootloader"
**Permissions**: Requires system-level permissions
**Usage Example**:
```kotlin
AdbUtils.rebootDevice("system") // Normal reboot
```

### Package Management Functions

#### `installPackage(apkPath: String)`
**Purpose**: Installs APK packages using ADB install command
**ADB Command**: `adb install [options] <path>`
**Parameters**: apkPath - Full path to APK file
**Returns**: Installation result status
**Permissions**: Install unknown apps permission
**Usage Example**:
```kotlin
val result = AdbUtils.installPackage("/sdcard/app.apk")
if (result.success) Log.d("Install", "Package installed successfully")
```

#### `uninstallPackage(packageName: String)`
**Purpose**: Removes installed applications
**ADB Command**: `adb uninstall <package>`
**Parameters**: packageName - Package identifier
**Returns**: Uninstallation result
**Permissions**: Package management permissions
**Usage Example**:
```kotlin
AdbUtils.uninstallPackage("com.example.app")
```

#### `listInstalledPackages()`
**Purpose**: Retrieves list of installed applications
**ADB Command**: `adb shell pm list packages`
**Returns**: List of package names and details
**Permissions**: None required
**Usage Example**:
```kotlin
val packages = AdbUtils.listInstalledPackages()
packages.forEach { Log.d("Package", it.name) }
```

#### `getPackageInfo(packageName: String)`
**Purpose**: Gets detailed information about specific package
**ADB Command**: `adb shell pm dump <package>`
**Parameters**: packageName - Target package identifier
**Returns**: Comprehensive package information
**Permissions**: None required
**Usage Example**:
```kotlin
val info = AdbUtils.getPackageInfo("com.android.settings")
Log.d("Package", "Version: ${info.versionName}")
```

### File System Functions

#### `pushFile(localPath: String, remotePath: String)`
**Purpose**: Uploads files to device using ADB push
**ADB Command**: `adb push <local> <remote>`
**Parameters**: localPath, remotePath - Source and destination paths
**Returns**: Transfer result status
**Permissions**: Storage permissions
**Usage Example**:
```kotlin
val result = AdbUtils.pushFile("/local/file.txt", "/sdcard/file.txt")
```

#### `pullFile(remotePath: String, localPath: String)`
**Purpose**: Downloads files from device
**ADB Command**: `adb pull <remote> <local>`
**Parameters**: remotePath, localPath - Source and destination paths
**Returns**: Transfer result status
**Permissions**: Storage permissions
**Usage Example**:
```kotlin
AdbUtils.pullFile("/sdcard/file.txt", "/local/file.txt")
```

#### `listFiles(directoryPath: String)`
**Purpose**: Lists directory contents
**ADB Command**: `adb shell ls <path>`
**Parameters**: directoryPath - Target directory
**Returns**: File and directory listing
**Permissions**: Read permissions for target directory
**Usage Example**:
```kotlin
val files = AdbUtils.listFiles("/sdcard/")
files.forEach { Log.d("File", "${it.name} - ${it.size}") }
```

#### `readFileContent(filePath: String)`
**Purpose**: Reads text file contents
**ADB Command**: `adb shell cat <path>`
**Parameters**: filePath - Target file path
**Returns**: File content as string
**Permissions**: Read permissions for file
**Usage Example**:
```kotlin
val content = AdbUtils.readFileContent("/sdcard/config.txt")
```

### System Control Functions

#### `executeShellCommand(command: String)`
**Purpose**: Executes arbitrary shell commands via ADB
**ADB Command**: `adb shell <command>`
**Parameters**: command - Shell command to execute
**Returns**: Command output and exit status
**Permissions**: Varies by command
**Usage Example**:
```kotlin
val result = AdbUtils.executeShellCommand("getprop ro.build.version.release")
Log.d("Android Version", result.output)
```

#### `simulateInput(action: String, params: Map<String, Any>)`
**Purpose**: Simulates user input events
**ADB Command**: `adb shell input <type> <parameters>`
**Parameters**: action - "tap", "swipe", "text", "keyevent"
**Returns**: Input simulation result
**Permissions**: Accessibility service permissions
**Usage Example**:
```kotlin
// Simulate screen tap
AdbUtils.simulateInput("tap", mapOf("x" to 500, "y" to 800))

// Simulate text input
AdbUtils.simulateInput("text", mapOf("text" to "Hello World"))
```

#### `captureScreenshot(outputPath: String)`
**Purpose**: Takes device screenshots
**ADB Command**: `adb shell screencap <path>`
**Parameters**: outputPath - Where to save screenshot
**Returns**: Capture result and file path
**Permissions**: Storage and display capture permissions
**Usage Example**:
```kotlin
val result = AdbUtils.captureScreenshot("/sdcard/screenshot.png")
```

#### `recordScreen(outputPath: String, duration: Int)`
**Purpose**: Records screen activity
**ADB Command**: `adb shell screenrecord <options> <path>`
**Parameters**: outputPath, duration - Output file and recording length
**Returns**: Recording result
**Permissions**: Storage and screen recording permissions
**Usage Example**:
```kotlin
AdbUtils.recordScreen("/sdcard/recording.mp4", 30) // 30 seconds
```

### Network Functions

#### `getNetworkInfo()`
**Purpose**: Retrieves network configuration details
**ADB Command**: `adb shell ip route`, `adb shell netstat`
**Returns**: Network interfaces, routes, and connections
**Permissions**: None required
**Usage Example**:
```kotlin
val networkInfo = AdbUtils.getNetworkInfo()
Log.d("Network", "WiFi IP: ${networkInfo.wifiIp}")
```

#### `enableWirelessDebugging()`
**Purpose**: Activates wireless ADB debugging
**ADB Command**: `adb tcpip 5555`
**Returns**: Wireless debugging status
**Permissions**: USB debugging initially required
**Usage Example**:
```kotlin
val result = AdbUtils.enableWirelessDebugging()
if (result.success) Log.d("ADB", "Wireless debugging enabled")
```

### Log Functions

#### `getLogcat(filter: String?, maxLines: Int)`
**Purpose**: Retrieves system logs
**ADB Command**: `adb logcat [options]`
**Parameters**: filter - Log filter criteria, maxLines - Maximum log entries
**Returns**: Filtered log entries
**Permissions**: Read logs permission
**Usage Example**:
```kotlin
val logs = AdbUtils.getLogcat("AndroidRuntime:E", 100)
logs.forEach { Log.d("Logcat", it.message) }
```

#### `clearLogcat()`
**Purpose**: Clears system log buffer
**ADB Command**: `adb logcat -c`
**Returns**: Clear operation result
**Permissions**: System permissions
**Usage Example**:
```kotlin
AdbUtils.clearLogcat()
```

## 🛡️ Security Considerations

### Permission Requirements
All ADB functions require appropriate Android permissions:
- **Storage Operations**: READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE
- **Input Simulation**: Accessibility service permissions
- **Screen Capture**: System-level permissions
- **Package Management**: INSTALL_PACKAGES (system apps)
- **Network Operations**: ACCESS_NETWORK_STATE

### Safe Usage Guidelines
1. **Validate Input**: Always validate parameters before executing ADB commands
2. **Handle Errors**: Implement proper error handling for all operations
3. **Check Permissions**: Verify required permissions before function calls
4. **User Consent**: Obtain explicit user consent for sensitive operations
5. **Audit Trail**: Log all executed commands for transparency

### Error Handling
```kotlin
try {
    val result = AdbUtils.executeShellCommand("command")
    if (result.success) {
        // Handle success
    } else {
        Log.e("ADB", "Command failed: ${result.error}")
    }
} catch (exception: SecurityException) {
    Log.e("ADB", "Permission denied: ${exception.message}")
}
```

## 📝 Function Categories

### Standard Operations (No Special Permissions)
- Device information queries
- Package listing
- File system browsing
- Network status checks

### Privileged Operations (System Permissions)
- Package installation/removal
- System setting modifications
- Input simulation
- Screen recording

### Administrative Operations (Root/Shizuku)
- Device reboot
- System file modifications
- Service management
- Low-level system access

## 🔄 Integration with Shizuku

For enhanced capabilities without root access, functions can utilize the Shizuku framework:

```kotlin
if (ShizukuUtils.isShizukuAvailable()) {
    // Use Shizuku for privileged operations
    val result = ShizukuUtils.executePrivilegedCommand(command)
} else {
    // Fallback to standard ADB
    val result = AdbUtils.executeShellCommand(command)
}
```

## 📋 Best Practices

1. **Always Check Availability**: Verify ADB connectivity before operations
2. **Graceful Degradation**: Provide fallbacks when privileged access unavailable
3. **User Experience**: Show progress indicators for long-running operations
4. **Resource Management**: Properly close resources and streams
5. **Documentation**: Document all custom ADB implementations clearly

This guide covers all standard ADB functionality available through the application. All operations are performed within Android's security framework and require appropriate permissions.
