package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MuslimDao {
    // Tasbih
    @Query("SELECT * FROM tasbih_records ORDER BY updatedAt DESC")
    fun getAllTasbihRecords(): Flow<List<TasbihRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasbih(record: TasbihRecord): Long

    @Query("DELETE FROM tasbih_records WHERE id = :id")
    suspend fun deleteTasbih(id: Long)

    @Query("DELETE FROM tasbih_records")
    suspend fun clearAllTasbih()

    // Prayer tracker
    @Query("SELECT * FROM prayer_tracker WHERE dateKey = :dateKey LIMIT 1")
    fun getPrayerRecord(dateKey: String): Flow<PrayerTrackerRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerRecord(record: PrayerTrackerRecord)

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    suspend fun getAllBookmarksList(): List<Bookmark>

    @Query("SELECT * FROM bookmarks WHERE type = :type ORDER BY timestamp DESC")
    fun getBookmarksByType(type: String): Flow<List<Bookmark>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE type = :type AND referenceId = :refId AND secondaryId = :secId)")
    fun isBookmarked(type: String, refId: Int, secId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    @Query("""
        UPDATE bookmarks SET title = :title, subtitle = :subtitle, timestamp = :timestamp
        WHERE type = :type AND referenceId = :refId AND secondaryId = :secId
    """)
    suspend fun updateBookmark(type: String, refId: Int, secId: Int, title: String, subtitle: String, timestamp: Long)

    @Query("DELETE FROM bookmarks WHERE type = :type AND referenceId = :refId AND secondaryId = :secId")
    suspend fun deleteBookmark(type: String, refId: Int, secId: Int)

    // Users & Subscriptions
    @Query("SELECT * FROM app_users ORDER BY registeredDate DESC")
    fun getAllUsers(): Flow<List<AppUser>>

    @Query("SELECT * FROM app_users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): AppUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AppUser)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsersIfNotExist(users: List<AppUser>)

    @Query("UPDATE app_users SET isPremium = :isPremium, planType = :planType, expiresAt = :expiresAt WHERE email = :email")
    suspend fun updateSubscription(email: String, isPremium: Boolean, planType: String, expiresAt: Long?)

    @Query("DELETE FROM app_users WHERE email = :email")
    suspend fun deleteUser(email: String)

    // Quran Reading Position
    @Query("SELECT * FROM quran_reading_position WHERE id = 1 LIMIT 1")
    fun getQuranReadingPosition(): Flow<QuranReadingPosition?>

    @Query("SELECT * FROM quran_reading_position WHERE id = 1 LIMIT 1")
    suspend fun getQuranReadingPositionDirect(): QuranReadingPosition?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuranReadingPosition(position: QuranReadingPosition)

    // Quran Notes
    @Query("SELECT * FROM quran_notes ORDER BY timestamp DESC")
    fun getAllQuranNotes(): Flow<List<QuranNote>>

    @Query("SELECT * FROM quran_notes ORDER BY timestamp DESC")
    suspend fun getAllQuranNotesList(): List<QuranNote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranNote(note: QuranNote): Long

    @Query("""
        UPDATE quran_notes SET surahName = :surahName, noteText = :noteText, timestamp = :timestamp
        WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber
    """)
    suspend fun updateQuranNote(surahNumber: Int, ayahNumber: Int, surahName: String, noteText: String, timestamp: Long)

    @Query("DELETE FROM quran_notes WHERE id = :id")
    suspend fun deleteQuranNote(id: Long)

    // Hadith Reading Position
    @Query("SELECT * FROM hadith_reading_position WHERE id = 1 LIMIT 1")
    fun getHadithReadingPosition(): Flow<HadithReadingPosition?>

    @Query("SELECT * FROM hadith_reading_position WHERE id = 1 LIMIT 1")
    suspend fun getHadithReadingPositionDirect(): HadithReadingPosition?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHadithReadingPosition(position: HadithReadingPosition)

    // Hadith Notes
    @Query("SELECT * FROM hadith_notes ORDER BY timestamp DESC")
    fun getAllHadithNotes(): Flow<List<HadithNote>>

    @Query("SELECT * FROM hadith_notes ORDER BY timestamp DESC")
    suspend fun getAllHadithNotesList(): List<HadithNote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHadithNote(note: HadithNote): Long

    @Query("""
        UPDATE hadith_notes SET noteText = :noteText, timestamp = :timestamp
        WHERE bookId = :bookId AND hadithId = :hadithId
    """)
    suspend fun updateHadithNote(bookId: String, hadithId: Int, noteText: String, timestamp: Long)

    @Query("DELETE FROM hadith_notes WHERE id = :id")
    suspend fun deleteHadithNote(id: Long)

    // Khatam Progress
    @Query("SELECT * FROM khatam_progress WHERE id = 1 LIMIT 1")
    fun getKhatamProgress(): Flow<KhatamProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveKhatamProgress(progress: KhatamProgress)
}
