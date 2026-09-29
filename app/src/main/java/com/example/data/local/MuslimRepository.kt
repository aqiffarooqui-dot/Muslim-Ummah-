package com.example.data.local

import kotlinx.coroutines.flow.Flow

class MuslimRepository(private val dao: MuslimDao) {

    val allTasbihRecords: Flow<List<TasbihRecord>> = dao.getAllTasbihRecords()
    val allBookmarks: Flow<List<Bookmark>> = dao.getAllBookmarks()

    suspend fun saveTasbih(record: TasbihRecord): Long {
        return dao.insertTasbih(record)
    }

    suspend fun deleteTasbih(id: Long) {
        dao.deleteTasbih(id)
    }

    suspend fun clearAllTasbih() {
        dao.clearAllTasbih()
    }

    fun getPrayerTracker(dateKey: String): Flow<PrayerTrackerRecord?> {
        return dao.getPrayerRecord(dateKey)
    }

    suspend fun savePrayerTracker(record: PrayerTrackerRecord) {
        dao.insertPrayerRecord(record)
    }

    fun isBookmarked(type: String, refId: Int, secId: Int = 0): Flow<Boolean> {
        return dao.isBookmarked(type, refId, secId)
    }

    suspend fun toggleBookmark(type: String, refId: Int, secId: Int = 0, title: String, subtitle: String, isCurrentlyBookmarked: Boolean) {
        if (isCurrentlyBookmarked) {
            dao.deleteBookmark(type, refId, secId)
        } else {
            dao.insertBookmark(Bookmark(type = type, referenceId = refId, secondaryId = secId, title = title, subtitle = subtitle))
        }
    }
}
