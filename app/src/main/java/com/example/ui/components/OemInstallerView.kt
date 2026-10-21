package com.example.ui.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DeviceOem
import com.example.model.FontModel
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TertiaryAmber

@Composable
fun OemInstallerView(
    selectedFont: FontModel?,
    selectedOem: DeviceOem,
    detectedOem: DeviceOem,
    isBangla: Boolean,
    onSelectOem: (DeviceOem) -> Unit,
    onExportPackage: (DeviceOem, FontModel) -> Unit,
    onApplyInApp: (FontModel) -> Unit,
    onGoToCatalog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Device Detection Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D30)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(PrimaryIndigo.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                                style = MaterialTheme.typography.bodySmall,
                                color = SecondaryCyan
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(SuccessEmerald.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isBangla) "শনাক্ত হয়েছে" else "Detected",
                            color = SuccessEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Target Selected Font Card
        Text(
            text = if (isBangla) "ফোনে সেট করার জন্য নির্বাচিত ফন্ট:" else "Target Font to Install:",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFFCBD5E1)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedFont != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1523)),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222F49)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) selectedFont.nativeName else selectedFont.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedFont.previewTextBengali,
                            fontFamily = selectedFont.toFontFamily(),
                            fontSize = 15.sp,
                            color = SecondaryCyan,
                            maxLines = 1
                        )
                    }

                    OutlinedButton(
                        onClick = onGoToCatalog,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Text(if (isBangla) "পরিবর্তন" else "Change", fontSize = 11.sp)
                    }
                }
            }
        } else {
            OutlinedButton(
                onClick = onGoToCatalog,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isBangla) "লাইব্রেরি থেকে একটি ফন্ট বাছাই করুন" else "Pick a Font from Catalog")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // OEM Brand Switcher Chips
        Text(
            text = if (isBangla) "ফোনের সিস্টেম পদ্ধতি বেছে নিন:" else "Select Installation Method:",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFFCBD5E1)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(DeviceOem.XIAOMI, DeviceOem.SAMSUNG, DeviceOem.OPPO_REALME_ONEPLUS).forEach { oem ->
                OemSelectorChip(
                    title = when(oem) {
                        DeviceOem.XIAOMI -> "Xiaomi/POCO"
                        DeviceOem.SAMSUNG -> "Samsung"
                        DeviceOem.OPPO_REALME_ONEPLUS -> "OPPO/Realme"
                        else -> oem.name
                    },
                    isSelected = selectedOem == oem,
                    onClick = { onSelectOem(oem) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(DeviceOem.VIVO, DeviceOem.PIXEL_AOSP, DeviceOem.MAGISK_ROOT).forEach { oem ->
                OemSelectorChip(
                    title = when(oem) {
                        DeviceOem.VIVO -> "Vivo/iQOO"
                        DeviceOem.PIXEL_AOSP -> "Pixel/AOSP"
                        DeviceOem.MAGISK_ROOT -> "Magisk (Root)"
                        else -> oem.name
                    },
                    isSelected = selectedOem == oem,
                    onClick = { onSelectOem(oem) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Detailed Steps for Active OEM
        when (selectedOem) {
            DeviceOem.XIAOMI -> XiaomiGuideContent(isBangla = isBangla)
            DeviceOem.SAMSUNG -> SamsungGuideContent(isBangla = isBangla)
            DeviceOem.OPPO_REALME_ONEPLUS -> OppoRealmeGuideContent(isBangla = isBangla)
            DeviceOem.VIVO -> VivoGuideContent(isBangla = isBangla)
            DeviceOem.PIXEL_AOSP -> PixelGuideContent(isBangla = isBangla)
            DeviceOem.MAGISK_ROOT, DeviceOem.AUTO_DETECT -> MagiskGuideContent(isBangla = isBangla)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons
        if (selectedFont != null) {
            Button(
                onClick = { onExportPackage(selectedOem, selectedFont) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("oem_installer_export_btn")
            ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (selectedOem) {
                        DeviceOem.XIAOMI -> if (isBangla) "Xiaomi MTZ প্যাকেজ তৈরি করুন" else "Create Xiaomi MTZ Package"
                        DeviceOem.MAGISK_ROOT -> if (isBangla) "Magisk ফ্ল্যাশেবল ZIP তৈরি করুন" else "Create Magisk Flashable ZIP"
                        DeviceOem.VIVO -> if (isBangla) "Vivo ITZ প্যাকেজ তৈরি করুন" else "Create Vivo ITZ Package"
                        else -> if (isBangla) "TTF ফন্ট প্যাকেজ এক্সপোর্ট করুন" else "Export TTF Font Package"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { onApplyInApp(selectedFont) },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessEmerald),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("oem_installer_apply_in_app_btn")
            ) {
                Icon(imageVector = Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "সরাসরি এই অ্যাপে ফন্ট সক্রিয় করুন (রুট ছাড়া)" else "Apply in Mukul font UI (No Root)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = { openSystemFontSettings(context) },
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryCyan),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryCyan),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("oem_installer_open_settings_btn")
            ) {
                Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBangla) "ফোনের সিস্টেম ডিসপ্লে সেটিংস খুলুন" else "Open Phone Display Settings",
                    fontSize = 12.sp
                )
            }
        }
    }
}
