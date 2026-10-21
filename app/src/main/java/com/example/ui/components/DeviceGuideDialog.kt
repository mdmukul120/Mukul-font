package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.model.DeviceOem
import com.example.model.FontModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TertiaryAmber

@Composable
fun DeviceGuideDialog(
    font: FontModel,
    selectedOem: DeviceOem,
    isBangla: Boolean,
    onDismiss: () -> Unit,
    onSelectOem: (DeviceOem) -> Unit,
    onExportPackage: (DeviceOem, FontModel) -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isBangla) "ফোনে ফন্ট ইন্সটলার ও গাইড" else "Phone Font Changer Guide",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${if (isBangla) "নির্বাচিত ফন্ট:" else "Target font:"} ${font.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryCyan
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Brand Selector Pills
                Text(
                    text = if (isBangla) "আপনার ফোনের ব্র্যান্ড নির্বাচন করুন:" else "Select Your Phone Brand:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val oems = listOf(DeviceOem.XIAOMI, DeviceOem.SAMSUNG, DeviceOem.OPPO_REALME_ONEPLUS)
                    oems.forEach { oem ->
                        OemSelectorChip(
                            title = when(oem) {
                                DeviceOem.XIAOMI -> "Xiaomi/Redmi"
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
                    val oems2 = listOf(DeviceOem.VIVO, DeviceOem.PIXEL_AOSP, DeviceOem.MAGISK_ROOT)
                    oems2.forEach { oem ->
                        OemSelectorChip(
                            title = when(oem) {
                                DeviceOem.VIVO -> "Vivo/iQOO"
                                DeviceOem.PIXEL_AOSP -> "Pixel/AOSP"
                                DeviceOem.MAGISK_ROOT -> "Root (All)"
                                else -> oem.name
                            },
                            isSelected = selectedOem == oem,
                            onClick = { onSelectOem(oem) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Guide Content Card based on Selected OEM
                when (selectedOem) {
                    DeviceOem.XIAOMI -> {
                        XiaomiGuideContent(isBangla = isBangla)
                    }
                    DeviceOem.SAMSUNG -> {
                        SamsungGuideContent(isBangla = isBangla)
                    }
                    DeviceOem.OPPO_REALME_ONEPLUS -> {
                        OppoRealmeGuideContent(isBangla = isBangla)
                    }
                    DeviceOem.VIVO -> {
                        VivoGuideContent(isBangla = isBangla)
                    }
                    DeviceOem.PIXEL_AOSP -> {
                        PixelGuideContent(isBangla = isBangla)
                    }
                    DeviceOem.MAGISK_ROOT, DeviceOem.AUTO_DETECT -> {
                        MagiskGuideContent(isBangla = isBangla)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Primary Action Button (Export Package)
                Button(
                    onClick = { onExportPackage(selectedOem, font) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("export_font_package_btn")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (selectedOem) {
                            DeviceOem.XIAOMI -> if (isBangla) "Xiaomi MTZ থিম ফন্ট এক্সপোর্ট করুন" else "Export Xiaomi MTZ Theme"
                            DeviceOem.MAGISK_ROOT -> if (isBangla) "Magisk রুট মডিউল ZIP তৈরি করুন" else "Generate Magisk Root Module ZIP"
                            DeviceOem.VIVO -> if (isBangla) "Vivo ITZ ফাইল এক্সপোর্ট করুন" else "Export Vivo ITZ File"
                            else -> if (isBangla) "TTF ফন্ট প্যাকেজ এক্সপোর্ট ও শেয়ার করুন" else "Export & Share TTF Font"
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Action: Open Display Settings
                OutlinedButton(
                    onClick = {
                        openSystemFontSettings(context)
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryCyan),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("open_system_font_settings_btn")
                ) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "ফোনের ডিসপ্লে / ফন্ট সেটিংস খুলুন" else "Open Phone Font Settings",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OemSelectorChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) PrimaryIndigo else DarkSurfaceVariant)
            .border(
                1.dp,
                if (isSelected) SecondaryCyan else DarkCardBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else Color(0xFFCBD5E1),
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
fun StepItem(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(PrimaryIndigo),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$number", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFE2E8F0),
            lineHeight = 18.sp
        )
    }
}

@Composable
fun XiaomiGuideContent(isBangla: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162035)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Xiaomi HyperOS & MIUI মেথড",
                color = SecondaryCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            StepItem(1, if (isBangla) "নিচের বাটনে চাপ দিয়ে MTZ থিম ফন্ট এক্সপোর্ট করুন।" else "Tap Export Xiaomi MTZ Theme button below.")
            StepItem(2, if (isBangla) "ফোনের অফিসিয়াল Themes অ্যাপ খুলুন।" else "Open the stock Themes app on your Xiaomi/POCO device.")
            StepItem(3, if (isBangla) "Profile (আমার প্রোফাইল) > Fonts (ফন্ট) এ যান।" else "Go to Profile tab > Fonts.")
            StepItem(4, if (isBangla) "নিচে 'Import' (ইমপোর্ট) অপশনে ক্লিক করে ডাউনলোড ফোল্ডারের .mtz ফাইলটি সিলেক্ট করুন।" else "Tap 'Import' at the bottom and choose the exported .mtz file.")
            StepItem(5, if (isBangla) "'Apply' চাপুন এবং ফোন রিস্টার্ট দিন।" else "Tap Apply and reboot if required.")
        }
    }
}

@Composable
fun SamsungGuideContent(isBangla: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162035)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Samsung OneUI (OneUI 4/5/6/7) মেথড",
                color = SecondaryCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            StepItem(1, if (isBangla) "নিচের বাটন দিয়ে TTF ফন্ট এক্সপোর্ট করুন।" else "Export TTF font file using the button below.")
            StepItem(2, if (isBangla) "স্যামসাং এর জন্য 'Samsung Theme Park' (Good Lock মডিউল) অথবা #mono_ v2 অ্যাপ ব্যবহার করুন।" else "Use Samsung Theme Park (Good Lock) or #mono_ v2 font installer.")
            StepItem(3, if (isBangla) "Theme Park অ্যাপে গিয়ে Text সেকশনে কাস্টম TTF ফন্ট অ্যাড করে অ্যাপ্লাই করুন।" else "In Theme Park, select Custom Font > pick this TTF and build theme.")
            StepItem(4, if (isBangla) "অথবা ফোনের Settings > Display > Font size and style এ গিয়ে সক্রিয় করুন।" else "Or activate via Settings > Display > Font size and style.")
        }
    }
}

@Composable
fun OppoRealmeGuideContent(isBangla: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162035)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "OnePlus / OPPO / Realme (ColorOS / OxygenOS)",
                color = SecondaryCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            StepItem(1, if (isBangla) "ফোনের Settings > Language & Region এ গিয়ে Region 'Myanmar' অথবা 'India' করুন।" else "Change Region to 'Myanmar' or 'India' in Settings > Language & Region.")
            StepItem(2, if (isBangla) "Settings > Display & Brightness এ 'Support Dai Characters' অন করুন।" else "Turn on 'Support Dai Characters' in Settings > Display & Brightness.")
            StepItem(3, if (isBangla) "TTF ফাইলটি এক্সপোর্ট করে কাস্টম ফন্ট ইনস্টলার দিয়ে সেট করুন।" else "Export TTF font and set via Theme Store or Font Tool.")
        }
    }
}

@Composable
fun VivoGuideContent(isBangla: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162035)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Vivo & iQOO (FuntouchOS / OriginOS)",
                color = SecondaryCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            StepItem(1, if (isBangla) "নিচের বাটন দিয়ে Vivo ITZ ফন্ট প্যাকেজ জেনারেট করুন।" else "Generate Vivo ITZ font package using button below.")
            StepItem(2, if (isBangla) "ফাইল ম্যানেজার দিয়ে ITZ ফাইলটি থিম ফোল্ডারে পেস্ট করুন।" else "Move the ITZ file into theme directory.")
            StepItem(3, if (isBangla) "i Theme অ্যাপে গিয়ে Local Font এ ঢুকে সিলেক্ট করুন।" else "Open i Theme app > Local > Local Fonts and apply.")
        }
    }
}

