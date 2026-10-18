package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Usb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AmberHidden
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldSafe
import com.example.usb.*

@Composable
fun DiagnosticsScreen(
    portState: UsbPortDetectionState,
    deviceInfo: SanDiskDeviceInfo?,
    flashStatus: FlashStatus,
    logs: List<DiagnosticLog>,
    onRunDiagnostics: () -> Unit,
    onStartRepair: () -> Unit,
    onSimulateAttachment: () -> Unit,
    onNavigateToWizard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Auto-scroll logs
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_usb_repair),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xCC0F172A),
                                        Color(0xF50F172A)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "SanDisk Ultra Dual 128GB",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "أداة إصلاح مشكلة No Media واستعادة السعة",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "استشعار مباشر لمنفذ USB-C حتى لو تعذر على الهاتف أو الكمبيوتر قراءته",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        )
                    }
                }
            }
        }

        // Hardware Port Sensor Live Status
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hardware_port_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (portState.isPhysicalConnectionDetected) EmeraldSafe.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Usb,
                                    contentDescription = null,
                                    tint = if (portState.isPhysicalConnectionDetected) EmeraldSafe else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "مستشعر منفذ USB-C (Hardware Sensor)",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (portState.isPhysicalConnectionDetected)
                                        "تم رصد الاتصال الفيزيائي بالمنفذ ✅"
                                    else "بانتظار وضع الفلاش في منفذ USB-C…",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (portState.isPhysicalConnectionDetected) EmeraldSafe else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Badge
                        Surface(
                            color = when (flashStatus) {
                                FlashStatus.READY_HEALTHY -> EmeraldSafe.copy(alpha = 0.2f)
                                FlashStatus.NO_MEDIA -> CoralDanger.copy(alpha = 0.2f)
                                FlashStatus.REPAIRING -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = when (flashStatus) {
                                    FlashStatus.READY_HEALTHY -> "سليم وجاهز (128GB)"
                                    FlashStatus.NO_MEDIA -> "عطل No Media"
                                    FlashStatus.REPAIRING -> "جاري المعالجة…"
                                    else -> "غير متصل"
                                },
                                color = when (flashStatus) {
                                    FlashStatus.READY_HEALTHY -> EmeraldSafe
                                    FlashStatus.NO_MEDIA -> CoralDanger
                                    FlashStatus.REPAIRING -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Key feature explanation
                    Surface(
                        color = AmberHidden.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = AmberHidden,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ميزة الاستشعار الفوري: يتصل التطبيق مباشرة مع شريحة الناقل فيزيائياً لمخاطبة الفلاش فور إدخاله حتى وإن ظهر في الكمبيوتر والهاتف كـ (No Media) أو تعذر فتحه.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (deviceInfo != null) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        // Device Details Table
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            DeviceDataRow("الموديل", deviceInfo.productName)
                            DeviceDataRow("الشركة المصنعة", deviceInfo.manufacturer)
                            DeviceDataRow("معرف الجهاز (VID/PID)", "${deviceInfo.vidHex} / ${deviceInfo.pidHex} (SanDisk)")
                            DeviceDataRow("السعة الأصلية", deviceInfo.formattedCapacity)
                            DeviceDataRow("متحكم الفلاش المتوقع", deviceInfo.controllerChip)
                            DeviceDataRow("الرقم التسلسلي", deviceInfo.serialNumber)
                        }
                    }
                }
            }
        }

        // Quick Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRunDiagnostics,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("run_diagnostics_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("فحص SCSI عميق")
                }

                Button(
                    onClick = onNavigateToWizard,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("open_wizard_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("معالج الإصلاح")
                }
            }
        }

        // Simulation Button (Allows instant test in emulator or without OTG cable)
        item {
            OutlinedButton(
                onClick = onSimulateAttachment,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("simulate_connection_btn"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("محاكاة استشعار فلاش SanDisk Ultra 128GB في المنفذ فوراً")
            }
        }

        // Diagnostic Terminal / Log Output
        item {
            Text(
                text = "سجل أوامر بروتوكول الـ USB والـ SCSI المباشر:",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF030712)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                if (logs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "سجل التشخيص جاهز. قم بتوصيل الفلاش بالمنفذ أو اضغط 'فحص SCSI' لبدء التتبع.",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(logs) { log ->
                            val textColor = when (log.level) {
                                LogLevel.SUCCESS -> Color(0xFF34D399)
                                LogLevel.ERROR -> Color(0xFFF87171)
                                LogLevel.WARN -> Color(0xFFFBBF24)
                                LogLevel.INFO -> Color(0xFF93C5FD)
                            }
                            Text(
                                text = log.message,
                                color = textColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceDataRow(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
