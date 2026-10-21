package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.FontRepository
import com.example.generator.FontModuleGenerator
import com.example.model.DeviceOem
import com.example.model.FontCategory
import com.example.model.FontModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Mukul font", appName)
    }

    @Test
    fun `font repository loads bundled fonts`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = FontRepository(context)
        val fonts = repository.getAllFonts()
        assertTrue("Fonts should not be empty", fonts.isNotEmpty())
        assertTrue("Should contain Bengali fonts", fonts.any { it.category == FontCategory.BENGALI })
    }

    @Test
    fun `font module generator creates zip module`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dummyFont = FontModel(
            id = "test_font",
            name = "Test Font",
            category = FontCategory.SANS,
            isBundled = false,
            resId = null
        )
        val zipFile = FontModuleGenerator.generateMagiskModule(context, dummyFont)
        assertNotNull(zipFile)
        assertTrue(zipFile.exists())
    }
}
