package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FontModel
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald

data class PresetSample(val title: String, val sample: String)

@Composable
fun FontPlaygroundView(
    fonts: List<FontModel>,
    selectedFont: FontModel?,
    playgroundText: String,
    fontSize: Float,
    letterSpacing: Float,
    isBangla: Boolean,
    onSelectFont: (FontModel) -> Unit,
    onTextChange: (String) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onLetterSpacingChange: (Float) -> Unit,
    onApplyInApp: (FontModel) -> Unit,
    onInstallToPhone: (FontModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var showComparison by remember { mutableStateOf(false) }

    val presetTexts = listOf(
        PresetSample(
            if (isBangla) "আমার সোনার বাংলা" else "National Anthem",
            "আমার সোনার বাংলা, আমি তোমায় ভালোবাসি। চিরদিন তোমার আকাশ, তোমার বাতাস, আমার প্রাণে বাজায় বাঁশি।"
        ),
        PresetSample(
            if (isBangla) "যুক্তবর্ণ পরীক্ষা" else "Bengali Conjuncts",
            "ক্ষ, জ্ঞ, ষ্ণ, ঙ্ক, ঙ্গ, ঙ্ঘ, ঞ্চ, ঞ্ছ, ঞ্জ, ণ্ট, ণ্ঠ, ণ্ড, ত্ত, থ্থ, দ্দ, ধ্ধ, ন্ন, প্প, ব্ব, ম্ম, ল্ল, শ্চ, ষ্প, স্ত, স্থ, স্ন, স্প, স্ফ, হ্ন, হৃ"
        ),
        PresetSample(
            if (isBangla) "বাংলা বর্ণমালা" else "Bengali Alphabets",
            "অ আ ই ঈ উ ঊ ঋ এ ঐ ও ঔ | ক খ গ ঘ ঙ চ ছ জ ঝ ঞ ট ঠ ড ঢ ণ ত থ দ ধ ন প ফ ব ভ ম য র ল শ ষ স হ ড় ঢ় য় ৎ ং ঃ ঁ"
        ),
        PresetSample(
            if (isBangla) "ইংরেজি প্যানগ্রাম" else "English Pangram",
            "The quick brown fox jumps over the lazy dog. 0123456789 !@#$%^&*"
        ),
        PresetSample(
            if (isBangla) "স্মার্ট বাংলাদেশ" else "Tech Quote",
            "প্রযুক্তির বিকাশ ও নান্দনিক ফন্টের মেলবন্ধনে গড়ে উঠুক আগামী দিনের ডিজিটাল অভিজ্ঞতা।"
        )
    )

    val currentFont = selectedFont ?: fonts.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Font Selection Horizontal Carousel
        Text(
            text = if (isBangla) "ফন্ট নির্বাচন করুন:" else "Select Font to Test:",
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFFCBD5E1),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(fonts) { font ->
                val isSelected = font.id == currentFont?.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PrimaryIndigo else DarkSurfaceVariant)
                        .border(1.dp, if (isSelected) SecondaryCyan else DarkCardBorder, RoundedCornerShape(12.dp))
                        .clickable { onSelectFont(font) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                        .testTag("playground_select_font_${font.id}")
                ) {
                    Text(
                        text = if (isBangla) font.nativeName else font.name,
                        color = if (isSelected) Color.White else Color(0xFF94A3B8),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Quick Chips
        Text(
            text = if (isBangla) "টেস্টিং প্রিসেট নমুনা:" else "Sample Presets:",
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presetTexts.forEach { preset ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF162035))
                        .clickable { onTextChange(preset.sample) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = preset.title, color = SecondaryCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Font Canvas Box
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1523)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222F49)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentFont?.name ?: "Default"} Preview",
                        color = SecondaryCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isBangla) "তুলনা করুন" else "Compare",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = showComparison,
                            onCheckedChange = { showComparison = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SecondaryCyan,
                                checkedTrackColor = PrimaryIndigo
                            ),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom font rendered text
                Text(
                    text = playgroundText,
                    fontFamily = currentFont?.toFontFamily() ?: FontFamily.Default,
                    fontSize = fontSize.sp,
                    letterSpacing = letterSpacing.sp,
                    color = Color.White,
                    lineHeight = (fontSize * 1.4f).sp,
                    modifier = Modifier.fillMaxWidth()
                )

                // Comparison Box (Default System Font)
                AnimatedVisibility(visible = showComparison) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF161F30))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = if (isBangla) "অ্যান্ড্রয়েডের সাধারণ ডিফল্ট সিস্টেম ফন্ট (Roboto):" else "Android Default System Font (Roboto):",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = playgroundText,
                            fontFamily = FontFamily.Default,
                            fontSize = fontSize.sp,
                            letterSpacing = letterSpacing.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = (fontSize * 1.4f).sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Text Input Field
        OutlinedTextField(
            value = playgroundText,
            onValueChange = onTextChange,
            label = { Text(if (isBangla) "নিজের লেখা দিয়ে টেস্ট করুন" else "Type Custom Text") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryIndigo,
                unfocusedBorderColor = DarkCardBorder,
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("playground_text_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sliders Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Font Size Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBangla) "ফন্টের আকার (Font Size):" else "Font Size:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                    Text(text = "${fontSize.toInt()} sp", color = SecondaryCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Slider(
                    value = fontSize,
                    onValueChange = onFontSizeChange,
                    valueRange = 14f..48f,
                    colors = SliderDefaults.colors(
                        thumbColor = SecondaryCyan,
                        activeTrackColor = PrimaryIndigo
                    ),
                    modifier = Modifier.testTag("playground_font_size_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Letter Spacing Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isBangla) "অক্ষরের দূরত্ব (Letter Spacing):" else "Letter Spacing:",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                    Text(text = "${"%.1f".format(letterSpacing)} sp", color = SecondaryCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Slider(
                    value = letterSpacing,
                    onValueChange = onLetterSpacingChange,
                    valueRange = -1f..6f,
                    colors = SliderDefaults.colors(
                        thumbColor = SecondaryCyan,
                        activeTrackColor = PrimaryIndigo
                    ),
                    modifier = Modifier.testTag("playground_letter_spacing_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Action Row for this font
        if (currentFont != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onApplyInApp(currentFont) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("playground_apply_in_app_btn")
                ) {
                    Icon(imageVector = Icons.Default.FormatPaint, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isBangla) "অ্যাপে সেট করুন" else "Apply to App", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { onInstallToPhone(currentFont) },
                    border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryCyan),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("playground_install_phone_btn")
                ) {
                    Icon(imageVector = Icons.Default.Smartphone, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isBangla) "ফোনে সেট করুন" else "Install to Phone", fontSize = 12.sp)
                }
            }
        }
    }
}
