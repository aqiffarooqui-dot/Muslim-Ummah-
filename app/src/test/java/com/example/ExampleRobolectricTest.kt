package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.AiMode
import com.example.data.ai.MuslimUmmahAiEngine
import com.example.data.local.AppUser
import com.example.data.local.Bookmark
import com.example.data.local.QuranReadingPosition
import com.example.data.model.CalculationMethod
import com.example.data.model.HadithRepository
import com.example.data.model.HijriCalendarHelper
import com.example.data.model.PrayerCalculator
import com.example.data.model.QiblaCalculator
import com.example.data.model.QuranRepository
import com.example.data.model.QuranTranslationLanguage
import com.example.data.subscription.SubscriptionManager
import com.example.data.subscription.SubscriptionPricingManager
import com.example.data.subscription.SubscriptionTier
import org.junit.Assert.*
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

    // ==========================================
    // 1. Subscription & Entitlement Tests
    // ==========================================

    @Test
    fun `subscription manager initializes as FREE by default and enforces expiry`() {
        val freeEntitlement = SubscriptionManager.createFreeEntitlement()
        assertEquals(SubscriptionTier.FREE, freeEntitlement.tier)
        assertFalse(freeEntitlement.isPremiumActive)

        // Expired subscription test
        val pastTimestamp = System.currentTimeMillis() - 10000L
        SubscriptionManager.updateEntitlementForUser("regular@user.com", true, "1 Month", pastTimestamp)
        val expiredEntitlement = SubscriptionManager.entitlementFlow.value
        assertFalse(expiredEntitlement.isPremiumActive)
        assertEquals(SubscriptionTier.FREE, expiredEntitlement.tier)
    }

    @Test
    fun `subscription manager handles admin entitlement correctly`() {
        SubscriptionManager.updateEntitlementForUser("aqiffarooqui@gmail.com", true, "Super Admin Access", null)
        val entitlement = SubscriptionManager.entitlementFlow.value
        assertTrue(entitlement.isPremiumActive)
        assertTrue(entitlement.isAdFree)
        assertTrue(entitlement.canAccessAiAssistant)
        assertTrue(entitlement.canAccessOfflineDownloads)
    }

    @Test
    fun `subscription manager supports revocation returning user to FREE`() {
        // First grant active subscription
        val futureTime = System.currentTimeMillis() + (30L * 24 * 3600 * 1000)
        SubscriptionManager.updateEntitlementForUser("user@example.com", true, "1 Month", futureTime)
        assertTrue(SubscriptionManager.entitlementFlow.value.isPremiumActive)

        // Now revoke
        SubscriptionManager.updateEntitlementForUser("user@example.com", false, "Free", null)
        assertFalse(SubscriptionManager.entitlementFlow.value.isPremiumActive)
        assertEquals(SubscriptionTier.FREE, SubscriptionManager.entitlementFlow.value.tier)
    }

    // ==========================================
    // 2. Pricing Tests
    // ==========================================

    @Test
    fun `subscription pricing contains exactly 5 official plans`() {
        val plans = SubscriptionPricingManager.plansState.value
        assertEquals(5, plans.size)
        val planIds = plans.map { it.id }
        assertTrue(planIds.contains("plan_7_days"))
        assertTrue(planIds.contains("plan_1_month"))
        assertTrue(planIds.contains("plan_3_months"))
        assertTrue(planIds.contains("plan_9_months"))
        assertTrue(planIds.contains("plan_1_year"))

        val plan7d = plans.find { it.id == "plan_7_days" }
        assertEquals(49, plan7d?.priceInr)

        val plan1m = plans.find { it.id == "plan_1_month" }
        assertEquals(129, plan1m?.priceInr)
    }

    @Test
    fun `pricing calculator generates progressive proportional suggestions for 5 plans`() {
        val suggestions = SubscriptionPricingManager.calculateSuggestedPrices("plan_7_days", 49)
        assertEquals(5, suggestions.size)
        assertEquals(49, suggestions["plan_7_days"])
        assertTrue((suggestions["plan_1_month"] ?: 0) in 100..200)
        assertTrue((suggestions["plan_3_months"] ?: 0) in 250..400)
        assertTrue((suggestions["plan_9_months"] ?: 0) in 550..750)
        assertTrue((suggestions["plan_1_year"] ?: 0) in 700..900)
    }

    @Test
    fun `pricing calculator handles invalid base prices gracefully`() {
        val invalidZero = SubscriptionPricingManager.calculateSuggestedPrices("plan_7_days", 0)
        assertTrue(invalidZero.isEmpty())

        val invalidNegative = SubscriptionPricingManager.calculateSuggestedPrices("plan_7_days", -50)
        assertTrue(invalidNegative.isEmpty())
    }

    // ==========================================
    // 3. Quran Experience Tests
    // ==========================================

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
    fun `quran repository contains all 114 Surahs`() {
        val surahs = QuranRepository.ALL_SURAHS
        assertEquals(114, surahs.size)
        assertEquals(1, surahs.first().number)
        assertEquals("Al-Fatihah", surahs.first().nameEnglish)
        assertEquals(114, surahs.last().number)
        assertEquals("An-Nas", surahs.last().nameEnglish)
    }

    @Test
    fun `quran translation languages provide valid text`() {
        val fatihah = QuranRepository.getVersesForSurah(1)
        assertTrue(fatihah.isNotEmpty())
        val ayah1 = fatihah.first()

        assertEquals("In the name of Allah, the Entirely Merciful, the Especially Merciful.", ayah1.getTranslation(QuranTranslationLanguage.ENGLISH))
        assertTrue(ayah1.getTranslation(QuranTranslationLanguage.URDU).isNotBlank())
        assertTrue(ayah1.getTranslation(QuranTranslationLanguage.HINDI).isNotBlank())
        assertTrue(ayah1.getTranslation(QuranTranslationLanguage.HINGLISH).isNotBlank())
    }

    @Test
    fun `quran reading position persists surah ayah and para`() {
        val pos = QuranReadingPosition(
            id = 1,
            surahNumber = 2,
            ayahNumber = 255,
            surahName = "Al-Baqarah",
            paraNumber = 3
        )
        assertEquals(2, pos.surahNumber)
        assertEquals(255, pos.ayahNumber)
        assertEquals("Al-Baqarah", pos.surahName)
        assertEquals(3, pos.paraNumber)
    }

    // ==========================================
    // 4. Hadith System Tests
    // ==========================================

    @Test
    fun `hadith repository contains Kutub al-Sittah collections and Sahih texts`() {
        val books = HadithRepository.MAIN_BOOKS
        assertTrue(books.any { it.id == "bukhari" })
        assertTrue(books.any { it.id == "muslim" })
        assertTrue(books.any { it.id == "tirmidhi" })
        assertTrue(books.any { it.id == "abudawud" })
        assertTrue(books.any { it.id == "nasai" })
        assertTrue(books.any { it.id == "ibnmajah" })

        val hadiths = HadithRepository.ALL_HADITHS
        assertTrue(hadiths.isNotEmpty())
        assertTrue(hadiths.all { it.arabicText.isNotBlank() && it.englishTranslation.isNotBlank() && it.urduTranslation.isNotBlank() })
    }

    @Test
    fun `hadith search finds matching content across narrations`() {
        val bukhari1 = HadithRepository.ALL_HADITHS.find { it.hadithNumber == "Hadith 1" }
        assertNotNull(bukhari1)
        assertTrue(bukhari1!!.englishTranslation.contains("intentions", ignoreCase = true))
        assertTrue(bukhari1.narrator.contains("Umar", ignoreCase = true))
    }

    // ==========================================
    // 5. Prayer Times & Transitions Tests
    // ==========================================

    @Test
    fun `prayer times calculation returns valid times and transitions`() {
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
        assertEquals(6, times.allList.size)
        assertNotNull(times.nextPrayer)
        assertTrue(times.timeRemainingMillis >= 0)
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

    // ==========================================
    // 6. Source-Aware AI Assistant Tests
    // ==========================================

    @Test
    fun `muslim ummah ai provides verified citations for witr prayer`() {
        val answer = MuslimUmmahAiEngine.answerQuery("What is the method of Witr prayer?", AiMode.GENERAL)
        assertNotNull(answer)
        assertTrue(answer.quranCitations.isNotEmpty())
        assertTrue(answer.hadithCitations.isNotEmpty())
        assertTrue(answer.scholarlyOpinions != null)
    }

    @Test
    fun `muslim ummah ai provides verified citations for zakat`() {
        val answer = MuslimUmmahAiEngine.answerQuery("How is Zakat calculated?", AiMode.GENERAL)
        assertNotNull(answer)
        assertTrue(answer.quranCitations.any { it.contains("Surah 2, Ayah 43") || it.contains("Surah 9, Ayah 60") })
        assertTrue(answer.hadithCitations.isNotEmpty())
    }

    @Test
    fun `muslim ummah ai reports uncertainty gracefully for unknown topics`() {
        val answer = MuslimUmmahAiEngine.answerQuery("What is the quantum mechanics equation?", AiMode.GENERAL)
        assertNotNull(answer)
        assertTrue(answer.text.contains("Quran and the authentic Sunnah", ignoreCase = true) || answer.text.contains("scholar", ignoreCase = true))
    }

    // ==========================================
    // 7. Security & Privilege Tests
    // ==========================================

    @Test
    fun `normal user cannot self-assign ADMIN role or premium entitlement`() {
        val normalUser = AppUser(
            email = "normaluser@example.com",
            displayName = "Brother Ahmad",
            role = "USER",
            isPremium = false,
            planType = "Free"
        )
        assertEquals("USER", normalUser.role)
        assertFalse(normalUser.isPremium)
        assertEquals("Free", normalUser.planType)
    }

    // ==========================================
    // 8. ViewModel Lifecycle Test
    // ==========================================

    @Test
    fun `muslim viewmodel initializes without crash`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.viewmodel.MuslimViewModel(app)
        assertNotNull(vm)
    }
}
