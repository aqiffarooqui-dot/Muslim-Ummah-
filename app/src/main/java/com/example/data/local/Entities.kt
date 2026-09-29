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
    val type: String, // "QURAN_SURAH", "QURAN_AYAH", "DUA"
    val referenceId: Int, // Surah number, Ayah number, or Dua id
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
    val planType: String = "Free", // "Free", "Monthly Pro", "Annual Pro", "Lifetime VIP"
    val role: String = "USER", // "ADMIN" or "USER"
    val registeredDate: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,
    val notes: String = ""
)

