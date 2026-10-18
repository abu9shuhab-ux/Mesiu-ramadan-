package com.example

import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.UsbRepairViewModel
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.FirmwareGuideScreen
import com.example.ui.screens.LowLevelMbrScreen
import com.example.ui.screens.RepairWizardScreen
import com.example.ui.theme.EmeraldSafe
import com.example.ui.theme.MyApplicationTheme
import com.example.usb.FlashStatus

enum class UsbRepairTab(val title: String) {
    DIAGNOSTICS("التشخيص"),
    WIZARD("معالج الإصلاح"),
    MBR_SECTOR("قطاع MBR"),
    FIRMWARE("دليل السوفت وير")
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                UsbRepairApp()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED == intent.action) {
            val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
            }
            // Handled automatically by UsbPortDetector broadcast listener
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsbRepairApp(viewModel: UsbRepairViewModel = viewModel()) {
    var currentTab by remember { mutableStateOf(UsbRepairTab.DIAGNOSTICS) }

    val portState by viewModel.portDetectionState.collectAsStateWithLifecycle()
    val deviceInfo by viewModel.deviceInfo.collectAsStateWithLifecycle()
    val flashStatus by viewModel.flashStatus.collectAsStateWithLifecycle()
    val progress by viewModel.repairProgress.collectAsStateWithLifecycle()
    val stepTitle by viewModel.repairStepTitle.collectAsStateWithLifecycle()
    val logs by viewModel.diagnosticLogs.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "مصلح فلاش SanDisk",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Real-time hardware status dot
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    if (portState.isPhysicalConnectionDetected) EmeraldSafe else Color(0xFFEF4444),
                                    shape = CircleShape
                                )
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.simulateInstantPortPlug() },
                        modifier = Modifier.testTag("topbar_simulate_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = "محاكاة توصيل الفلاش",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = currentTab == UsbRepairTab.DIAGNOSTICS,
                    onClick = { currentTab = UsbRepairTab.DIAGNOSTICS },
                    icon = {
                        Icon(
                            if (currentTab == UsbRepairTab.DIAGNOSTICS) Icons.Filled.Troubleshoot else Icons.Outlined.Troubleshoot,
                            contentDescription = "التشخيص"
                        )
                    },
                    label = { Text(UsbRepairTab.DIAGNOSTICS.title) },
                    modifier = Modifier.testTag("nav_diagnostics")
                )

                NavigationBarItem(
                    selected = currentTab == UsbRepairTab.WIZARD,
                    onClick = { currentTab = UsbRepairTab.WIZARD },
                    icon = {
                        Icon(
                            if (currentTab == UsbRepairTab.WIZARD) Icons.Filled.AutoFixHigh else Icons.Outlined.AutoFixHigh,
                            contentDescription = "معالج الإصلاح"
                        )
                    },
                    label = { Text(UsbRepairTab.WIZARD.title) },
                    modifier = Modifier.testTag("nav_wizard")
                )

                NavigationBarItem(
                    selected = currentTab == UsbRepairTab.MBR_SECTOR,
                    onClick = { currentTab = UsbRepairTab.MBR_SECTOR },
                    icon = {
                        Icon(
                            if (currentTab == UsbRepairTab.MBR_SECTOR) Icons.Filled.Code else Icons.Outlined.Code,
                            contentDescription = "قطاع MBR"
                        )
                    },
                    label = { Text(UsbRepairTab.MBR_SECTOR.title) },
                    modifier = Modifier.testTag("nav_mbr")
                )

                NavigationBarItem(
                    selected = currentTab == UsbRepairTab.FIRMWARE,
                    onClick = { currentTab = UsbRepairTab.FIRMWARE },
                    icon = {
                        Icon(
                            if (currentTab == UsbRepairTab.FIRMWARE) Icons.Filled.Memory else Icons.Outlined.Memory,
                            contentDescription = "دليل السوفت وير"
                        )
                    },
                    label = { Text(UsbRepairTab.FIRMWARE.title) },
                    modifier = Modifier.testTag("nav_firmware")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                UsbRepairTab.DIAGNOSTICS -> {
                    DiagnosticsScreen(
                        portState = portState,
                        deviceInfo = deviceInfo,
                        flashStatus = flashStatus,
                        logs = logs,
                        onRunDiagnostics = { viewModel.runDiagnostics() },
                        onStartRepair = { viewModel.startRepair() },
                        onSimulateAttachment = { viewModel.simulateInstantPortPlug() },
                        onNavigateToWizard = { currentTab = UsbRepairTab.WIZARD }
                    )
                }

                UsbRepairTab.WIZARD -> {
                    RepairWizardScreen(
                        deviceInfo = deviceInfo,
                        flashStatus = flashStatus,
                        progress = progress,
                        stepTitle = stepTitle,
                        onStartRepair = { viewModel.startRepair() },
                        onResetForTest = { viewModel.resetToNoMediaForTesting() },
                        onSimulateAttachment = { viewModel.simulateInstantPortPlug() }
                    )
                }

                UsbRepairTab.MBR_SECTOR -> {
                    LowLevelMbrScreen(
                        onRebuildMbr = {
                            viewModel.startRepair()
                            currentTab = UsbRepairTab.WIZARD
                        }
                    )
                }

                UsbRepairTab.FIRMWARE -> {
                    FirmwareGuideScreen()
                }
            }
        }
    }
}
