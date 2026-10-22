package com.example

import com.example.data.model.HadithRepository
import com.example.data.model.QuranRepository
import com.example.data.subscription.SubscriptionManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regression checks for the local Islamic-content catalog.
 * These tests validate catalog structure and ensure missing Quran content is
 * never fabricated or relabeled as a different Surah.
 */
class IslamicContentIntegrityTest {
    @Test
    fun quranMetadataContainsExactly114SequentialSurahs() {
        assertEquals((1..114).toList(), QuranRepository.ALL_SURAHS.map { it.number })
    }

    @Test
    fun quranMetadataContainsExactly30SequentialParas() {
        assertEquals((1..30).toList(), QuranRepository.ALL_PARAS.map { it.number })
    }

    @Test
    fun quranVerseResultsNeverClaimAnotherSurahNumber() {
        QuranRepository.ALL_SURAHS.forEach { surah ->
            QuranRepository.getVersesForSurah(surah.number).forEach { ayah ->
                assertEquals(surah.number, ayah.surahNumber)
                assertTrue(ayah.ayahNumber >= 1)
                assertTrue(ayah.ayahNumber <= surah.totalAyahs)
            }
        }
    }

    @Test
    fun invalidSurahNumberReturnsNoVerses() {
        assertTrue(QuranRepository.getVersesForSurah(0).isEmpty())
        assertTrue(QuranRepository.getVersesForSurah(115).isEmpty())
    }

    @Test
    fun premiumCatalogContainsOnlyFourApprovedPlans() {
        assertEquals(
            listOf("plan_7_days", "plan_1_month", "plan_3_months", "plan_9_months"),
            SubscriptionManager.DEFAULT_PLANS.map { it.id }
        )
        assertEquals(listOf(49, 129, 299, 649), SubscriptionManager.DEFAULT_PLANS.map { it.priceInr })
    }

    @Test
    fun hadithBookCatalogContainsTheSixMajorCollections() {
        val ids = HadithRepository.MAIN_BOOKS.map { it.id }.toSet()
        assertTrue(ids.containsAll(setOf("bukhari", "muslim", "tirmidhi", "abudawud", "nasai", "ibnmajah")))
    }

    @Test
    fun hadithRecordsHaveNonBlankSourceFields() {
        HadithRepository.ALL_HADITHS.forEach { hadith ->
            assertTrue("Hadith ID must be positive", hadith.id > 0)
            assertTrue("Book ID must be present", hadith.bookId.isNotBlank())
            assertTrue("Hadith number must be present", hadith.hadithNumber.isNotBlank())
            assertTrue("Arabic text must be present", hadith.arabicText.isNotBlank())
        }
    }
}
