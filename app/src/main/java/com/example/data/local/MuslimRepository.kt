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

    suspend fun insertBookmark(bookmark: Bookmark): Long {
        return dao.insertBookmark(bookmark)
    }

    // Users and Subscriptions
    val allUsers: Flow<List<AppUser>> = dao.getAllUsers()

    suspend fun getUserByEmail(email: String): AppUser? {
        return dao.getUserByEmail(email)
    }

    suspend fun saveUser(user: AppUser) {
        dao.insertUser(user)
    }

    suspend fun updateSubscription(email: String, isPremium: Boolean, planType: String, expiresAt: Long?) {
        dao.updateSubscription(email, isPremium, planType, expiresAt)
    }

    suspend fun deleteUser(email: String) {
        dao.deleteUser(email)
    }

    // Quran Reading Position & Notes
    val quranReadingPosition: Flow<QuranReadingPosition?> = dao.getQuranReadingPosition()
    val allQuranNotes: Flow<List<QuranNote>> = dao.getAllQuranNotes()

    suspend fun saveQuranReadingPosition(pos: QuranReadingPosition) {
        dao.saveQuranReadingPosition(pos)
    }

    suspend fun addQuranNote(note: QuranNote): Long {
        return dao.insertQuranNote(note)
    }

    suspend fun deleteQuranNote(id: Long) {
        dao.deleteQuranNote(id)
    }

    // Hadith Reading Position & Notes
    val hadithReadingPosition: Flow<HadithReadingPosition?> = dao.getHadithReadingPosition()
    val allHadithNotes: Flow<List<HadithNote>> = dao.getAllHadithNotes()

    suspend fun saveHadithReadingPosition(pos: HadithReadingPosition) {
        dao.saveHadithReadingPosition(pos)
    }

    suspend fun addHadithNote(note: HadithNote): Long {
        return dao.insertHadithNote(note)
    }

    suspend fun deleteHadithNote(id: Long) {
        dao.deleteHadithNote(id)
    }

    // Khatam Progress
    val khatamProgress: Flow<KhatamProgress?> = dao.getKhatamProgress()

    suspend fun saveKhatamProgress(progress: KhatamProgress) {
        dao.saveKhatamProgress(progress)
    }

    suspend fun getAllBookmarksList(): List<Bookmark> = dao.getAllBookmarksList()
    suspend fun getQuranReadingPositionDirect(): QuranReadingPosition? = dao.getQuranReadingPositionDirect()
    suspend fun getAllQuranNotesList(): List<QuranNote> = dao.getAllQuranNotesList()
    suspend fun getHadithReadingPositionDirect(): HadithReadingPosition? = dao.getHadithReadingPositionDirect()
    suspend fun getAllHadithNotesList(): List<HadithNote> = dao.getAllHadithNotesList()

    suspend fun seedInitialUsersIfNeeded() {
        val adminUser = AppUser(
            email = "aqiffarooqui@gmail.com",
            displayName = "Aqif Farooqui",
            photoUrl = "",
            isPremium = true,
            planType = "1 Year",
            role = "ADMIN",
            registeredDate = System.currentTimeMillis() - (90L * 24 * 3600 * 1000),
            notes = "Super Administrator"
        )
        dao.insertUser(adminUser)
    }
}
