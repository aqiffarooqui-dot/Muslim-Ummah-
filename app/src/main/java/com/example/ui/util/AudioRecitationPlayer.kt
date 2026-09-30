package com.example.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurahOrDuaId: Int? = null,
    val currentAyahNumber: Int? = null,
    val title: String = "",
    val error: String? = null,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val repeatCount: Int = 1,
    val isContinuous: Boolean = true
) {
    val currentSurahNumber: Int?
        get() = currentSurahOrDuaId

    val progressFraction: Float
        get() = if (durationMs > 0L) (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}

/**
 * Production-ready Audio Recitation Player for Quran Surahs, Ayahs, and Duas.
 * Supports play, pause, resume, stop, seek, continuous next/prev playback, and repeating.
 */
class AudioRecitationPlayer(private val context: Context) {
    private val TAG = "AudioRecitationPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    var onTrackCompleted: (() -> Unit)? = null
    private var loopRemaining = 0

    fun play(id: Int, title: String, url: String, ayahNumber: Int? = null, repeatTimes: Int = 1) {
        if (_playbackState.value.currentSurahOrDuaId == id &&
            _playbackState.value.currentAyahNumber == ayahNumber &&
            _playbackState.value.isPlaying
        ) {
            pause()
            return
        }
        if (_playbackState.value.currentSurahOrDuaId == id &&
            _playbackState.value.currentAyahNumber == ayahNumber &&
            mediaPlayer != null &&
            !_playbackState.value.isPlaying
        ) {
            resume()
            return
        }

        stop()
        loopRemaining = (repeatTimes - 1).coerceAtLeast(0)
        _playbackState.value = AudioPlaybackState(
            isPlaying = false,
            isLoading = true,
            currentSurahOrDuaId = id,
            currentAyahNumber = ayahNumber,
            title = title,
            repeatCount = repeatTimes
        )

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    mp.start()
                    val totalDuration = try { mp.duration.toLong() } catch (e: Exception) { 0L }
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = true,
                        isLoading = false,
                        durationMs = totalDuration,
                        error = null
                    )
                    startProgressTracker()
                }
                setOnCompletionListener {
                    if (loopRemaining > 0) {
                        loopRemaining--
                        try {
                            it.seekTo(0)
                            it.start()
                        } catch (e: Exception) {
                            handleComplete()
                        }
                    } else {
                        handleComplete()
                    }
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "Audio error: what=$what, extra=$extra")
                    stopProgressTracker()
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        isLoading = false,
                        error = "Audio recitation stream unavailable"
                    )
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize player for $url", e)
            _playbackState.value = AudioPlaybackState(
                isPlaying = false,
                isLoading = false,
                currentSurahOrDuaId = null,
                error = "Cannot load recitation: ${e.localizedMessage ?: "Unknown error"}"
            )
        }
    }

    private fun handleComplete() {
        stopProgressTracker()
        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            isLoading = false,
            currentPositionMs = _playbackState.value.durationMs
        )
        onTrackCompleted?.invoke()
    }

    fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    stopProgressTracker()
                    _playbackState.value = _playbackState.value.copy(isPlaying = false)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing player", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.let {
                it.start()
                _playbackState.value = _playbackState.value.copy(isPlaying = true)
                startProgressTracker()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming player", e)
        }
    }

    fun seekTo(positionMs: Long) {
        try {
            mediaPlayer?.let {
                val clamped = positionMs.coerceIn(0L, it.duration.toLong()).toInt()
                it.seekTo(clamped)
                _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking player", e)
        }
    }

    fun stop() {
        stopProgressTracker()
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping player", e)
        } finally {
            _playbackState.value = AudioPlaybackState()
        }
    }

    fun setRepeatCount(count: Int) {
        loopRemaining = (count - 1).coerceAtLeast(0)
        _playbackState.value = _playbackState.value.copy(repeatCount = count)
    }

    fun toggleContinuous() {
        _playbackState.value = _playbackState.value.copy(isContinuous = !_playbackState.value.isContinuous)
    }

    private fun startProgressTracker() {
        stopProgressTracker()
        progressJob = coroutineScope.launch {
            while (isActive) {
                try {
                    val mp = mediaPlayer
                    if (mp != null && mp.isPlaying) {
                        val current = mp.currentPosition.toLong()
                        val dur = try { mp.duration.toLong() } catch (e: Exception) { _playbackState.value.durationMs }
                        _playbackState.value = _playbackState.value.copy(
                            currentPositionMs = current,
                            durationMs = dur
                        )
                    }
                } catch (e: Exception) {
                    // Ignore transient position read errors
                }
                delay(500)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTracker()
        stop()
        coroutineScope.cancel()
    }
}
