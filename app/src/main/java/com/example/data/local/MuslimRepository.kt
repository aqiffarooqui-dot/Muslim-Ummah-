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

    suspend fun seedInitialUsersIfNeeded() {
        val now = System.currentTimeMillis()
        val oneMonth = 30L * 24 * 3600 * 1000
        val oneYear = 365L * 24 * 3600 * 1000

        val initialList = listOf(
            AppUser(
                email = "aqiffarooqui@gmail.com",
                displayName = "Aqif Farooqui",
                photoUrl = "",
                isPremium = true,
                planType = "Lifetime VIP",
                role = "ADMIN",
                registeredDate = now - (60L * 24 * 3600 * 1000),
                expiresAt = null,
                notes = "Owner & System Administrator"
            ),
            AppUser(
                email = "fatima.zahra@gmail.com",
                displayName = "Fatima Zahra",
                isPremium = false,
                planType = "Free",
                role = "USER",
                registeredDate = now - (3L * 24 * 3600 * 1000),
                notes = "Active daily reader"
            ),
            AppUser(
                email = "omar.khattab@gmail.com",
                displayName = "Omar Al-Khattab",
                isPremium = true,
                planType = "Annual Pro",
                role = "USER",
                registeredDate = now - (15L * 24 * 3600 * 1000),
                expiresAt = now + (350L * 24 * 3600 * 1000),
                notes = "Annual Subscriber via Google Play"
            ),
            AppUser(
                email = "aisha.malik@gmail.com",
                displayName = "Aisha Malik",
                isPremium = true,
                planType = "Monthly Pro",
                role = "USER",
                registeredDate = now - (8L * 24 * 3600 * 1000),
                expiresAt = now + (22L * 24 * 3600 * 1000),
                notes = "Monthly subscriber"
            ),
            AppUser(
                email = "zayd.ansari@outlook.com",
                displayName = "Zayd Ansari",
                isPremium = false,
                planType = "Free",
                role = "USER",
                registeredDate = now - (20L * 24 * 3600 * 1000),
                notes = "Exploring Quran and Duas"
            ),
            AppUser(
                email = "tariq.mansoor@gmail.com",
                displayName = "Tariq Mansoor",
                isPremium = false,
                planType = "Free",
                role = "USER",
                registeredDate = now - (2L * 24 * 3600 * 1000),
                notes = "New sign-up"
            ),
            AppUser(
                email = "bilal.habashi@gmail.com",
                displayName = "Bilal Habashi",
                isPremium = true,
                planType = "Lifetime VIP",
                role = "USER",
                registeredDate = now - (90L * 24 * 3600 * 1000),
                expiresAt = null,
                notes = "Early lifetime backer"
            )
        )
        dao.insertUsersIfNotExist(initialList)
    }
}
