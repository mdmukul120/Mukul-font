package com.example.model

import android.graphics.Typeface
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import java.io.File

enum class FontCategory(val labelEn: String, val labelBn: String) {
    ALL("All", "সকল"),
    BENGALI("Bengali", "বাংলা"),
    SANS("Sans-Serif", "আধুনিক Sans"),
    SERIF("Serif", "ক্লাসিক Serif"),
    DISPLAY("Display", "স্টাইলিশ Display"),
    HANDWRITING("Handwritten", "হস্তলিপি"),
    CUSTOM("Custom", "কাস্টম")
}

enum class DeviceOem(
    val title: String,
    val subtitle: String,
    val methodEn: String,
    val methodBn: String,
    val requiresRoot: Boolean = false
) {
    AUTO_DETECT("Auto Detect", "স্বয়ংক্রিয় শনাক্ত", "System Auto", "সিস্টেম অটো"),
    XIAOMI(
        "Xiaomi / Redmi / POCO",
        "HyperOS & MIUI",
        "MTZ Theme Font / Themes Store Import",
        "MTZ থিম ফন্ট / থিমস স্টোর ইমপোর্ট"
    ),
    SAMSUNG(
        "Samsung",
        "OneUI 4.0 / 5.0 / 6.0 / 7.0",
        "Monotype Font APK / Hex Installer / Theme Park",
        "মনোটাইপ ফন্ট APK / হেক্স প্লাগইন / থিম পার্ক"
    ),
    OPPO_REALME_ONEPLUS(
        "OnePlus / OPPO / Realme",
        "OxygenOS / ColorOS / RealmeUI",
        "Theme Store Region Change & APK Method",
        "থিম স্টোর রিজিওন চেঞ্জ ও APK মেথড"
    ),
    VIVO(
        "Vivo / iQOO",
        "OriginOS & FuntouchOS",
        "ITZ Font Format & Theme Directory Injection",
        "ITZ ফন্ট ফরম্যাট ও থিম ফাইল ইনজেকশন"
    ),
    PIXEL_AOSP(
        "Google Pixel / Motorola / AOSP",
        "Stock Android & Nothing OS",
        "Shizuku / Wireless Debugging Overlay",
        "Shizuku / ওয়ারলেস ডিবাগিং ওভারলে"
    ),
    MAGISK_ROOT(
        "Root / Magisk / KernelSU",
        "Universal (All Android Devices)",
        "Flashable System Font Module ZIP",
        "ফ্ল্যাশেবল সিস্টেম ফন্ট মডিউল ZIP (সব ফোনে ১০০% কাজ করে)",
        requiresRoot = true
    )
}

data class FontModel(
    val id: String,
    val name: String,
    val nativeName: String = name,
    val category: FontCategory,
    val author: String = "Google Fonts / Open Source",
    val languageSupport: String = "Bengali & English",
    val isBundled: Boolean = false,
    val resId: Int? = null,
    val downloadUrl: String? = null,
    val localFilePath: String? = null,
    val previewTextBengali: String = "আমার সোনার বাংলা, আমি তোমায় ভালোবাসি।",
    val previewTextEnglish: String = "The quick brown fox jumps over the lazy dog.",
    val tags: List<String> = emptyList(),
    val isDownloaded: Boolean = isBundled,
    val fileSizeBytes: Long = 180_000L
) {
    fun toFontFamily(): FontFamily {
        return if (resId != null) {
            FontFamily(Font(resId))
        } else if (localFilePath != null) {
            val file = File(localFilePath)
            if (file.exists()) {
                try {
                    val typeface = Typeface.createFromFile(file)
                    FontFamily(typeface)
                } catch (e: Exception) {
                    FontFamily.Default
                }
            } else {
                FontFamily.Default
            }
        } else {
            FontFamily.Default
        }
    }
}