@Composable
fun PixelGuideContent(isBangla: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF162035)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Google Pixel / Motorola / AOSP",
                color = SecondaryCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            StepItem(1, if (isBangla) "স্টক অ্যান্ড্রয়েডে সরাসরি TTF ফন্ট এক্সপোর্ট করে Shizuku বা Wireless Debugging দিয়ে সিস্টেম ফন্ট ওভারলে করা যায়।" else "On Stock Android/Pixel, use Shizuku & Wireless ADB font overlays.")
            StepItem(2, if (isBangla) "অথবা FontMorph অ্যাপে 'অ্যাপে ব্যবহার' ক্লিক করে সরাসরি এই অ্যাপেই ফন্ট উপভোগ করুন।" else "Or tap 'Set in App' to immediately enjoy the custom font inside FontMorph.")
        }
    }
}

@Composable
fun MagiskGuideContent(isBangla: Boolean) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A33)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TertiaryAmber.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Magisk / KernelSU রুট মেথড (১০০% ইউনিভার্সাল)",
                    color = TertiaryAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            StepItem(1, if (isBangla) "নিচের বাটনে চাপ দিয়ে একটি সম্পূর্ণ ফ্ল্যাশেবল ZIP মডিউল তৈরি করুন।" else "Tap the button below to generate a ready-to-flash Magisk ZIP module.")
            StepItem(2, if (isBangla) "Magisk অথবা KernelSU অথবা APatch অ্যাপ খুলুন।" else "Open Magisk, KernelSU, or APatch manager app.")
            StepItem(3, if (isBangla) "'Modules' ট্যাবে যান এবং 'Install from storage' দিয়ে ZIP ফাইলটি সিলেক্ট করুন।" else "Go to Modules tab > Install from storage > select the generated ZIP.")
            StepItem(4, if (isBangla) "ফোন রিবুট করুন। যেকোনো অ্যান্ড্রয়েড ফোনে সম্পূর্ণ সিস্টেমের বাংলা ও ইংরেজি ফন্ট পরিবর্তন হয়ে যাবে!" else "Reboot phone. Entire system typography including Bengali conjuncts will be replaced permanently!")
        }
    }
}

fun openSystemFontSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
