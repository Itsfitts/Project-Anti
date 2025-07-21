package com.anti.rootadbcontroller.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anti.rootadbcontroller.R
import com.anti.rootadbcontroller.models.FeatureItem
import com.anti.rootadbcontroller.ui.theme.RootADBControllerTheme
import com.anti.rootadbcontroller.utils.AutomationUtils
import com.anti.rootadbcontroller.utils.Constants

@Composable
fun MainScreen(
    isRootAvailable: Boolean,
    isShizukuAvailable: Boolean,
    onFeatureClick: (Int) -> Unit,
    onShizukuPermissionRequest: () -> Unit,
    onAutomationSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    var showAutomationDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Root ADB Controller") },
                backgroundColor = MaterialTheme.colors.surface,
                actions = {
                    RootStatusIndicator(isRootAvailable, isShizukuAvailable, onShizukuPermissionRequest)

                    // Add automation settings button
                    IconButton(onClick = onAutomationSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Automation Settings"
                        )
                    }
                }
            )
        }
    ) { padding ->
        FeatureList(
            features = getFeatureList(context),
            onFeatureClick = onFeatureClick,
            modifier = Modifier.padding(padding)
        )

        // Automation Settings Dialog
        if (showAutomationDialog) {
            AutomationSettingsDialog(
                context = context,
                onDismissRequest = { showAutomationDialog = false }
            )
        }
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
                .clickable(onClick = onShizukuPermissionRequest)
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
    var showExplanationDialog by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        backgroundColor = if (isHovered) MaterialTheme.colors.surface.copy(alpha = 0.9f) else MaterialTheme.colors.surface,
        elevation = if (isHovered) 8.dp else 4.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (isHovered) MaterialTheme.colors.primary.copy(alpha = 0.5f) else Color.Transparent
        )
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // In a real app, you'd use feature.iconResId here
                // Icon(painterResource(id = feature.iconResId), contentDescription = null)
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = feature.title, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = feature.description, style = MaterialTheme.typography.body2, color = Color.Gray)

                    // Tap hint text
                    Text(
                        text = "Tap to activate",
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.primary.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Info button with tooltip hint
                Box {
                    IconButton(
                        onClick = { showExplanationDialog = true },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "More information",
                            tint = MaterialTheme.colors.primary
                        )
                    }

                    // Pulsating effect for the info button
                    if (!showExplanationDialog) {
                        val infiniteTransition = rememberInfiniteTransition()
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000),
                                repeatMode = RepeatMode.Reverse
                            )
                        )

                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.Center)
                                .scale(scale)
                                .background(
                                    color = MaterialTheme.colors.primary.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }

    // Explanation dialog
    if (showExplanationDialog) {
        FeatureExplanationDialog(
            feature = feature,
            onDismiss = { showExplanationDialog = false }
        )
    }
}

