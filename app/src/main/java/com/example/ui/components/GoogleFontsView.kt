package com.example.ui.components

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FontModel
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.TertiaryAmber

@Composable
fun GoogleFontsView(
    downloadableFonts: List<FontModel>,
    downloadingIds: Set<String>,
    isBangla: Boolean,
    onDownloadFont: (FontModel) -> Unit,
    onDownloadUrl: (String, String) -> Unit,
    onImportUri: (Uri, String) -> Unit,
    onSelectFont: (FontModel) -> Unit,
    onApplyInApp: (FontModel) -> Unit,
    onInstallToPhone: (FontModel) -> Unit,
    onOpenPlayground: (FontModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var customUrl by remember { mutableStateOf("") }
    var customFontName by remember { mutableStateOf("") }

    // File Picker Launcher for .ttf / .otf files
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            var fileName = "CustomFont.ttf"
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex)
                }
            }
            onImportUri(uri, fileName)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Section 1: Import Local TTF/OTF File from phone storage
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryCyan.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SecondaryCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.FileOpen, contentDescription = null, tint = SecondaryCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isBangla) "ফোন মেমরি থেকে ফন্ট যোগ করুন" else "Import from Storage",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBangla) ".TTF বা .OTF ফাইল সরাসরি অ্যাপে আনুন" else "Supports .ttf and .otf font files",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        filePickerLauncher.launch(arrayOf("*/*"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("choose_local_font_file_btn")
                ) {
                    Icon(imageVector = Icons.Default.FileOpen, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "ফাইল সিলেক্ট করুন (.ttf / .otf)" else "Select Font File (.ttf / .otf)",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: Download Custom Font via Web URL
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigo.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = PrimaryIndigo, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isBangla) "ওয়েব URL থেকে সরাসরি ডাউনলোড" else "Direct Web URL Downloader",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBangla) "যেকোনো ওয়েবসাইট থেকে .ttf লিংক দিন" else "Enter direct link to any .ttf file",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customUrl,
                    onValueChange = { customUrl = it },
                    label = { Text(if (isBangla) "ফন্টের ডাউনলোড লিংক (URL)" else "Font TTF Download URL") },
                    placeholder = { Text("https://example.com/font.ttf") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryIndigo,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = Color(0xFF0F1523),
                        unfocusedContainerColor = Color(0xFF0F1523),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_font_url_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customFontName,
                    onValueChange = { customFontName = it },
                    label = { Text(if (isBangla) "ফন্টের নাম (ঐচ্ছিক)" else "Font Name (Optional)") },
                    placeholder = { Text("My Custom Font") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryIndigo,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = Color(0xFF0F1523),
                        unfocusedContainerColor = Color(0xFF0F1523),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_font_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                val isDownloadingUrl = downloadingIds.contains("custom_url")
                Button(
                    onClick = {
                        onDownloadUrl(customUrl, customFontName)
                        customUrl = ""
                        customFontName = ""
                    },
                    enabled = customUrl.isNotBlank() && !isDownloadingUrl,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("download_custom_url_btn")
                ) {
                    if (isDownloadingUrl) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBangla) "ডাউনলোড হচ্ছে..." else "Downloading...", fontSize = 12.sp)
                    } else {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBangla) "ডাউনলোড ও যোগ করুন" else "Download & Add", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 3: Popular Online Google Fonts Directory
        Text(
            text = if (isBangla) "গুগল ফন্টস অনলাইন ক্যাটালগ:" else "Google Fonts Online Library:",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        downloadableFonts.forEach { font ->
            FontCard(
                font = font,
                isSelected = false,
                isActiveInApp = false,
                isDownloading = downloadingIds.contains(font.id),
                isBangla = isBangla,
                onSelect = { onSelectFont(font) },
                onDownload = { onDownloadFont(font) },
                onApplyInApp = { onApplyInApp(font) },
                onInstallToPhone = { onInstallToPhone(font) },
                onOpenPlayground = { onOpenPlayground(font) },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}
