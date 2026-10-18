package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberHidden

@Composable
fun FirmwareGuideScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "دليل الفيرموير والـ MPTool المتقدم",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "في حال تعذر الإصلاح عبر الهاتف وكان الفلاش يعاني من تلف عميق في السوفت وير الداخلي للمتحكم (Firmware Corruption)، اتبع هذه الخطوات الفنية المتخصصة لحرقه وإحيائه من جديد.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Step 1: Chip Identification
        FirmwareStepItem(
            stepNumber = "1",
            title = "معرفة نوع شريحة المتحكم (Controller Chip)",
            content = "قم بتشغيل أداة ChipGenius على الكمبيوتر لمعرفة متحكم الفلاش بدقة. فلاشات SanDisk Ultra Dual تستخدم عادة:\n" +
                    "• المتحكم: SanDisk 82-00508 أو Silicon Motion (SMI SM3267 / SM3269)\n" +
                    "• معرّف الشركة: VID = 0781 (SanDisk)\n" +
                    "• معرّف المنتج: PID = 5591 أو 5583"
        )

        // Step 2: MPTool Software
        FirmwareStepItem(
            stepNumber = "2",
            title = "برامج شحن السوفت وير الأصلية (Mass Production Tools)",
            content = "• إذا كان المتحكم Silicon Motion (SMI): استخدم أداة SMI MPTool (إصدار SM3267ABS).\n" +
                    "• إذا كان المتحكم Phison: استخدم أداة Phison MPALL (v3.72 أو v5.03).\n" +
                    "• إذا كان المتحكم SanDisk الأصلي: استخدم أداة SanDisk RescuePRO أو Western Digital Dashboard (الشركة الأم لـ SanDisk)."
        )

        // Step 3: Hardware Test-Point (القفلة)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AmberHidden.copy(alpha = 0.10f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, AmberHidden.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AmberHidden,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "طريقة الـ Test Point (القفلة) لإحياء الفلاش الميت",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = AmberHidden
                    )
                }

                Text(
                    text = "إذا لم يتعرف الكمبيوتر على الفلاش نهائياً وظل مصباح الـ LED مطفأ:\n" +
                            "1. افتح الغطاء الخارجي للفلاش بحذر للوصول إلى شريحة الذاكرة NAND.\n" +
                            "2. باستخدام ملقط أو إبرة، قم بتوصيل الرجل 29 مع 30 (أو خطوط البيانات D0 و D1) معاً.\n" +
                            "3. ضع الفلاش في منفذ USB مع الاستمرار في لمس الرجلين لمدة 3 ثوانٍ ثم افصل الملقط.\n" +
                            "4. سيُجبر المتحكم على الدخول في وضع الـ BootROM Mode، وسيتعرف عليه الكمبيوتر فوراً لوضع السوفت وير الجديد!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        }

        // Step 4: Terminal Script for PC
        Text(
            text = "أوامر الإصلاح السريع عبر موجه الأوامر (CMD / Terminal):",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF030712), RoundedCornerShape(10.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                .padding(12.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            Text(
                text = """
# في نظام Windows (موجه الأوامر كمسؤول):
diskpart
list disk
select disk X (اختر رقم الفلاش 128GB)
clean
create partition primary
format fs=exfat quick
assign
exit
                """.trimIndent(),
                color = Color(0xFF34D399),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun FirmwareStepItem(
    stepNumber: String,
    title: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}
