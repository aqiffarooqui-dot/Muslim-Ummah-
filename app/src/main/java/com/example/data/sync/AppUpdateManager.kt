package com.example.data.sync

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppUpdateInfo(
    val currentVersion: String = "1.0.0",
    val latestVersion: String = "1.0.0",
    val whatsNew: List<String> = emptyList(),
    val downloadUrl: String? = null,
    val isMandatory: Boolean = false,
    val hasUpdate: Boolean = false
)

object AppUpdateManager {
    private const val TAG = "AppUpdateManager"
    private const val CURRENT_VERSION = "1.0.0"

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _updateState = MutableStateFlow(AppUpdateInfo(currentVersion = CURRENT_VERSION))
    val updateState: StateFlow<AppUpdateInfo> = _updateState.asStateFlow()

    init {
        listenForRemoteUpdates()
    }

    private fun listenForRemoteUpdates() {
        try {
            firestore.collection("configuration").document("app_update")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Error fetching update config: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val latest = snapshot.getString("latestVersion") ?: CURRENT_VERSION
                        val url = snapshot.getString("downloadUrl")
                        val mandatory = snapshot.getBoolean("isMandatory") ?: false
                        val notes = snapshot.get("whatsNew") as? List<*>
                        val notesList = notes?.mapNotNull { it?.toString() } ?: emptyList()

                        val isNewer = isVersionNewer(CURRENT_VERSION, latest) && !url.isNullOrBlank()

                        _updateState.value = AppUpdateInfo(
                            currentVersion = CURRENT_VERSION,
                            latestVersion = latest,
                            whatsNew = notesList,
                            downloadUrl = url,
                            isMandatory = mandatory,
                            hasUpdate = isNewer
                        )
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Could not initialize update listener: ${e.message}")
        }
    }

    private fun isVersionNewer(current: String, candidate: String): Boolean {
        return try {
            val curParts = current.split(".").map { it.toIntOrNull() ?: 0 }
            val canParts = candidate.split(".").map { it.toIntOrNull() ?: 0 }
            val length = maxOf(curParts.size, canParts.size)

            for (i in 0 until length) {
                val c = curParts.getOrElse(i) { 0 }
                val target = canParts.getOrElse(i) { 0 }
                if (target > c) return true
                if (target < c) return false
            }
            false
        } catch (_: Exception) {
            false
        }
    }

    fun launchUpdate(context: Context, url: String?) {
        if (url.isNullOrBlank()) return
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch update URL: $url", e)
        }
    }

    fun dismissUpdate() {
        _updateState.value = _updateState.value.copy(hasUpdate = false)
    }
}
