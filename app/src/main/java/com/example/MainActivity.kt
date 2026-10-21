package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DeviceOem
import com.example.model.FontCategory
import com.example.model.FontModel
import com.example.ui.components.DeviceGuideDialog
import com.example.ui.components.FontCard
import com.example.ui.components.FontPlaygroundView
import com.example.ui.components.GithubCiDialog
import com.example.ui.components.GoogleFontsView
import com.example.ui.components.OemInstallerView
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryCyan
import com.example.ui.theme.SuccessEmerald
import com.example.viewmodel.FontTab
import com.example.viewmodel.FontViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FontMorphApp()
            }
        }
    }
}

@Composable
fun FontMorphApp(viewModel: FontViewModel = viewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val filteredFonts by viewModel.filteredFonts.collectAsState()
    val allFonts by viewModel.allFonts.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedFont by viewModel.selectedFont.collectAsState()
    val inAppActiveFont by viewModel.inAppActiveFont.collectAsState()
    val isBangla by viewModel.isBangla.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedOem by viewModel.selectedOem.collectAsState()
    val detectedOem by viewModel.detectedOem.collectAsState()
    val downloadingIds by viewModel.downloadingIds.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val exportResult by viewModel.exportResult.collectAsState()

    val playgroundText by viewModel.playgroundText.collectAsState()
    val playgroundFontSize by viewModel.playgroundFontSize.collectAsState()
    val playgroundLetterSpacing by viewModel.playgroundLetterSpacing.collectAsState()

    // Dialogs state
    var showDeviceGuideDialog by remember { mutableStateOf(false) }
    var showGithubCiDialog by remember { mutableStateOf(false) }

    // Handle status messages via Snackbar
    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(message = msg, duration = SnackbarDuration.Short)
            viewModel.clearStatusMessage()
        }
    }

    // Handle export share intent via Android Sharesheet
    LaunchedEffect(exportResult) {
        exportResult?.let { res ->
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_STREAM, res.shareUri)
                putExtra(Intent.EXTRA_TITLE, res.title)
                putExtra(Intent.EXTRA_TEXT, res.message)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(sendIntent, "Mukul font: Share / Install Package")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            viewModel.clearExportResult()
        }
    }

    // Active in-app font override for headers and dynamic preview
    val appCustomFontFamily = inAppActiveFont?.toFontFamily() ?: FontFamily.Default

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                isBangla = isBangla,
                detectedOem = detectedOem,
                inAppActiveFont = inAppActiveFont,
                fontFamily = appCustomFontFamily,
                onToggleLanguage = { viewModel.toggleLanguage() },
                onOpenGithubCi = { showGithubCiDialog = true },
                onResetInAppFont = { viewModel.resetInAppFont() },
                onOpenOem = {
                    viewModel.setActiveTab(FontTab.INSTALLER)
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                FontTab.values().forEach { tab ->
                    val isSelected = activeTab == tab
                    val label = if (isBangla) tab.titleBn else tab.titleEn
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setActiveTab(tab) },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    FontTab.EXPLORE -> Icons.Default.Explore
                                    FontTab.GOOGLE_WEB -> Icons.Default.FontDownload
                                    FontTab.PLAYGROUND -> Icons.Default.PlayArrow
                                    FontTab.INSTALLER -> Icons.Default.Smartphone
                                },
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = SecondaryCyan,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B),
                            indicatorColor = PrimaryIndigo
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name}")
                    )
                }
            }
        },
        containerColor = DarkBackground,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                FontTab.EXPLORE -> {
                    CatalogScreen(
                        fonts = filteredFonts,
                        selectedFont = selectedFont,
                        inAppActiveFont = inAppActiveFont,
                        downloadingIds = downloadingIds,
                        selectedCategory = selectedCategory,
                        searchQuery = searchQuery,
                        isBangla = isBangla,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategorySelect = { viewModel.setCategory(it) },
                        onSelectFont = { viewModel.selectFont(it) },
                        onDownloadFont = { viewModel.downloadFont(it) },
                        onApplyInApp = { viewModel.applyInAppFont(it) },
                        onInstallToPhone = {
                            viewModel.selectFont(it)
                            showDeviceGuideDialog = true
                        },
                        onOpenPlayground = {
                            viewModel.selectFont(it)
                            viewModel.setActiveTab(FontTab.PLAYGROUND)
                        }
                    )
                }
                FontTab.GOOGLE_WEB -> {
                    GoogleFontsView(
                        downloadableFonts = allFonts.filter { !it.isBundled },
                        downloadingIds = downloadingIds,
                        isBangla = isBangla,
                        onDownloadFont = { viewModel.downloadFont(it) },
                        onDownloadUrl = { url, name -> viewModel.downloadFromUrl(url, name) },
                        onImportUri = { uri, name -> viewModel.importFromUri(uri, name) },
                        onSelectFont = { viewModel.selectFont(it) },
                        onApplyInApp = { viewModel.applyInAppFont(it) },
                        onInstallToPhone = {
                            viewModel.selectFont(it)
                            showDeviceGuideDialog = true
                        },
                        onOpenPlayground = {
                            viewModel.selectFont(it)
                            viewModel.setActiveTab(FontTab.PLAYGROUND)
                        }
                    )
                }
                FontTab.PLAYGROUND -> {
                    FontPlaygroundView(
                        fonts = allFonts,
                        selectedFont = selectedFont,
                        playgroundText = playgroundText,
                        fontSize = playgroundFontSize,
                        letterSpacing = playgroundLetterSpacing,
                        isBangla = isBangla,
                        onSelectFont = { viewModel.selectFont(it) },
                        onTextChange = { viewModel.setPlaygroundText(it) },
                        onFontSizeChange = { viewModel.setPlaygroundFontSize(it) },
                        onLetterSpacingChange = { viewModel.setPlaygroundLetterSpacing(it) },
                        onApplyInApp = { viewModel.applyInAppFont(it) },
                        onInstallToPhone = {
                            viewModel.selectFont(it)
                            showDeviceGuideDialog = true
                        }
                    )
                }
                FontTab.INSTALLER -> {
                    OemInstallerView(
                        selectedFont = selectedFont,
                        selectedOem = selectedOem,
                        detectedOem = detectedOem,
                        isBangla = isBangla,
                        onSelectOem = { viewModel.setSelectedOem(it) },
                        onExportPackage = { oem, font -> viewModel.exportFontPackage(oem, font) },
                        onApplyInApp = { viewModel.applyInAppFont(it) },
                        onGoToCatalog = { viewModel.setActiveTab(FontTab.EXPLORE) }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showDeviceGuideDialog && selectedFont != null) {
        DeviceGuideDialog(
            font = selectedFont!!,
            selectedOem = selectedOem,
            isBangla = isBangla,
            onDismiss = { showDeviceGuideDialog = false },
            onSelectOem = { viewModel.setSelectedOem(it) },
            onExportPackage = { oem, font ->
                viewModel.exportFontPackage(oem, font)
                showDeviceGuideDialog = false
            }
        )
    }

    if (showGithubCiDialog) {
        GithubCiDialog(
            isBangla = isBangla,
            onDismiss = { showGithubCiDialog = false }
        )
    }
}

