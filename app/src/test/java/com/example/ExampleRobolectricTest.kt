package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CalculationMethod
import com.example.data.model.HijriCalendarHelper
import com.example.data.model.PrayerCalculator
import com.example.data.model.QiblaCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Muslim Pro", appName)
    }

    @Test
    fun `prayer times calculation returns valid times`() {
        val calendar = Calendar.getInstance()
        val times = PrayerCalculator.calculateTimes(
            calendar = calendar,
            latitude = 21.4225,
            longitude = 39.8262,
            timezoneOffsetHours = 3.0,
            method = CalculationMethod.UMM_AL_QURA
        )
        assertNotNull(times.fajr)
        assertNotNull(times.dhuhr)
        assertNotNull(times.asr)
        assertNotNull(times.maghrib)
        assertNotNull(times.isha)
        assertTrue(times.allList.size == 6)
    }

    @Test
    fun `qibla calculation gives correct bearing for London`() {
        // London is approx latitude 51.5074, longitude -0.1278
        val bearing = QiblaCalculator.calculateQiblaDirection(51.5074, -0.1278)
        // Qibla from London is roughly 118-120 degrees (ESE)
        assertTrue(bearing in 115.0..122.0)
    }

    @Test
    fun `hijri calendar helper returns valid year and month`() {
        val hijriDate = HijriCalendarHelper.getHijriDate()
        assertTrue(hijriDate.year >= 1445)
        assertTrue(hijriDate.day in 1..30)
        assertTrue(hijriDate.monthNumber in 1..12)
    }

    @Test
    fun `admin check confirms aqiffarooqui email as super admin`() {
        assertTrue(com.example.ui.util.GoogleAuthManager.isAdminEmail("aqiffarooqui@gmail.com"))
        assertTrue(com.example.ui.util.GoogleAuthManager.isAdminEmail("AqifFarooqui@Gmail.Com"))
        org.junit.Assert.assertFalse(com.example.ui.util.GoogleAuthManager.isAdminEmail("other.user@gmail.com"))
    }

    @Test
    fun `mumbai prayer calculation succeeds with Karachi method`() {
        val calendar = Calendar.getInstance()
        val times = PrayerCalculator.calculateTimes(
            calendar = calendar,
            latitude = 19.0760,
            longitude = 72.8777,
            timezoneOffsetHours = 5.5,
            method = CalculationMethod.KARACHI
        )
        assertNotNull(times.fajr)
        assertNotNull(times.dhuhr)
        assertNotNull(times.asr)
        assertNotNull(times.maghrib)
        assertNotNull(times.isha)
    }

    @Test
    fun `hadith repository contains Kutub al-Sittah collections and Sahih texts`() {
        val books = com.example.data.model.HadithRepository.MAIN_BOOKS
        assertTrue(books.any { it.id == "bukhari" })
        assertTrue(books.any { it.id == "muslim" })
        assertTrue(books.any { it.id == "tirmidhi" })
        assertTrue(books.any { it.id == "abudawud" })
        assertTrue(books.any { it.id == "nasai" })
        assertTrue(books.any { it.id == "ibnmajah" })

        val hadiths = com.example.data.model.HadithRepository.ALL_HADITHS
        assertTrue(hadiths.isNotEmpty())
        assertTrue(hadiths.all { it.arabicText.isNotBlank() && it.englishTranslation.isNotBlank() && it.urduTranslation.isNotBlank() })
    }
}
