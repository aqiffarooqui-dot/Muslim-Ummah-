package com.example.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentSurahOrDuaId: Int? = null,
    val title: String = "",
    val error: String? = null
)

class AudioRecitationPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    fun play(id: Int, title: String, url: String) {
        if (_playbackState.value.currentSurahOrDuaId == id && _playbackState.value.isPlaying) {
            pause()
            return
        }
        if (_playbackState.value.currentSurahOrDuaId == id && mediaPlayer != null && !_playbackState.value.isPlaying) {
            resume()
            return
        }

        stop()
        _playbackState.value = AudioPlaybackState(
            isPlaying = false,
            isLoading = true,
            currentSurahOrDuaId = id,
            title = title
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
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = true,
                        isLoading = false,
                        error = null
                    )
                }
                setOnCompletionListener {
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        isLoading = false
                    )
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayer", "Error: $what, $extra")
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        isLoading = false,
                        error = "Audio stream unavailable"
                    )
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Failed to init player", e)
            _playbackState.value = AudioPlaybackState(
                isPlaying = false,
                isLoading = false,
                currentSurahOrDuaId = null,
                error = "Cannot load recitation: ${e.localizedMessage}"
            )
        }
    }

    fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    _playbackState.value = _playbackState.value.copy(isPlaying = false)
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error pausing", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.let {
                it.start()
                _playbackState.value = _playbackState.value.copy(isPlaying = true)
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error resuming", e)
        }
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error stopping", e)
        } finally {
            _playbackState.value = AudioPlaybackState()
        }
    }

    fun release() {
        stop()
    }
}
