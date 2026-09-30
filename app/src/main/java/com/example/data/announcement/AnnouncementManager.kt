package com.example.data.announcement

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class AnnouncementType {
    GENERAL,
    RAMADAN,
    JUMMAH,
    FEATURE,
    MAINTENANCE
}

data class AppAnnouncement(
    val id: String = "announcement_default",
    val title: String = "Welcome to Muslim Ummah",
    val message: String = "May Allah bless your spiritual journey. Explore the Holy Quran, authentic Hadiths, and accurate prayer times.",
    val type: AnnouncementType = AnnouncementType.GENERAL,
    val isActive: Boolean = true,
    val showVersionInfo: Boolean = false,
    val currentVersion: String = "1.0.0",
    val nextVersion: String = "1.1.0",
    val ctaText: String? = null,
    val ctaUrl: String? = null,
    val startDateMillis: Long? = null,
    val endDateMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)

object AnnouncementManager {
    private const val TAG = "AnnouncementManager"
    private const val PREFS_NAME = "muslim_ummah_announcements"
    private const val KEY_LAST_DISMISSED_ID = "last_dismissed_announcement_id"

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private val _activeAnnouncement = MutableStateFlow<AppAnnouncement?>(null)
    val activeAnnouncement: StateFlow<AppAnnouncement?> = _activeAnnouncement.asStateFlow()

    private val _allAnnouncements = MutableStateFlow<List<AppAnnouncement>>(emptyList())
    val allAnnouncements: StateFlow<List<AppAnnouncement>> = _allAnnouncements.asStateFlow()

    init {
        listenToAnnouncements()
    }

    private fun listenToAnnouncements() {
        try {
            firestore.collection("announcements")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Error listening to announcements: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = mutableListOf<AppAnnouncement>()
                        for (doc in snapshot.documents) {
                            val id = doc.id
                            val title = doc.getString("title") ?: continue
                            val message = doc.getString("message") ?: ""
                            val typeStr = doc.getString("type") ?: "GENERAL"
                            val type = try { AnnouncementType.valueOf(typeStr) } catch (_: Exception) { AnnouncementType.GENERAL }
                            val isActive = doc.getBoolean("isActive") ?: false
                            val showVersion = doc.getBoolean("showVersionInfo") ?: false
                            val currentVer = doc.getString("currentVersion") ?: "1.0.0"
                            val nextVer = doc.getString("nextVersion") ?: "1.1.0"
                            val cta = doc.getString("ctaText")
                            val ctaUrl = doc.getString("ctaUrl")

                            list.add(
                                AppAnnouncement(
                                    id = id,
                                    title = title,
                                    message = message,
                                    type = type,
                                    isActive = isActive,
                                    showVersionInfo = showVersion,
                                    currentVersion = currentVer,
                                    nextVersion = nextVer,
                                    ctaText = cta,
                                    ctaUrl = ctaUrl
                                )
                            )
                        }
                        _allAnnouncements.value = list
                        // Active announcement: first active one
                        _activeAnnouncement.value = list.firstOrNull { it.isActive }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Announcement listener error: ${e.message}")
        }
    }

    fun isDismissed(context: Context, announcementId: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dismissedId = prefs.getString(KEY_LAST_DISMISSED_ID, null)
        return dismissedId == announcementId
    }

    fun dismissAnnouncement(context: Context, announcementId: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LAST_DISMISSED_ID, announcementId).apply()
        if (_activeAnnouncement.value?.id == announcementId) {
            _activeAnnouncement.value = null
        }
    }

    suspend fun saveOrPublishAnnouncement(
        announcement: AppAnnouncement,
        adminEmail: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val docRef = firestore.collection("announcements").document(announcement.id)
            val data = hashMapOf(
                "title" to announcement.title,
                "message" to announcement.message,
                "type" to announcement.type.name,
                "isActive" to announcement.isActive,
                "showVersionInfo" to announcement.showVersionInfo,
                "currentVersion" to announcement.currentVersion,
                "nextVersion" to announcement.nextVersion,
                "ctaText" to announcement.ctaText,
                "ctaUrl" to announcement.ctaUrl,
                "updatedAt" to FieldValue.serverTimestamp(),
                "adminEmail" to adminEmail
            )
            docRef.set(data, SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save announcement: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteAnnouncement(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("announcements").document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
