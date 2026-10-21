package com.example.viewmodel

import android.app.Application
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FontRepository
import com.example.generator.FontModuleGenerator
import com.example.model.DeviceOem
import com.example.model.FontCategory
import com.example.model.FontModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

enum class FontTab(val titleEn: String, val titleBn: String) {
    EXPLORE("Catalog", "ফন্ট লাইব্রেরি"),
    GOOGLE_WEB("Web & Import", "গুগল ও ইমপোর্ট"),
    PLAYGROUND("Playground", "টেস্ট ল্যাব"),
    INSTALLER("Apply & Install", "সেট করুন")
}

data class ExportResult(
    val file: File,
    val shareUri: Uri,
    val title: String,
    val message: String
)

class FontViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FontRepository(application)

    private val _allFonts = MutableStateFlow<List<FontModel>>(emptyList())
    val allFonts: StateFlow<List<FontModel>> = _allFonts.asStateFlow()

    private val _selectedCategory = MutableStateFlow(FontCategory.ALL)
    val selectedCategory: StateFlow<FontCategory> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeTab = MutableStateFlow(FontTab.EXPLORE)
    val activeTab: StateFlow<FontTab> = _activeTab.asStateFlow()

    private val _selectedFont = MutableStateFlow<FontModel?>(null)
    val selectedFont: StateFlow<FontModel?> = _selectedFont.asStateFlow()

    private val _inAppActiveFont = MutableStateFlow<FontModel?>(null)
    val inAppActiveFont: StateFlow<FontModel?> = _inAppActiveFont.asStateFlow()

    private val _isBangla = MutableStateFlow(true)
    val isBangla: StateFlow<Boolean> = _isBangla.asStateFlow()

    // Playground controls
    private val _playgroundText = MutableStateFlow("আমার সোনার বাংলা, আমি তোমায় ভালোবাসি। চিরদিন তোমার আকাশ, তোমার বাতাস, আমার প্রাণে বাজায় বাঁশি। The quick brown fox jumps over the lazy dog 1234567890")
    val playgroundText: StateFlow<String> = _playgroundText.asStateFlow()

    private val _playgroundFontSize = MutableStateFlow(24f)
    val playgroundFontSize: StateFlow<Float> = _playgroundFontSize.asStateFlow()

    private val _playgroundLetterSpacing = MutableStateFlow(0f)
    val playgroundLetterSpacing: StateFlow<Float> = _playgroundLetterSpacing.asStateFlow()

    // OEM state
    private val _detectedOem = MutableStateFlow(detectDeviceOem())
    val detectedOem: StateFlow<DeviceOem> = _detectedOem.asStateFlow()

    private val _selectedOem = MutableStateFlow(detectDeviceOem())
    val selectedOem: StateFlow<DeviceOem> = _selectedOem.asStateFlow()

    // Async states
    private val _downloadingIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadingIds: StateFlow<Set<String>> = _downloadingIds.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _exportResult = MutableStateFlow<ExportResult?>(null)
    val exportResult: StateFlow<ExportResult?> = _exportResult.asStateFlow()

    val filteredFonts: StateFlow<List<FontModel>> = combine(_allFonts, _selectedCategory, _searchQuery) { fonts, cat, query ->
        fonts.filter { font ->
            val matchCategory = cat == FontCategory.ALL || font.category == cat
            val matchQuery = query.isBlank() ||
                    font.name.contains(query, ignoreCase = true) ||
                    font.nativeName.contains(query, ignoreCase = true) ||
                    font.author.contains(query, ignoreCase = true) ||
                    font.tags.any { it.contains(query, ignoreCase = true) }
            matchCategory && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadFonts()
    }

    fun loadFonts() {
        val list = repository.getAllFonts()
        _allFonts.value = list
        if (_selectedFont.value == null && list.isNotEmpty()) {
            _selectedFont.value = list.first()
        }
    }

    fun setCategory(category: FontCategory) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActiveTab(tab: FontTab) {
        _activeTab.value = tab
    }

    fun selectFont(font: FontModel) {
        _selectedFont.value = font
    }

    fun toggleLanguage() {
        _isBangla.value = !_isBangla.value
    }

    fun setPlaygroundText(text: String) {
        _playgroundText.value = text
    }

    fun setPlaygroundFontSize(size: Float) {
        _playgroundFontSize.value = size
    }

    fun setPlaygroundLetterSpacing(spacing: Float) {
        _playgroundLetterSpacing.value = spacing
    }

    fun setSelectedOem(oem: DeviceOem) {
        _selectedOem.value = oem
    }

    fun applyInAppFont(font: FontModel) {
        _inAppActiveFont.value = font
        _statusMessage.value = if (_isBangla.value) {
            "\"${font.name}\" ফন্ট অ্যাপে সফলভাবে সেট করা হয়েছে!"
        } else {
            "\"${font.name}\" applied to Mukul Font UI!"
        }
    }

    fun resetInAppFont() {
        _inAppActiveFont.value = null
        _statusMessage.value = if (_isBangla.value) "ডিফল্ট ফন্ট রিস্টোর করা হয়েছে" else "Default font restored"
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun clearExportResult() {
        _exportResult.value = null
    }

    fun downloadFont(font: FontModel) {
        if (font.isDownloaded || font.isBundled) return

        viewModelScope.launch {
            _downloadingIds.value = _downloadingIds.value + font.id
            try {
                val updated = repository.downloadFont(font)
                loadFonts()
                _selectedFont.value = updated
                _statusMessage.value = if (_isBangla.value) {
                    "\"${font.name}\" ফন্ট সফলভাবে ডাউনলোড হয়েছে!"
                } else {
                    "\"${font.name}\" downloaded successfully!"
                }
            } catch (e: Exception) {
                _statusMessage.value = if (_isBangla.value) {
                    "ডাউনলোড ব্যর্থ হয়েছে: ${e.localizedMessage}"
                } else {
                    "Download failed: ${e.localizedMessage}"
                }
            } finally {
                _downloadingIds.value = _downloadingIds.value - font.id
            }
        }
    }

    fun downloadFromUrl(url: String, name: String) {
        if (url.isBlank()) {
            _statusMessage.value = if (_isBangla.value) "অনুগ্রহ করে একটি সঠিক URL দিন" else "Please enter a valid URL"
            return
        }
        viewModelScope.launch {
            _downloadingIds.value = _downloadingIds.value + "custom_url"
            try {
                val font = repository.downloadFromCustomUrl(url, name)
                loadFonts()
                _selectedFont.value = font
                _statusMessage.value = if (_isBangla.value) {
                    "\"${font.name}\" অনলাইন থেকে সফলভাবে যোগ করা হয়েছে!"
                } else {
                    "\"${font.name}\" downloaded and added successfully!"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error: ${e.localizedMessage}"
            } finally {
                _downloadingIds.value = _downloadingIds.value - "custom_url"
            }
        }
    }

    fun importFromUri(uri: Uri, displayName: String) {
        viewModelScope.launch {
            try {
                val font = repository.importFontFromUri(uri, displayName)
                loadFonts()
                _selectedFont.value = font
                _statusMessage.value = if (_isBangla.value) {
                    "\"${font.name}\" ফন্ট সফলভাবে ইমপোর্ট হয়েছে!"
                } else {
                    "\"${font.name}\" imported successfully!"
                }
            } catch (e: Exception) {
                _statusMessage.value = "Import failed: ${e.localizedMessage}"
            }
        }
    }

    fun exportFontPackage(oem: DeviceOem, font: FontModel) {
        viewModelScope.launch {
            val context = getApplication<Application>()
            try {
                val file: File
                val title: String
                val message: String

                when (oem) {
                    DeviceOem.XIAOMI -> {
                        file = FontModuleGenerator.generateXiaomiMtz(context, font)
                        title = "Xiaomi MTZ Font Theme"
                        message = if (_isBangla.value) {
                            "Xiaomi MTZ থিম ফন্ট প্রস্তুত! থিম অ্যাপে গিয়ে 'Import' অপশন দিয়ে সিলেক্ট করুন।"
                        } else {
                            "Xiaomi MTZ theme ready! Open Themes app > Profile > Fonts > Import to apply."
                        }
                    }
                    DeviceOem.MAGISK_ROOT -> {
                        file = FontModuleGenerator.generateMagiskModule(context, font)
                        title = "Magisk Root Flashable ZIP"
                        message = if (_isBangla.value) {
                            "ম্যাজিস্ক ফ্ল্যাশেবল মডিউল ZIP তৈরি হয়েছে! Magisk অথবা KernelSU অ্যাপে গিয়ে মডিউলটি ইনস্টল করে রিবুট দিন।"
                        } else {
                            "Magisk flashable ZIP generated! Flash in Magisk or KernelSU modules tab and reboot."
                        }
                    }
                    DeviceOem.VIVO -> {
                        file = FontModuleGenerator.generateVivoItz(context, font)
                        title = "Vivo ITZ Font Theme"
                        message = if (_isBangla.value) {
                            "Vivo ITZ ফন্ট তৈরি হয়েছে! থিমস স্টোর দিয়ে অ্যাপ্লাই করুন।"
                        } else {
                            "Vivo ITZ font package generated!"
                        }
                    }
                    else -> {
                        file = FontModuleGenerator.exportTtfFile(context, font)
                        title = "${font.name} TTF Font File"
                        message = if (_isBangla.value) {
                            "TTF ফন্ট ফাইল এক্সপোর্ট সম্পন্ন! যেকোনো ফন্ট অ্যাপ বা সেটিংসে ব্যবহার করতে পারবেন।"
                        } else {
                            "TTF font exported successfully!"
                        }
                    }
                }

                val uri = FontModuleGenerator.getShareableUri(context, file)
                _exportResult.value = ExportResult(file, uri, title, message)
            } catch (e: Exception) {
                _statusMessage.value = "Export error: ${e.localizedMessage}"
            }
        }
    }

    private fun detectDeviceOem(): DeviceOem {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()
        return when {
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || brand.contains("poco") -> DeviceOem.XIAOMI
            manufacturer.contains("samsung") -> DeviceOem.SAMSUNG
            manufacturer.contains("oppo") || manufacturer.contains("realme") || manufacturer.contains("oneplus") -> DeviceOem.OPPO_REALME_ONEPLUS
            manufacturer.contains("vivo") || brand.contains("iqoo") -> DeviceOem.VIVO
            manufacturer.contains("google") || manufacturer.contains("motorola") || manufacturer.contains("nothing") -> DeviceOem.PIXEL_AOSP
            else -> DeviceOem.AUTO_DETECT
        }
    }
}
