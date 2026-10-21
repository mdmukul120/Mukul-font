package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FontCategory
import com.example.model.FontModel
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FontCard(
    font: FontModel,
    isSelected: Boolean,
    isActiveInApp: Boolean,
    isDownloading: Boolean,
    isBangla: Boolean,
    onSelect: () -> Unit,
    onDownload: () -> Unit,
    onApplyInApp: () -> Unit,
    onInstallToPhone: () -> Unit,
    onOpenPlayground: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fontFamily = font.toFontFamily()

    val borderColor = when {
        isActiveInApp -> SuccessEmerald
        isSelected -> PrimaryIndigo
        else -> DarkCardBorder
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(if (isSelected || isActiveInApp) 2.dp else 1.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("font_card_${font.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Font Title & Status badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isBangla) font.nativeName else font.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isActiveInApp) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SuccessEmerald.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active in App",
                                        tint = SuccessEmerald,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isBangla) "অ্যাপে সক্রিয়" else "Active in App",
                                        color = SuccessEmerald,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "${font.author} • ${font.languageSupport}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                // Category Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(PrimaryIndigo.copy(alpha = 0.25f), SecondaryCyan.copy(alpha = 0.25f))
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBangla) font.category.labelBn else font.category.labelEn,
                        color = SecondaryCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Font Preview Box rendered using its OWN FontFamily
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F1523))
                    .border(1.dp, Color(0xFF222F49), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    // Bengali Preview line
                    Text(
                        text = font.previewTextBengali,
                        fontFamily = fontFamily,
                        fontSize = 18.sp,
                        color = Color(0xFFF1F5F9),
                        lineHeight = 26.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // English / Latin Preview line
                    Text(
                        text = font.previewTextEnglish,
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tags row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (font.isBundled) {
                    BadgeChip(text = if (isBangla) "অন্তর্ভুক্ত ফন্ট" else "Bundled Font", color = PrimaryIndigo)
                } else if (font.isDownloaded) {
                    BadgeChip(text = if (isBangla) "ডাউনলোড সম্পন্ন" else "Downloaded", color = SuccessEmerald)
                }

                font.tags.forEach { tag ->
                    BadgeChip(text = "#$tag", color = Color(0xFF64748B))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!font.isDownloaded && !font.isBundled) {
                    Button(
                        onClick = onDownload,
                        enabled = !isDownloading,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("download_font_${font.id}")
                    ) {
                        if (isDownloading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isBangla) "ডাউনলোড হচ্ছে..." else "Downloading...", fontSize = 12.sp)
                        } else {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isBangla) "ডাউনলোড করুন" else "Download Font", fontSize = 12.sp)
                        }
                    }
                } else {
                    // Set as App Font button
                    Button(
                        onClick = onApplyInApp,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActiveInApp) SuccessEmerald else PrimaryIndigo
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("apply_app_font_${font.id}")
                    ) {
                        Icon(imageVector = Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isActiveInApp) {
                                if (isBangla) "অ্যাপে সচল" else "In App"
                            } else {
                                if (isBangla) "অ্যাপে ব্যবহার" else "Set in App"
                            },
                            fontSize = 12.sp
                        )
                    }

                    // Install to Phone (OEM Changer)
                    OutlinedButton(
                        onClick = onInstallToPhone,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryCyan),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryCyan),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("install_phone_font_${font.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBangla) "ফোনে সেট করুন" else "Install to Phone",
                            fontSize = 12.sp
                        )
                    }
                }

                // Lab / Playground button
                IconButton(
                    onClick = onOpenPlayground,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E283D))
                        .testTag("test_playground_${font.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Test in Playground",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}
