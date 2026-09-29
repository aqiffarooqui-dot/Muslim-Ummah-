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

    @Query("SELECT * FROM bookmarks WHERE type = :type ORDER BY timestamp DESC")
    fun getBookmarksByType(type: String): Flow<List<Bookmark>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE type = :type AND referenceId = :refId AND secondaryId = :secId)")
    fun isBookmarked(type: String, refId: Int, secId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: Bookmark): Long

    @Query("DELETE FROM bookmarks WHERE type = :type AND referenceId = :refId AND secondaryId = :secId")
    suspend fun deleteBookmark(type: String, refId: Int, secId: Int)
}