@Composable
fun FeatureExplanationDialog(feature: FeatureItem, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = feature.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6
                )
                Divider(
                    color = MaterialTheme.colors.primary.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        text = {
            // Use LazyColumn for scrollable content
            LazyColumn(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                item {
                    Text(
                        text = feature.detailedExplanation,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
            ) {
                Text("Close", color = Color.White)
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(16.dp)
    )
}

fun getFeatureList(context: Context): List<FeatureItem> {
    return listOf(
        FeatureItem(Constants.FEATURE_KEYLOGGING, context.getString(R.string.keylogging), "Monitors and logs all text input.", 0, context.getString(R.string.keylogging_explanation)),
        FeatureItem(Constants.FEATURE_DATA_EXFILTRATION, context.getString(R.string.data_exfiltration), "Extracts sensitive data like contacts, messages, and photos.", 0, context.getString(R.string.data_exfiltration_explanation)),
        FeatureItem(Constants.FEATURE_SILENT_INSTALL, context.getString(R.string.silent_install), "Installs APKs in the background without user interaction.", 0, context.getString(R.string.silent_install_explanation)),
        FeatureItem(Constants.FEATURE_NETWORK_MONITOR, context.getString(R.string.network_monitor), "Displays all active network connections on the device.", 0, context.getString(R.string.network_monitor_explanation)),
        FeatureItem(Constants.FEATURE_FILE_ACCESS, context.getString(R.string.file_access), "Browse and view files in protected system directories.", 0, context.getString(R.string.file_access_explanation)),
        FeatureItem(Constants.FEATURE_SYSTEM_CONTROL, context.getString(R.string.system_control), "Control system functions like rebooting or toggling hardware.", 0, context.getString(R.string.system_control_explanation)),
        FeatureItem(Constants.FEATURE_SCREENSHOT, context.getString(R.string.screenshot), "Takes a screenshot of the current screen without any visual indication.", 0, context.getString(R.string.screenshot_explanation)),
        FeatureItem(Constants.FEATURE_LOG_ACCESS, context.getString(R.string.log_access), "Accesses and displays system-level logs (logcat).", 0, context.getString(R.string.log_access_explanation)),
        FeatureItem(Constants.FEATURE_MIC_RECORDER, context.getString(R.string.mic_recorder), "Silently records audio from the microphone.", 0, context.getString(R.string.mic_recorder_explanation)),
        FeatureItem(Constants.FEATURE_LOCATION_TRACKER, context.getString(R.string.location_tracker), "Fetches the device's last known GPS location.", 0, context.getString(R.string.location_tracker_explanation)),
        FeatureItem(Constants.FEATURE_OVERLAY_ATTACK, context.getString(R.string.overlay_attack), "Demonstrates a UI overlay by drawing a window over other apps.", 0, context.getString(R.string.overlay_attack_explanation)),
        FeatureItem(Constants.FEATURE_HIDE_APP_ICON, context.getString(R.string.hide_app_icon), "Hides the app icon from the launcher.", 0, context.getString(R.string.hide_app_icon_explanation)),
        FeatureItem(Constants.FEATURE_PERMISSIONS_SCANNER, context.getString(R.string.permissions_scanner), "Scans all installed apps and lists those with potentially dangerous permissions.", 0, context.getString(R.string.permissions_scanner_explanation)),
        FeatureItem(Constants.FEATURE_OVERLAY_DETECTOR, context.getString(R.string.overlay_detector), "Lists all apps that have permission to draw over other applications.", 0, context.getString(R.string.overlay_detector_explanation)),
        FeatureItem(Constants.FEATURE_HIDDEN_APP_FINDER, context.getString(R.string.hidden_app_finder), "Finds installed apps that do not have a launcher icon.", 0, context.getString(R.string.hidden_app_finder_explanation)),
        FeatureItem(Constants.FEATURE_CAMERA_MIC_DETECTOR, context.getString(R.string.camera_mic_detector), "Detects if the camera or microphone is currently being used by any app.", 0, context.getString(R.string.camera_mic_detector_explanation)),
        FeatureItem(Constants.FEATURE_STEALTH_CAMERA, context.getString(R.string.stealth_camera), "Silently takes a picture from the front camera.", 0, context.getString(R.string.stealth_camera_explanation)),
        FeatureItem(Constants.FEATURE_GET_CLIPBOARD, context.getString(R.string.get_clipboard), "Gets the current clipboard content.", 0, context.getString(R.string.get_clipboard_explanation)),
        FeatureItem(Constants.FEATURE_SET_CLIPBOARD, context.getString(R.string.set_clipboard), "Sets the clipboard content.", 0, context.getString(R.string.set_clipboard_explanation)),
        FeatureItem(Constants.FEATURE_SHIZUKU_OPERATIONS, context.getString(R.string.shizuku_operations), "Performs operations using Shizuku.", 0, context.getString(R.string.shizuku_operations_explanation)),
        FeatureItem(Constants.FEATURE_PACKAGE_MANAGER, context.getString(R.string.package_manager), "Manages installed packages using Shizuku.", 0, context.getString(R.string.package_manager_explanation)),
        FeatureItem(Constants.FEATURE_SYSTEM_PROPERTIES, context.getString(R.string.system_properties), "Views system properties and device information.", 0, context.getString(R.string.system_properties_explanation)),
        FeatureItem(Constants.FEATURE_REMOTE_ADB, context.getString(R.string.remote_adb), "Controls ADB over the network.", 0, context.getString(R.string.remote_adb_explanation)),
        FeatureItem(Constants.FEATURE_AUTOMATION_SETTINGS, "Automation Settings", "Configure automatic tasks.", 0, context.getString(R.string.automation_settings_explanation))
    )
}

@Composable
fun AutomationSettingsDialog(
    context: Context,
    onDismissRequest: () -> Unit
) {
    val automationKeys = remember { AutomationUtils.getAllAutomationKeys() }
    val automationStates = remember {
        mutableStateMapOf<String, Boolean>().apply {
            automationKeys.forEach { key ->
                this[key] = AutomationUtils.getAutomation(context, key)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Automation Settings") },
        text = {
            Column {
                Text(
                    text = "Select which features to automatically run when access is gained:",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                automationKeys.forEach { key ->
                    val displayName = AutomationUtils.getKeyDisplayName(key)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = displayName,
                            style = MaterialTheme.typography.body1,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = automationStates[key] ?: false,
                            onCheckedChange = { isChecked ->
                                automationStates[key] = isChecked
                                AutomationUtils.setAutomation(context, key, isChecked)
                            }
                        )
                    }

                    if (key != automationKeys.last()) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text("Close")
            }
        }
    )
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DefaultPreview() {
    RootADBControllerTheme {
        MainScreen(
            isRootAvailable = true,
            isShizukuAvailable = true,
            onFeatureClick = {},
            onShizukuPermissionRequest = {},
            onAutomationSettingsClick = {}
        )
    }
}
