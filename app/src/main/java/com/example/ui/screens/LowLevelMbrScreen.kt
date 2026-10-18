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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSafe

@Composable
fun LowLevelMbrScreen(
    onRebuildMbr: () -> Unit,
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
            text = "فاحص قطاع الإقلاع الرئيسي (MBR Sector 0)",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "عندما يتعامل الكمبيوتر مع الفلاش كـ No Media، يكون قطاع الإقلاع LBA 0 تالفاً أو لا يعيد إشارة 0x55AA، مما يجعل نظام التشغيل يعتقد أن منفذ الفلاش فارغ من وسائط التخزين.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // MBR Structure Inspector Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "محتوى قطاع الإقلاع القياسي لسعة 128GB (LBA 0):",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Hex Dump Viewer
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
0000: 33 C0 8E D0 BC 00 7C 8E C0 8E D8 BE 00 7C BF 00
0010: 06 B9 00 02 FC F3 A4 50 68 1C 06 CB FB B4 04 B6
0020: 00 CD 13 73 05 BE 69 06 EB 31 BF E0 07 B9 04 00
...
01B0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 [80 00
01C0: 01 00 07 FE FF FF 00 08 00 00 B1 25 8F 0E] 00 00
01D0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
01E0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
01F0: 00 00 00 00 00 00 00 00 00 00 00 00 00 00 [55 AA]
                        """.trimIndent(),
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 18.sp
                    )
                }

                // Legend
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(Color(0xFF38BDF8), RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "العنوان [01BE - 01CD]: جدول القسم الأساسي الأول بسعة 128GB (244,140,625 قطاع).",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).background(EmeraldSafe, RoundedCornerShape(2.dp)))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "العنوان [01FE - 01FF]: التوقيع القياسي (0x55AA) الذي بدونه يرفض الكمبيوتر فتح الفلاش.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action Button
        Button(
            onClick = onRebuildMbr,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("apply_clean_mbr_btn"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("إعادة كتابة وتثبيت MBR نظيف 128GB على الفلاش")
        }

        // Technical Notes Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "لماذا تتكرر مشكلة No Media في SanDisk Ultra؟",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "فلاشات SanDisk Ultra Dual مزودة بمنفذي Type-C و Type-A، وكثرة نزع الفلاش المفاجئ أثناء عمليات الكتابة دون عمل 'إخراج آمن' (Safely Remove) يؤدي إلى تلف قطاع LBA 0 وتجمد شريحة الذاكرة NAND في وضع الحماية من الكتابة. تطبيقنا يقوم بإرسال أمر استعادة LBA وتجاوز وضع الحماية برمجياً.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
