package com.example.data

import android.content.Context
import android.net.Uri
import com.example.R
import com.example.model.FontCategory
import com.example.model.FontModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class FontRepository(private val context: Context) {

    private val prefs = context.getSharedPreferences("fontmorph_prefs", Context.MODE_PRIVATE)
    private val fontsDir = File(context.filesDir, "fonts").apply { mkdirs() }

    // Curated catalog of bundled and downloadable fonts
    private val initialCatalog = listOf(
        // Bundled Bengali Fonts
        FontModel(
            id = "anek_bangla",
            name = "Anek Bangla",
            nativeName = "অনেক বাংলা",
            category = FontCategory.BENGALI,
            author = "Ek Type & Google Fonts",
            languageSupport = "Bengali & English",
            isBundled = true,
            resId = R.font.anek_bangla,
            previewTextBengali = "আমার সোনার বাংলা, আমি তোমায় ভালোবাসি। চিরদিন তোমার আকাশ, তোমার বাতাস, আমার প্রাণে বাজায় বাঁশি।",
            previewTextEnglish = "Modern Bengali typeface crafted for versatile mobile UI and readability.",
            tags = listOf("Bengali", "Sans", "Popular", "Clean")
        ),
        FontModel(
            id = "hind_siliguri",
            name = "Hind Siliguri",
            nativeName = "হিন্দ শিলিগুড়ি",
            category = FontCategory.BENGALI,
            author = "Indian Type Foundry",
            languageSupport = "Bengali & Latin",
            isBundled = true,
            resId = R.font.hind_siliguri,
            previewTextBengali = "সহজ কথায় বাংলা লিখুন ও পড়ুন অতি চমৎকার পরিচ্ছন্ন অক্ষরে।",
            previewTextEnglish = "Crisp and elegant Bengali typography for mobile screens.",
            tags = listOf("Bengali", "Display", "Android", "Clear")
        ),
        FontModel(
            id = "galada",
            name = "Galada",
            nativeName = "গালাদা স্টাইলিশ",
            category = FontCategory.BENGALI,
            author = "Carolina Giovagnoli",
            languageSupport = "Bengali Script",
            isBundled = true,
            resId = R.font.galada,
            previewTextBengali = "হৃদয় মাঝে গানের সুর, রঙিন স্বপ্ন আঁকা ভোর।",
            previewTextEnglish = "Flowing and artistic cursive Bengali script.",
            tags = listOf("Bengali", "Artistic", "Calligraphy", "Stylish")
        ),

        // Bundled Global Fonts
        FontModel(
            id = "poppins",
            name = "Poppins",
            nativeName = "পপিন্স জ্যামিতিক",
            category = FontCategory.SANS,
            author = "Ninad Raje, Devanagari Foundry",
            languageSupport = "Latin, Devanagari",
            isBundled = true,
            resId = R.font.poppins,
            previewTextBengali = "১ ২ ৩ ৪ ৫ ৬ ৭ ৮ ৯ ০ — ডিজিটাল বাংলাদেশের আধুনিক ফন্ট।",
            previewTextEnglish = "Geometric sans-serif with international acclaim.",
            tags = listOf("Sans", "Geometric", "Minimal", "Google")
        ),
        FontModel(
            id = "cinzel",
            name = "Cinzel",
            nativeName = "সিনজেল রাজকীয়",
            category = FontCategory.SERIF,
            author = "Natanael Gama",
            languageSupport = "Latin & Classical",
            isBundled = true,
            resId = R.font.cinzel,
            previewTextBengali = "রাজকীয় আভিজাত্যের স্পর্শে ক্লাসিক্যাল ফন্ট।",
            previewTextEnglish = "Inspired by first century Roman inscriptions.",
            tags = listOf("Serif", "Classic", "Luxury", "Royal")
        ),
        FontModel(
            id = "orbitron",
            name = "Orbitron",
            nativeName = "অরবিট্রন সাইবারপাঙ্ক",
            category = FontCategory.DISPLAY,
            author = "Matt McInerney",
            languageSupport = "Latin & Sci-Fi",
            isBundled = true,
            resId = R.font.orbitron,
            previewTextBengali = "ভবিষ্যতের প্রযুক্তি ও গেমিং ইন্টারফেস ফন্ট।",
            previewTextEnglish = "Cyberpunk sci-fi geometric display typeface.",
            tags = listOf("Display", "Cyberpunk", "Gaming", "Sci-Fi")
        ),
        FontModel(
            id = "pacifico",
            name = "Pacifico",
            nativeName = "প্যাসিফিকো কার্সিভ",
            category = FontCategory.HANDWRITING,
            author = "Vernon Adams",
            languageSupport = "Latin Script",
            isBundled = true,
            resId = R.font.pacifico,
            previewTextBengali = "হাতের লেখার স্নিগ্ধ ও মিষ্টি রূপরেখা।",
            previewTextEnglish = "Delightful original brush script inspired by 1950s surf culture.",
            tags = listOf("Handwritten", "Retro", "Fun", "Cursive")
        ),

        // Downloadable Online Bengali & Global Fonts
        FontModel(
            id = "noto_serif_bengali",
            name = "Noto Serif Bengali",
            nativeName = "নোটো সেরিফ বাংলা (কল্পরূপ)",
            category = FontCategory.BENGALI,
            author = "Google Fonts",
            languageSupport = "Bengali & English",
            isBundled = false,
            downloadUrl = "https://fonts.gstatic.com/s/notoserifbengali/v23/wXKyE3c2D8kK6l9-O_9YFvQn9g.ttf",
            previewTextBengali = "বাংলা সাহিত্যের চিরায়ত রূপ ও মার্জিত পাঠযোগ্য সেরিফ ফন্ট।",
            previewTextEnglish = "Refined serif Bengali font suitable for books and long articles.",
            tags = listOf("Bengali", "Serif", "Classic", "Literature")
        ),
        FontModel(
            id = "tiro_bangla",
            name = "Tiro Bangla",
            nativeName = "তিরো বাংলা",
            category = FontCategory.BENGALI,
            author = "Tiro Typeworks & John Hudson",
            languageSupport = "Bengali & Latin",
            isBundled = false,
            downloadUrl = "https://fonts.gstatic.com/s/tirobangla/v5/3XFjEr0y0Zf076bB8fB4aZc.ttf",
            previewTextBengali = "সুকুমার রায় ও রবীন্দ্রনাথ ঠাকুরের গল্পের নিখুঁত রূপ।",
            previewTextEnglish = "Specialized Bengali typography for academic and editorial excellence.",
            tags = listOf("Bengali", "Editorial", "Formal")
        ),
        FontModel(
            id = "mina",
            name = "Mina Regular",
            nativeName = "মীনা বাংলা",
            category = FontCategory.BENGALI,
            author = "Sorkin Type",
            languageSupport = "Bengali & Latin",
            isBundled = false,
            downloadUrl = "https://fonts.gstatic.com/s/mina/v16/bvAg7K_9pC56Qe2Z.ttf",
            previewTextBengali = "সহজ ও সাবলীল বাংলা বর্ণমালা ও আধুনিক বানান শৈলী।",
            previewTextEnglish = "Compact and contemporary Bengali sans-serif design.",
            tags = listOf("Bengali", "Sans", "Modern")
        ),
        FontModel(
            id = "montserrat",
            name = "Montserrat",
            nativeName = "মন্টসেরাট",
            category = FontCategory.SANS,
            author = "Julieta Ulanovsky",
            languageSupport = "Latin & Global",
            isBundled = false,
            downloadUrl = "https://fonts.gstatic.com/s/montserrat/v26/JTUHjIg1_i6t8kCHKm4532VJOt5-QNFgpCtr6Ew-.ttf",
            previewTextBengali = "আধুনিক পোস্টার ও মোবাইল সিস্টেমের জন্য নিখুঁত।",
            previewTextEnglish = "Inspired by the old posters and signs in the traditional Montserrat neighborhood of Buenos Aires.",
            tags = listOf("Sans", "Google", "Popular")
        ),
        FontModel(
            id = "dancing_script",
            name = "Dancing Script",
            nativeName = "ড্যান্সিং স্ক্রিপ্ট",
            category = FontCategory.HANDWRITING,
            author = "Impallari Type",
            languageSupport = "Latin",
            isBundled = false,
            downloadUrl = "https://fonts.gstatic.com/s/dancingscript/v25/If2cXTr6YS-zF4S-kcSWSVi_sxjsohD9F50Ruu7BMSo3Sup6.ttf",
            previewTextBengali = "সুন্দর ও ছন্দময় ক্যালিগ্রাফি স্টাইল।",
            previewTextEnglish = "A lively casual script where letters bounce and change size slightly.",
            tags = listOf("Handwriting", "Calligraphy", "Stylish")
        ),
        FontModel(
            id = "press_start_2p",
            name = "Press Start 2P",
            nativeName = "প্রেস স্টার্ট পিক্সেল",
            category = FontCategory.DISPLAY,
            author = "CodeMan38",
            languageSupport = "Latin 8-Bit",
            isBundled = false,
            downloadUrl = "https://fonts.gstatic.com/s/pressstart2p/v15/e3t4euO8T-267oIAQAu6jDQyK3nVivM.ttf",
            previewTextBengali = "৮-বিট ক্লাসিক আর্কেড ও নস্টালজিক গেমের অনুভূতি।",
            previewTextEnglish = "Bitmap font based on 1980s arcade games.",
            tags = listOf("Pixel", "Retro", "8-Bit", "Gaming")
        ),
        FontModel(
            id = "caveat",
            name = "Caveat",
            nativeName = "ক্যাভিয়েট হ্যান্ডরাইটিং",
            category = FontCategory.HANDWRITING,
            author = "Pablo Impallari",
            languageSupport = "Latin",
            isBundled = false,
            downloadUrl = "https://fonts.gstatic.com/s/caveat/v18/Wnz4H6tF153q0S3Ljg76FX59Vp8q.ttf",
            previewTextBengali = "স্বাভাবিক খাতা-কলমের হাতের লেখা।",
            previewTextEnglish = "Warm, casual handwriting with open counters and legibility.",
            tags = listOf("Handwriting", "Notes", "Friendly")
        )
    )

    fun getAllFonts(): List<FontModel> {
        val customFonts = loadSavedCustomFonts()
        val all = mutableListOf<FontModel>()
        
        // Add catalog fonts with updated local download status
        for (item in initialCatalog) {
            val localFile = File(fontsDir, "${item.id}.ttf")
            if (localFile.exists() && localFile.length() > 0) {
                all.add(item.copy(isDownloaded = true, localFilePath = localFile.absolutePath))
            } else {
                all.add(item)
            }
        }

        // Add custom imported fonts
        all.addAll(customFonts)
        return all
    }

    suspend fun downloadFont(font: FontModel, onProgress: ((Int) -> Unit)? = null): FontModel = withContext(Dispatchers.IO) {
        if (font.downloadUrl == null) return@withContext font
        val destFile = File(fontsDir, "${font.id}.ttf")
        
        val url = URL(font.downloadUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.requestMethod = "GET"
        connection.connect()

        if (connection.responseCode in 200..299) {
            val totalBytes = connection.contentLength
            var downloadedBytes = 0
            connection.inputStream.use { input ->
                FileOutputStream(destFile).use { output ->
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        if (totalBytes > 0) {
                            val percent = (downloadedBytes * 100) / totalBytes
                            onProgress?.invoke(percent)
                        }
                    }
                }
            }
            font.copy(
                isDownloaded = true,
                localFilePath = destFile.absolutePath,
                fileSizeBytes = destFile.length()
            )
        } else {
            throw Exception("HTTP Error: ${connection.responseCode} ${connection.responseMessage}")
        }
    }

    suspend fun downloadFromCustomUrl(urlStr: String, customName: String): FontModel = withContext(Dispatchers.IO) {
        val safeId = "web_" + System.currentTimeMillis()
        val destFile = File(fontsDir, "${safeId}.ttf")

        val url = URL(urlStr)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.requestMethod = "GET"
        connection.connect()

        if (connection.responseCode in 200..299) {
            connection.inputStream.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }

            val font = FontModel(
                id = safeId,
                name = customName.ifBlank { "Custom Web Font" },
                nativeName = customName.ifBlank { "কাস্টম ওয়েব ফন্ট" },
                category = FontCategory.CUSTOM,
                author = "Online URL Download",
                languageSupport = "Custom TTF",
                isBundled = false,
                downloadUrl = urlStr,
                localFilePath = destFile.absolutePath,
                isDownloaded = true,
                fileSizeBytes = destFile.length(),
                tags = listOf("Custom", "Web Download")
            )

            saveCustomFont(font)
            font
        } else {
            throw Exception("Failed to download from URL: HTTP ${connection.responseCode}")
        }
    }

    suspend fun importFontFromUri(uri: Uri, displayName: String): FontModel = withContext(Dispatchers.IO) {
        val safeId = "imported_" + System.currentTimeMillis()
        val destFile = File(fontsDir, "${safeId}.ttf")

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        } ?: throw Exception("Cannot open file from selected URI")

        val cleanName = displayName.replace(Regex("\\.(ttf|otf)$", RegexOption.IGNORE_CASE), "")

        val font = FontModel(
            id = safeId,
            name = cleanName.ifBlank { "Imported Custom Font" },
            nativeName = cleanName.ifBlank { "ইমপোর্ট করা ফন্ট" },
            category = FontCategory.CUSTOM,
            author = "Local Phone Storage",
            languageSupport = "User Imported Font",
            isBundled = false,
            localFilePath = destFile.absolutePath,
            isDownloaded = true,
            fileSizeBytes = destFile.length(),
            tags = listOf("Imported", "Local", "Custom")
        )

        saveCustomFont(font)
        font
    }

    private fun saveCustomFont(font: FontModel) {
        val existing = loadSavedCustomFonts().toMutableList()
        existing.removeAll { it.id == font.id }
        existing.add(0, font)

        val array = JSONArray()
        for (f in existing) {
            val obj = JSONObject().apply {
                put("id", f.id)
                put("name", f.name)
                put("nativeName", f.nativeName)
                put("category", f.category.name)
                put("author", f.author)
                put("languageSupport", f.languageSupport)
                put("localFilePath", f.localFilePath)
                put("downloadUrl", f.downloadUrl ?: "")
                put("fileSizeBytes", f.fileSizeBytes)
            }
            array.put(obj)
        }
        prefs.edit().putString("custom_fonts_json", array.toString()).apply()
    }

    private fun loadSavedCustomFonts(): List<FontModel> {
        val raw = prefs.getString("custom_fonts_json", null) ?: return emptyList()
        val list = mutableListOf<FontModel>()
        try {
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val path = obj.optString("localFilePath", "")
                if (path.isNotEmpty() && File(path).exists()) {
                    list.add(
                        FontModel(
                            id = obj.getString("id"),
                            name = obj.getString("name"),
                            nativeName = obj.optString("nativeName", obj.getString("name")),
                            category = FontCategory.valueOf(obj.optString("category", FontCategory.CUSTOM.name)),
                            author = obj.optString("author", "Custom"),
                            languageSupport = obj.optString("languageSupport", "Multilingual"),
                            isBundled = false,
                            localFilePath = path,
                            downloadUrl = obj.optString("downloadUrl").ifEmpty { null },
                            isDownloaded = true,
                            fileSizeBytes = obj.optLong("fileSizeBytes", 0L),
                            tags = listOf("Custom", "Imported")
                        )
                    )
                }
            }
        } catch (_: Exception) {}
        return list
    }
}
