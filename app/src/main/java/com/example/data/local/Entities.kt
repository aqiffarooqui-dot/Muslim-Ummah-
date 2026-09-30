package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasbih_records")
data class TasbihRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dhikrText: String,
    val arabicText: String,
    val count: Int,
    val target: Int,
    val laps: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "prayer_tracker")
data class PrayerTrackerRecord(
    @PrimaryKey val dateKey: String, // Format: yyyy-MM-dd
    val fajrDone: Boolean = false,
    val dhuhrDone: Boolean = false,
    val asrDone: Boolean = false,
    val maghribDone: Boolean = false,
    val ishaDone: Boolean = false
)

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "QURAN_SURAH", "QURAN_AYAH", "HADITH", "DUA"
    val referenceId: Int, // Surah number, Hadith ID, or Dua ID
    val secondaryId: Int = 0, // Ayah number if type is QURAN_AYAH
    val title: String,
    val subtitle: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_users")
data class AppUser(
    @PrimaryKey val email: String,
    val displayName: String,
    val photoUrl: String = "",
    val isPremium: Boolean = false,
    val planType: String = "Free", // "Free", "7 Days", "1 Month", "3 Months", "9 Months", "1 Year"
    val role: String = "USER", // "ADMIN" or "USER"
    val registeredDate: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,
    val notes: String = "",
    val uid: String = ""
)

@Entity(tableName = "quran_reading_position")
data class QuranReadingPosition(
    @PrimaryKey val id: Int = 1,
    val surahNumber: Int = 1,
    val ayahNumber: Int = 1,
    val surahName: String = "Al-Fatihah",
    val paraNumber: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quran_notes")
data class QuranNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "hadith_reading_position")
data class HadithReadingPosition(
    @PrimaryKey val id: Int = 1,
    val bookId: String = "bukhari",
    val bookName: String = "Sahih al-Bukhari",
    val chapterName: String = "Revelation",
    val hadithId: Int = 1,
    val hadithNumber: String = "Hadith 1",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "hadith_notes")
data class HadithNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: String,
    val hadithId: Int,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "khatam_progress")
data class KhatamProgress(
    @PrimaryKey val id: Int = 1,
    val targetDateMillis: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000), // 30 days
    val totalPages: Int = 604,
    val completedPages: Int = 0,
    val dailyTargetPages: Int = 20,
    val isCompleted: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