@Composable
fun TopAppBar(
    isBangla: Boolean,
    detectedOem: DeviceOem,
    inAppActiveFont: FontModel?,
    fontFamily: FontFamily,
    onToggleLanguage: () -> Unit,
    onOpenGithubCi: () -> Unit,
    onResetInAppFont: () -> Unit,
    onOpenOem: () -> Unit
) {
    Surface(
        color = DarkSurface,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand logo & title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenOem() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(PrimaryIndigo, SecondaryCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Mukul font",
                            fontFamily = fontFamily,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBangla) "মোবাইল ফন্ট চেঞ্জার" else "Custom Font Changer",
                            fontFamily = fontFamily,
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryCyan
                        )
                    }
                }

                // Action buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Toggle
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E283D))
                            .clickable { onToggleLanguage() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("lang_toggle_btn")
                    ) {
                        Text(
                            text = if (isBangla) "বাং" else "EN",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // GitHub CI/CD Info Dialog button
                    IconButton(
                        onClick = onOpenGithubCi,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E283D))
                            .testTag("github_ci_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = "GitHub CI/CD Release",
                            tint = SecondaryCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // In-app font status banner if active
            AnimatedVisibility(visible = inAppActiveFont != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SuccessEmerald.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "সক্রিয় ফন্ট: ${inAppActiveFont?.name}" else "Active Font: ${inAppActiveFont?.name}",
                        color = SuccessEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (isBangla) "রিস্টোর" else "Reset",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onResetInAppFont() }
                    )
                }
            }
        }
    }
}

@Composable
fun CatalogScreen(
    fonts: List<FontModel>,
    selectedFont: FontModel?,
    inAppActiveFont: FontModel?,
    downloadingIds: Set<String>,
    selectedCategory: FontCategory,
    searchQuery: String,
    isBangla: Boolean,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (FontCategory) -> Unit,
    onSelectFont: (FontModel) -> Unit,
    onDownloadFont: (FontModel) -> Unit,
    onApplyInApp: (FontModel) -> Unit,
    onInstallToPhone: (FontModel) -> Unit,
    onOpenPlayground: (FontModel) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search & Filter Header
        item {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.White)
                            }
                        }
                    },
                    placeholder = {
                        Text(if (isBangla) "ফন্ট খুঁজুন (নাম, লেখক, ধরন)..." else "Search fonts...")
                    },
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
                        .testTag("font_search_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FontCategory.values().forEach { category ->
                        val isSelected = category == selectedCategory
                        val title = if (isBangla) category.labelBn else category.labelEn
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelect(category) },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryIndigo,
                                selectedLabelColor = Color.White,
                                containerColor = DarkSurfaceVariant,
                                labelColor = Color(0xFF94A3B8)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = DarkCardBorder,
                                selectedBorderColor = SecondaryCyan
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // Font Cards List
        if (fonts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isBangla) "কোনো ফন্ট পাওয়া যায়নি!" else "No fonts found!",
                            color = Color(0xFF94A3B8),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBangla) "অন্য নাম দিয়ে সার্চ করুন অথবা ওয়েব থেকে যোগ করুন।" else "Try different search terms or import from Web.",
                            color = Color(0xFF64748B),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        } else {
            items(fonts, key = { it.id }) { font ->
                FontCard(
                    font = font,
                    isSelected = font.id == selectedFont?.id,
                    isActiveInApp = font.id == inAppActiveFont?.id,
                    isDownloading = downloadingIds.contains(font.id),
                    isBangla = isBangla,
                    onSelect = { onSelectFont(font) },
                    onDownload = { onDownloadFont(font) },
                    onApplyInApp = { onApplyInApp(font) },
                    onInstallToPhone = { onInstallToPhone(font) },
                    onOpenPlayground = { onOpenPlayground(font) }
                )
            }
        }
    }
}
