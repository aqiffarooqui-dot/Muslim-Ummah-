package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.AiMode
import com.example.data.ai.MuslimUmmahAiEngine
import com.example.data.model.CalculationMethod
import com.example.data.model.HijriCalendarHelper
import com.example.data.model.PrayerCalculator
import com.example.data.model.QiblaCalculator
import com.example.data.model.QuranRepository
import com.example.data.subscription.SubscriptionManager
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
    fun `read string from context verifies Muslim Ummah app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Muslim Ummah", appName)
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
        val bearing = QiblaCalculator.calculateQiblaDirection(51.5074, -0.1278)
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

    @Test
    fun `quran repository contains all 30 Paras with mapping`() {
        val paras = QuranRepository.ALL_PARAS
        assertEquals(30, paras.size)
        assertEquals(1, paras.first().number)
        assertEquals(30, paras.last().number)
        assertTrue(paras.first().surahsInPara.contains(1))
        assertTrue(paras.last().surahsInPara.contains(114))
    }

    @Test
    fun `muslim ummah ai provides verified citations for witr prayer`() {
        val answer = MuslimUmmahAiEngine.answerQuery("What is the method of Witr prayer?", AiMode.GENERAL)
        assertNotNull(answer)
        assertTrue(answer.quranCitations.isNotEmpty())
        assertTrue(answer.hadithCitations.isNotEmpty())
        assertTrue(answer.scholarlyOpinions != null)
    }

    @Test
    fun `subscription manager handles admin entitlement correctly`() {
        SubscriptionManager.updateEntitlementForUser("aqiffarooqui@gmail.com", true, "Lifetime VIP", null)
        val entitlement = SubscriptionManager.entitlementFlow.value
        assertTrue(entitlement.isPremiumActive)
        assertTrue(entitlement.isAdFree)
        assertTrue(entitlement.canAccessAiAssistant)
        assertTrue(entitlement.canAccessOfflineDownloads)
    }
}
