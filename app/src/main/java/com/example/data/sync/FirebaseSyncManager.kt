package com.example.data.sync

import android.util.Log
import com.example.data.local.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object FirebaseSyncManager {
    private const val TAG = "FirebaseSync"
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * Creates or updates the user profile document in Firestore at users/{uid}
     */
    suspend fun syncUserProfile(user: AppUser): Result<Unit> = withContext(Dispatchers.IO) {
        if (user.uid.isBlank()) {
            return@withContext Result.failure(Exception("Cannot sync user profile without valid UID"))
        }

        try {
            val userDocRef = firestore.collection("users").document(user.uid)
            val snapshot = userDocRef.get().await()

            if (!snapshot.exists()) {
                val newProfile = hashMapOf(
                    "uid" to user.uid,
                    "displayName" to user.displayName,
                    "email" to user.email,
                    "photoUrl" to user.photoUrl,
                    "isPremium" to user.isPremium,
                    "planType" to user.planType,
                    "role" to user.role,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "lastLoginAt" to FieldValue.serverTimestamp()
                )
                userDocRef.set(newProfile).await()
                Log.d(TAG, "Created new Firestore user profile for UID: ${user.uid}")
            } else {
                val updates = hashMapOf(
                    "lastLoginAt" to FieldValue.serverTimestamp(),
                    "displayName" to user.displayName,
                    "photoUrl" to user.photoUrl
                )
                userDocRef.set(updates, SetOptions.merge()).await()
                Log.d(TAG, "Updated lastLoginAt for Firestore user UID: ${user.uid}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync user profile: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Loads server-controlled role and entitlement from users/{uid}.
     * Client login payloads are never trusted for admin status.
     */
    suspend fun applyServerUserProfile(user: AppUser): AppUser = withContext(Dispatchers.IO) {
        if (user.uid.isBlank()) return@withContext user.copy(role = "USER", isPremium = false, planType = "Free")
        try {
            val snapshot = firestore.collection("users").document(user.uid).get().await()
            if (!snapshot.exists()) return@withContext user.copy(role = "USER", isPremium = false, planType = "Free")
            val role = snapshot.getString("role")?.uppercase()?.takeIf { it == "ADMIN" } ?: "USER"
            val rawIsPremium = snapshot.getBoolean("isPremium") == true
            val expiresAt = snapshot.getLong("expiresAt")
            val isExpired = expiresAt != null && expiresAt > 0 && expiresAt < System.currentTimeMillis()
            val isPremium = (role == "ADMIN" || rawIsPremium) && !isExpired
            val planType = if (isPremium) {
                snapshot.getString("planType")?.takeIf { !it.contains("Lifetime", ignoreCase = true) }
                    ?: if (role == "ADMIN") "1 Year" else "1 Month"
            } else "Free"
            user.copy(role = role, isPremium = isPremium, planType = planType, expiresAt = expiresAt)
        } catch (e: Exception) {
            Log.w(TAG, "Could not load server user profile: ${e.message}")
            user.copy(role = "USER", isPremium = false, planType = "Free")
        }
    }
    /**
     * Syncs Quran reading position to users/{uid}/quran/position
     */
    suspend fun saveQuranPosition(uid: String, position: QuranReadingPosition) = withContext(Dispatchers.IO) {
        if (uid.isBlank()) return@withContext
        try {
            val docRef = firestore.collection("users").document(uid)
                .collection("quran").document("position")
            val data = hashMapOf(
                "surahNumber" to position.surahNumber,
                "ayahNumber" to position.ayahNumber,
                "surahName" to position.surahName,
                "paraNumber" to position.paraNumber,
                "timestamp" to position.timestamp
            )
            docRef.set(data, SetOptions.merge()).await()
            Log.d(TAG, "Synced Quran reading position to cloud")
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing Quran position: ${e.message}")
        }
    }

    /**
     * Syncs Hadith reading position to users/{uid}/hadith/position
     */
    suspend fun saveHadithPosition(uid: String, position: HadithReadingPosition) = withContext(Dispatchers.IO) {
        if (uid.isBlank()) return@withContext
        try {
            val docRef = firestore.collection("users").document(uid)
                .collection("hadith").document("position")
            val data = hashMapOf(
                "bookId" to position.bookId,
                "bookName" to position.bookName,
                "chapterName" to position.chapterName,
                "hadithId" to position.hadithId,
                "hadithNumber" to position.hadithNumber,
                "timestamp" to position.timestamp
            )
            docRef.set(data, SetOptions.merge()).await()
            Log.d(TAG, "Synced Hadith reading position to cloud")
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing Hadith position: ${e.message}")
        }
    }

    /**
     * Syncs a bookmark to users/{uid}/bookmarks/{bookmarkId}
     */
    suspend fun saveBookmark(uid: String, bookmark: Bookmark) = withContext(Dispatchers.IO) {
        if (uid.isBlank()) return@withContext
        try {
            val docId = "${bookmark.type}_${bookmark.referenceId}_${bookmark.secondaryId}"
            val docRef = firestore.collection("users").document(uid)
                .collection("bookmarks").document(docId)
            val data = hashMapOf(
                "type" to bookmark.type,
                "referenceId" to bookmark.referenceId,
                "secondaryId" to bookmark.secondaryId,
                "title" to bookmark.title,
                "subtitle" to bookmark.subtitle,
                "timestamp" to bookmark.timestamp
            )
            docRef.set(data, SetOptions.merge()).await()
            Log.d(TAG, "Synced bookmark $docId to cloud")
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing bookmark: ${e.message}")
        }
    }

    /**
     * Deletes a bookmark from users/{uid}/bookmarks/{bookmarkId}
     */
    suspend fun deleteBookmark(uid: String, type: String, refId: Int, secId: Int) = withContext(Dispatchers.IO) {
        if (uid.isBlank()) return@withContext
        try {
            val docId = "${type}_${refId}_${secId}"
            firestore.collection("users").document(uid)
                .collection("bookmarks").document(docId)
                .delete().await()
            Log.d(TAG, "Deleted cloud bookmark $docId")
        } catch (e: Exception) {
            Log.w(TAG, "Error deleting cloud bookmark: ${e.message}")
        }
    }

    /**
     * Syncs a Quran study note to users/{uid}/quran_notes/{noteId}
     */
    suspend fun saveQuranNote(uid: String, note: QuranNote) = withContext(Dispatchers.IO) {
        if (uid.isBlank()) return@withContext
        try {
            val docId = "${note.surahNumber}_${note.ayahNumber}"
            val docRef = firestore.collection("users").document(uid)
                .collection("quran_notes").document(docId)
            val data = hashMapOf(
                "surahNumber" to note.surahNumber,
                "ayahNumber" to note.ayahNumber,
                "surahName" to note.surahName,
                "noteText" to note.noteText,
                "timestamp" to note.timestamp
            )
            docRef.set(data, SetOptions.merge()).await()
            Log.d(TAG, "Synced Quran note $docId to cloud")
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing Quran note: ${e.message}")
        }
    }

    /**
     * Syncs a Hadith study note to users/{uid}/hadith_notes/{noteId}
     */
    suspend fun saveHadithNote(uid: String, note: HadithNote) = withContext(Dispatchers.IO) {
        if (uid.isBlank()) return@withContext
        try {
            val docId = "${note.bookId}_${note.hadithId}"
            val docRef = firestore.collection("users").document(uid)
                .collection("hadith_notes").document(docId)
            val data = hashMapOf(
                "bookId" to note.bookId,
                "hadithId" to note.hadithId,
                "noteText" to note.noteText,
                "timestamp" to note.timestamp
            )
            docRef.set(data, SetOptions.merge()).await()
            Log.d(TAG, "Synced Hadith note $docId to cloud")
        } catch (e: Exception) {
            Log.w(TAG, "Error syncing Hadith note: ${e.message}")
        }
    }

    /**
     * Bi-directional conflict-aware synchronization:
     * Restores cloud data onto this device and pushes any newer local data up to Firestore.
     */
    suspend fun pullAndSyncAllData(uid: String, repository: MuslimRepository) = withContext(Dispatchers.IO) {
        if (uid.isBlank()) return@withContext
        try {
            Log.d(TAG, "Starting bi-directional cloud synchronization for UID: $uid")

            // 1. Quran Reading Position
            val cloudQuranDoc = firestore.collection("users").document(uid)
                .collection("quran").document("position")
                .get().await()

            val localQuranPos = repository.getQuranReadingPositionDirect()

            if (cloudQuranDoc.exists()) {
                val cloudTime = cloudQuranDoc.getLong("timestamp") ?: 0L
                val localTime = localQuranPos?.timestamp ?: 0L

                if (cloudTime > localTime) {
                    val restoredPos = QuranReadingPosition(
                        id = 1,
                        surahNumber = cloudQuranDoc.getLong("surahNumber")?.toInt() ?: 1,
                        ayahNumber = cloudQuranDoc.getLong("ayahNumber")?.toInt() ?: 1,
                        surahName = cloudQuranDoc.getString("surahName") ?: "Al-Fatihah",
                        paraNumber = cloudQuranDoc.getLong("paraNumber")?.toInt() ?: 1,
                        timestamp = cloudTime
                    )
                    repository.saveQuranReadingPosition(restoredPos)
                    Log.d(TAG, "Restored Quran position from cloud: Surah ${restoredPos.surahName} Ayah ${restoredPos.ayahNumber}")
                } else if (localQuranPos != null && localTime > cloudTime) {
                    saveQuranPosition(uid, localQuranPos)
                }
            } else if (localQuranPos != null) {
                saveQuranPosition(uid, localQuranPos)
            }

            // 2. Hadith Reading Position
            val cloudHadithDoc = firestore.collection("users").document(uid)
                .collection("hadith").document("position")
                .get().await()

            val localHadithPos = repository.getHadithReadingPositionDirect()

            if (cloudHadithDoc.exists()) {
                val cloudTime = cloudHadithDoc.getLong("timestamp") ?: 0L
                val localTime = localHadithPos?.timestamp ?: 0L

                if (cloudTime > localTime) {
                    val restoredPos = HadithReadingPosition(
                        id = 1,
                        bookId = cloudHadithDoc.getString("bookId") ?: "bukhari",
                        bookName = cloudHadithDoc.getString("bookName") ?: "Sahih al-Bukhari",
                        chapterName = cloudHadithDoc.getString("chapterName") ?: "Revelation",
                        hadithId = cloudHadithDoc.getLong("hadithId")?.toInt() ?: 1,
                        hadithNumber = cloudHadithDoc.getString("hadithNumber") ?: "Hadith 1",
                        timestamp = cloudTime
                    )
                    repository.saveHadithReadingPosition(restoredPos)
                    Log.d(TAG, "Restored Hadith position from cloud: ${restoredPos.bookName} ${restoredPos.hadithNumber}")
                } else if (localHadithPos != null && localTime > cloudTime) {
                    saveHadithPosition(uid, localHadithPos)
                }
            } else if (localHadithPos != null) {
                saveHadithPosition(uid, localHadithPos)
            }

            // 3. Bookmarks
            val cloudBookmarks = firestore.collection("users").document(uid)
                .collection("bookmarks").get().await()
            val localBookmarks = repository.getAllBookmarksList()

            // Merge cloud bookmarks into local
            for (doc in cloudBookmarks.documents) {
                val type = doc.getString("type") ?: continue
                val refId = doc.getLong("referenceId")?.toInt() ?: 0
                val secId = doc.getLong("secondaryId")?.toInt() ?: 0
                val title = doc.getString("title") ?: ""
                val subtitle = doc.getString("subtitle") ?: ""
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                val existsLocally = localBookmarks.any {
                    it.type == type && it.referenceId == refId && it.secondaryId == secId
                }
                if (!existsLocally) {
                    repository.insertBookmark(
                        Bookmark(
                            type = type,
                            referenceId = refId,
                            secondaryId = secId,
                            title = title,
                            subtitle = subtitle,
                            timestamp = timestamp
                        )
                    )
                }
            }

            // Push any local bookmarks not yet in cloud
            for (b in localBookmarks) {
                val docId = "${b.type}_${b.referenceId}_${b.secondaryId}"
                val existsInCloud = cloudBookmarks.documents.any { it.id == docId }
                if (!existsInCloud) {
                    saveBookmark(uid, b)
                }
            }

            // 4. Quran Notes
            val cloudQuranNotes = firestore.collection("users").document(uid)
                .collection("quran_notes").get().await()
            val localQuranNotes = repository.getAllQuranNotesList()

            for (doc in cloudQuranNotes.documents) {
                val surahNum = doc.getLong("surahNumber")?.toInt() ?: continue
                val ayahNum = doc.getLong("ayahNumber")?.toInt() ?: continue
                val surahName = doc.getString("surahName") ?: ""
                val noteText = doc.getString("noteText") ?: ""
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                val existsLocally = localQuranNotes.any {
                    it.surahNumber == surahNum && it.ayahNumber == ayahNum
                }
                if (!existsLocally && noteText.isNotBlank()) {
                    repository.addQuranNote(
                        QuranNote(
                            surahNumber = surahNum,
                            ayahNumber = ayahNum,
                            surahName = surahName,
                            noteText = noteText,
                            timestamp = timestamp
                        )
                    )
                }
            }

            for (note in localQuranNotes) {
                val docId = "${note.surahNumber}_${note.ayahNumber}"
                val existsInCloud = cloudQuranNotes.documents.any { it.id == docId }
                if (!existsInCloud) {
                    saveQuranNote(uid, note)
                }
            }

            // 5. Hadith Notes
            val cloudHadithNotes = firestore.collection("users").document(uid)
                .collection("hadith_notes").get().await()
            val localHadithNotes = repository.getAllHadithNotesList()

            for (doc in cloudHadithNotes.documents) {
                val bookId = doc.getString("bookId") ?: continue
                val hadithId = doc.getLong("hadithId")?.toInt() ?: continue
                val noteText = doc.getString("noteText") ?: ""
                val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                val existsLocally = localHadithNotes.any {
                    it.bookId == bookId && it.hadithId == hadithId
                }
                if (!existsLocally && noteText.isNotBlank()) {
                    repository.addHadithNote(
                        HadithNote(
                            bookId = bookId,
                            hadithId = hadithId,
                            noteText = noteText,
                            timestamp = timestamp
                        )
                    )
                }
            }

            for (note in localHadithNotes) {
                val docId = "${note.bookId}_${note.hadithId}"
                val existsInCloud = cloudHadithNotes.documents.any { it.id == docId }
                if (!existsInCloud) {
                    saveHadithNote(uid, note)
                }
            }

            // 6. Entitlement & Subscription Sync
            syncUserEntitlementFromCloud(uid, repository)

            Log.d(TAG, "Completed bi-directional cloud synchronization successfully")
        } catch (e: Exception) {
            Log.w(TAG, "Bi-directional sync completed with offline or partial status: ${e.message}")
        }
    }

    /**
     * Pulls the user's current cloud subscription & entitlement from Firestore.
     * Evaluates expiry and updates local database and SubscriptionManager.
     */
    suspend fun syncUserEntitlementFromCloud(uid: String, repository: MuslimRepository) = withContext(Dispatchers.IO) {
        if (uid.isBlank()) return@withContext
        try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            if (userDoc.exists()) {
                val email = userDoc.getString("email") ?: ""
                val isPremiumCloud = userDoc.getBoolean("isPremium") ?: false
                val planTypeCloud = userDoc.getString("planType") ?: "Free"
                val expiresAtCloud = userDoc.getLong("expiresAt")
                val now = System.currentTimeMillis()

                val isStillActive = isPremiumCloud && (expiresAtCloud == null || expiresAtCloud >= now)
                val finalPlan = if (isStillActive) planTypeCloud else "Free"

                // Update local Room user record
                val localUser = repository.getUserByEmail(email)
                if (localUser != null) {
                    repository.updateSubscription(
                        email = email,
                        isPremium = isStillActive,
                        planType = finalPlan,
                        expiresAt = if (isStillActive) expiresAtCloud else null
                    )
                }

                // If expired in cloud, update Firestore back to clean state
                if (isPremiumCloud && expiresAtCloud != null && expiresAtCloud < now) {
                    firestore.collection("users").document(uid).update(
                        mapOf(
                            "isPremium" to false,
                            "planType" to "Free",
                            "expiredAt" to FieldValue.serverTimestamp()
                        )
                    )
                }

                // Update runtime SubscriptionManager
                com.example.data.subscription.SubscriptionManager.updateEntitlementForUser(
                    email = email,
                    isPremium = isStillActive,
                    planType = finalPlan,
                    expiresAt = if (isStillActive) expiresAtCloud else null
                )
                Log.d(TAG, "Synced cloud entitlement for $email: isPremium=$isStillActive, plan=$finalPlan")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not fetch cloud entitlement: ${e.message}")
        }
    }

    /**
     * Admin Panel: Activates, extends, or revokes a user's subscription in Firestore.
     * Writes to both users/{uid} and entitlements/{uid} so state persists across devices and app restarts.
     */
    suspend fun grantOrUpdateUserSubscriptionByAdmin(
        adminEmail: String,
        targetEmail: String,
        targetUid: String?,
        isPremium: Boolean,
        planType: String,
        durationDays: Int?,
        expiresAtOverride: Long? = null
    ): Result<Long?> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = targetEmail.trim()
            val now = System.currentTimeMillis()
            val expiresAt: Long? = when {
                !isPremium -> null
                expiresAtOverride != null -> expiresAtOverride
                durationDays != null -> now + (durationDays.toLong() * 24 * 3600 * 1000)
                else -> null
            }

            // Determine UID: use targetUid if provided or search users collection by email
            var resolvedUid = targetUid
            if (resolvedUid.isNullOrBlank()) {
                val querySnap = firestore.collection("users")
                    .whereEqualTo("email", cleanEmail)
                    .limit(1)
                    .get()
                    .await()
                if (!querySnap.isEmpty) {
                    resolvedUid = querySnap.documents[0].id
                }
            }

            val userPayload = hashMapOf<String, Any?>(
                "isPremium" to isPremium,
                "planType" to planType,
                "expiresAt" to expiresAt,
                "entitlementSource" to "ADMIN_GRANT",
                "adminGrantedBy" to adminEmail,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            if (!resolvedUid.isNullOrBlank()) {
                firestore.collection("users").document(resolvedUid)
                    .set(userPayload, SetOptions.merge()).await()

                val entitlementPayload = hashMapOf<String, Any?>(
                    "userId" to resolvedUid,
                    "email" to cleanEmail,
                    "tier" to com.example.data.subscription.SubscriptionManager.mapPlanNameToTier(planType).name,
                    "isActive" to isPremium,
                    "source" to "ADMIN_GRANT",
                    "planName" to planType,
                    "startDateMillis" to now,
                    "expiresAtMillis" to expiresAt,
                    "grantedBy" to adminEmail,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                firestore.collection("entitlements").document(resolvedUid)
                    .set(entitlementPayload, SetOptions.merge()).await()
            } else {
                // If UID is unknown, create placeholder document under email key
                val safeEmailKey = cleanEmail.replace(".", "_").replace("@", "_")
                firestore.collection("entitlements").document(safeEmailKey)
                    .set(userPayload, SetOptions.merge()).await()
            }

            Log.d(TAG, "Admin ($adminEmail) updated subscription for $cleanEmail -> $planType (expiresAt=$expiresAt)")
            Result.success(expiresAt)
        } catch (e: Exception) {
            Log.e(TAG, "Failed admin subscription update: ${e.message}", e)
            Result.failure(e)
        }
    }
}
