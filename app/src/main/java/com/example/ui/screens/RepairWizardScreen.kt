package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberHidden
import com.example.ui.theme.CoralDanger
import com.example.ui.theme.EmeraldSafe
import com.example.usb.FlashStatus
import com.example.usb.SanDiskDeviceInfo

@Composable
fun RepairWizardScreen(
    deviceInfo: SanDiskDeviceInfo?,
    flashStatus: FlashStatus,
    progress: Float,
    stepTitle: String,
    onStartRepair: () -> Unit,
    onResetForTest: () -> Unit,
    onSimulateAttachment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "معالج إصلاح فلاش SanDisk Ultra Dual 128GB",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "هذا المعالج يقوم بإعادة برمجة جدول الأقسام وتصفير قطاع الإقلاع التالف لحل مشكلة عدم قراءة الفلاش أو ظهوره كـ No Media في الكمبيوتر والهاتف.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Device Connection & Status Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (flashStatus) {
                    FlashStatus.READY_HEALTHY -> EmeraldSafe.copy(alpha = 0.12f)
                    FlashStatus.NO_MEDIA -> CoralDanger.copy(alpha = 0.12f)
                    FlashStatus.REPAIRING -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                when (flashStatus) {
                    FlashStatus.READY_HEALTHY -> EmeraldSafe.copy(alpha = 0.4f)
                    FlashStatus.NO_MEDIA -> CoralDanger.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (flashStatus) {
                            FlashStatus.READY_HEALTHY -> Icons.Default.CheckCircle
                            FlashStatus.NO_MEDIA -> Icons.Default.Warning
                            FlashStatus.REPAIRING -> Icons.Default.Sync
                            else -> Icons.Default.UsbOff
                        },
                        contentDescription = null,
                        tint = when (flashStatus) {
                            FlashStatus.READY_HEALTHY -> EmeraldSafe
                            FlashStatus.NO_MEDIA -> CoralDanger
                            FlashStatus.REPAIRING -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(28.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = when (flashStatus) {
                                FlashStatus.READY_HEALTHY -> "تم الإصلاح بنجاح! الفلاش جاهز بسعة 128GB"
                                FlashStatus.NO_MEDIA -> "تم اكتشاف عطل No Media (0 بايت)"
                                FlashStatus.REPAIRING -> "جاري معالجة وإصلاح الفلاش…"
                                else -> "الفلاش غير متصل"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = when (flashStatus) {
                                FlashStatus.READY_HEALTHY -> "تم بناء جدول MBR جديد وتهيئة نظام exFAT المتوافق مع الكمبيوتر والهواتف."
                                FlashStatus.NO_MEDIA -> "المتحكم معزول عن شريحة الذاكرة NAND أو جدول الأقسام تالف ويحتاج إعادة كتابة."
                                FlashStatus.REPAIRING -> stepTitle.ifBlank { "جاري تنفيذ الأوامر…" }
                                else -> "قم بتوصيل الفلاش عبر منفذ USB-C للبدء."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Live Progress Indicator when repairing
        AnimatedVisibility(visible = flashStatus == FlashStatus.REPAIRING) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "${(progress * 100).toInt()}% - $stepTitle",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Step by Step Architecture Cards
        Text(
            text = "مراحل الإصلاح البرمجي المباشر:",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        WizardStepCard(
            stepNumber = "1",
            title = "المصافحة وإلغاء وضع الحماية (SCSI Reset)",
            desc = "إرسال أوامر تحفيز المتحكم وإلغاء وضع القراءة فقط وحظر الكتابة من شريحة NAND.",
            isCompleted = progress >= 0.25f || flashStatus == FlashStatus.READY_HEALTHY
        )

        WizardStepCard(
            stepNumber = "2",
            title = "تصفير القطاعات التالفة (Zero-Fill LBA 0-2048)",
            desc = "مسح قطاع الإقلاع التالف بالكامل الذي يسبب تعليق الكمبيوتر على No Media.",
            isCompleted = progress >= 0.50f || flashStatus == FlashStatus.READY_HEALTHY
        )

        WizardStepCard(
            stepNumber = "3",
            title = "إعادة بناء Master Boot Record بسعة 128GB",
            desc = "كتابة جدول أقسام جديد وتحديد سعة 244,140,625 قطاع (128 جيجابايت كاملة).",
            isCompleted = progress >= 0.75f || flashStatus == FlashStatus.READY_HEALTHY
        )

        WizardStepCard(
            stepNumber = "4",
            title = "تهيئة نظام الملفات exFAT والتحقق النهائي",
            desc = "تهيئة الذاكرة بحجم كتلة 128KB مع محاذاة 4K القياسية لضمان أقصى سرعة واستقرار.",
            isCompleted = progress >= 1.0f || flashStatus == FlashStatus.READY_HEALTHY
        )

        Spacer(modifier = Modifier.height(8.dp))

        // CTA: Start Repair
        if (deviceInfo != null) {
            Button(
                onClick = onStartRepair,
                enabled = flashStatus != FlashStatus.REPAIRING,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_wizard_repair_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (flashStatus == FlashStatus.READY_HEALTHY) EmeraldSafe
                    else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (flashStatus == FlashStatus.READY_HEALTHY) Icons.Default.Refresh else Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (flashStatus == FlashStatus.READY_HEALTHY) "إعادة إجراء عملية الإصلاح" else "بدء عملية الإصلاح والتهيئة 128GB",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        } else {
            Button(
                onClick = onSimulateAttachment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("wizard_connect_btn"),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("محاكاة استشعار الفلاش في المنفذ لبدء المعالج")
            }
        }

        if (flashStatus == FlashStatus.READY_HEALTHY) {
            OutlinedButton(
                onClick = onResetForTest,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("إعادة تعيين الحالة إلى No Media (لأغراض الاختبار والعرض)")
            }
        }
    }
}

@Composable
private fun WizardStepCard(
    stepNumber: String,
    title: String,
    desc: String,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) EmeraldSafe.copy(alpha = 0.08f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isCompleted) androidx.compose.foundation.BorderStroke(1.dp, EmeraldSafe.copy(alpha = 0.3f))
        else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        if (isCompleted) EmeraldSafe else MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(17.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = stepNumber,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
