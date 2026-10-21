package com.example.generator

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.model.FontModel
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object FontModuleGenerator {

    /**
     * Extracts font bytes from bundled raw resource or local file path
     */
    fun getFontInputStream(context: Context, font: FontModel): InputStream? {
        return if (font.resId != null) {
            context.resources.openRawResource(font.resId)
        } else if (font.localFilePath != null) {
            val file = File(font.localFilePath)
            if (file.exists()) file.inputStream() else null
        } else {
            null
        }
    }

    /**
     * Exports raw TTF font file to cache directory and returns a shareable File
     */
    fun exportTtfFile(context: Context, font: FontModel): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeName = font.name.replace(" ", "_").lowercase()
        val targetFile = File(exportDir, "${safeName}.ttf")

        val fontStream = getFontInputStream(context, font) ?: ByteArrayInputStream(ByteArray(0))
        fontStream.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        return targetFile
    }

    /**
     * Generates a fully compliant Magisk / KernelSU / APatch Universal Flashable Zip
     * That replaces system fonts across any rooted Android device!
     */
    fun generateMagiskModule(context: Context, font: FontModel): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeName = font.name.replace(" ", "_").lowercase()
        val zipFile = File(exportDir, "Mukul_Font_Magisk_${safeName}_module.zip")

        val fontBytes = getFontInputStream(context, font)?.use { it.readBytes() } ?: ByteArray(0)

        val moduleProp = """
            id=mukulfont_${safeName}
            name=Mukul Font: ${font.name}
            version=v1.0
            versionCode=100
            author=Mukul Font Universal Engine
            description=Custom system typography replacement with ${font.name}. Full Bengali and Latin font rendering across all apps.
        """.trimIndent()

        val customizeSh = """
            #!/system/bin/sh
            ui_print "****************************************"
            ui_print "     Mukul Font Custom Font Changer     "
            ui_print "    Installing font: ${font.name}      "
            ui_print "****************************************"
            ui_print "- Setting up system fonts overlay..."
            set_perm_recursive ${'$'}MODPATH/system 0 0 0755 0644
            ui_print "- Font installed successfully! Please reboot your device."
        """.trimIndent()

        val updaterScript = "#MAGISK\n"
        val updateBinary = """
            #!/sbin/sh
            # Magisk dummy installer
            exit 0
        """.trimIndent()

        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            // module.prop
            addFileToZip(zos, "module.prop", moduleProp.toByteArray(StandardCharsets.UTF_8))
            // customize.sh
            addFileToZip(zos, "customize.sh", customizeSh.toByteArray(StandardCharsets.UTF_8))
            // META-INF
            addFileToZip(zos, "META-INF/com/google/android/updater-script", updaterScript.toByteArray(StandardCharsets.UTF_8))
            addFileToZip(zos, "META-INF/com/google/android/update-binary", updateBinary.toByteArray(StandardCharsets.UTF_8))

            // Replace standard Android fallback fonts
            if (fontBytes.isNotEmpty()) {
                addFileToZip(zos, "system/fonts/Roboto-Regular.ttf", fontBytes)
                addFileToZip(zos, "system/fonts/Roboto-Medium.ttf", fontBytes)
                addFileToZip(zos, "system/fonts/Roboto-Bold.ttf", fontBytes)
                addFileToZip(zos, "system/fonts/NotoSansBengali-Regular.ttf", fontBytes)
                addFileToZip(zos, "system/fonts/NotoSansBengali-Bold.ttf", fontBytes)
                addFileToZip(zos, "system/fonts/NotoSansBengaliUI-Regular.ttf", fontBytes)
                addFileToZip(zos, "system/fonts/NotoSansBengaliUI-Bold.ttf", fontBytes)
            }
        }

        return zipFile
    }

    /**
     * Generates a Xiaomi MIUI / HyperOS MTZ Theme Font archive
     */
    fun generateXiaomiMtz(context: Context, font: FontModel): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeName = font.name.replace(" ", "_").lowercase()
        val mtzFile = File(exportDir, "Mukul_Font_MIUI_${safeName}.mtz")

        val fontBytes = getFontInputStream(context, font)?.use { it.readBytes() } ?: ByteArray(0)

        val descriptionXml = """
            <?xml version="1.0" encoding="utf-8"?>
            <theme version="1.0">
                <title>${font.name} (Mukul Font)</title>
                <designer>${font.author}</designer>
                <author>Mukul Font Android</author>
                <version>1.0</version>
                <uiVersion>14</uiVersion>
                <description>Custom font package for Xiaomi / Redmi / POCO MIUI and HyperOS generated by Mukul Font.</description>
            </theme>
        """.trimIndent()

        ZipOutputStream(FileOutputStream(mtzFile)).use { zos ->
            addFileToZip(zos, "description.xml", descriptionXml.toByteArray(StandardCharsets.UTF_8))
            if (fontBytes.isNotEmpty()) {
                addFileToZip(zos, "fonts/regular.ttf", fontBytes)
                addFileToZip(zos, "fonts/bold.ttf", fontBytes)
            }
        }

        return mtzFile
    }

    /**
     * Generates an ITZ font container for Vivo OriginOS/FuntouchOS
     */
    fun generateVivoItz(context: Context, font: FontModel): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeName = font.name.replace(" ", "_").lowercase()
        val itzFile = File(exportDir, "FontMorph_Vivo_${safeName}.itz")

        val fontBytes = getFontInputStream(context, font)?.use { it.readBytes() } ?: ByteArray(0)

        ZipOutputStream(FileOutputStream(itzFile)).use { zos ->
            addFileToZip(zos, "font.ttf", fontBytes)
        }

        return itzFile
    }

    private fun addFileToZip(zos: ZipOutputStream, path: String, content: ByteArray) {
        val entry = ZipEntry(path)
        zos.putNextEntry(entry)
        zos.write(content)
        zos.closeEntry()
    }

    fun getShareableUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
