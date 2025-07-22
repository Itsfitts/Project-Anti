# Project-Anti: Android Debug Bridge Controller

A comprehensive Android application that provides secure access to Android Debug Bridge (ADB) functionality directly from your device. This tool supports standard ADB operations through multiple privilege modes including Shizuku integration for enhanced capabilities.

## ✨ Features

### Core ADB Functionality
- **Device Information**: Query device properties, build info, and system details
- **Package Management**: Install, uninstall, and manage application packages
- **File Operations**: Transfer files between device and external storage
- **System Commands**: Execute standard ADB shell commands securely
- **Network Operations**: Manage network settings and connectivity
- **Process Management**: Monitor and control running processes

### Privilege Modes
1. **Standard Mode**: Basic functionality using normal Android permissions
2. **ADB Mode**: Full ADB command access via USB debugging or wireless ADB
3. **Shizuku Mode**: Enhanced ADB-like functionality without computer connection using Shizuku framework

### Security Features
- **Permission Management**: Granular control over system permissions
- **Secure Command Execution**: All ADB commands are validated and executed securely
- **Access Control**: Multiple authentication methods for sensitive operations

## 🚀 Getting Started

### Prerequisites
- Android 7.0 (API level 24) or higher
- Developer Options enabled on your device
- For Shizuku mode: Shizuku app installed and configured

### Installation
1. **Download and Install**:
   - Download the APK from the releases page
   - Install on your Android device
   - Grant necessary permissions when prompted

2. **Setup ADB Access**:
   - Enable USB Debugging in Developer Options
   - For wireless debugging: Enable Wireless Debugging (Android 11+)
   - Connect via USB or pair wirelessly

3. **Configure Shizuku (Optional)**:
   - Install Shizuku from Google Play Store
   - Grant Shizuku necessary permissions
   - Start Shizuku service via ADB or root

### Basic Usage
1. Launch the application
2. Select your preferred privilege mode
3. Grant required permissions
4. Access ADB functionality through the intuitive interface

## 🔧 ADB Commands Supported

### Device Management
- `adb devices` - List connected devices
- `adb shell getprop` - Get device properties
- `adb shell settings` - Modify system settings
- `adb reboot` - Restart device (requires permissions)

### Package Operations
- `adb install` - Install APK packages
- `adb uninstall` - Remove applications
- `adb shell pm list packages` - List installed packages
- `adb shell pm dump` - Package information

### File System
- `adb push` - Upload files to device
- `adb pull` - Download files from device
- `adb shell ls` - Directory listings
- `adb shell cat` - File content display

### System Control
- `adb shell input` - Simulate user input
- `adb shell screencap` - Capture screenshots
- `adb shell screenrecord` - Record screen activity
- `adb logcat` - View system logs

## 🛡️ Security & Privacy

This application operates entirely within Android's security framework:
- All operations require appropriate permissions
- No data is transmitted to external servers
- Commands are executed locally on your device
- Full transparency in all operations performed

## 🤝 Contributing

We welcome contributions to improve ADB functionality:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/adb-enhancement`
3. **Commit changes**: `git commit -m "Add ADB feature"`
4. **Push to branch**: `git push origin feature/adb-enhancement`
5. **Submit a Pull Request**

### Development Setup
- Android Studio Arctic Fox or later
- JDK 17
- Android SDK with API level 34
- Gradle 8.2+

## 📱 Compatibility

- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Architecture**: ARM64, ARM, x86_64, x86
- **Permissions**: Varies by feature usage

## 🔄 Automated Features

This project includes automated workflows for:
- Continuous integration and testing
- Code quality checks and formatting
- Dependency updates
- Branch management and cleanup

## 📋 License

This project is licensed under the MIT License - see the LICENSE file for details.

## ⚠️ Disclaimer

This application provides access to Android Debug Bridge functionality for legitimate development and system administration purposes. Users are responsible for complying with all applicable laws and regulations. The developers are not responsible for any misuse of this software.
