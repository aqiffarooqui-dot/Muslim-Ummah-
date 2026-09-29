package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.model.*
import com.example.ui.util.AudioPlaybackState
import com.example.ui.util.AudioRecitationPlayer
import com.example.ui.util.CompassSensorManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

data class DailyVerseInspiration(
    val arabic: String,
    val translation: String,
    val reference: String
)

data class MuslimUiState(
    val selectedCity: CityLocation = PrayerCalculator.POPULAR_CITIES[0], // Makkah
    val calculationMethod: CalculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
    val juristicMethod: JuristicMethod = JuristicMethod.STANDARD,
    val prayerTimes: DailyPrayerTimes? = null,
    val currentHijriDate: HijriDate = HijriCalendarHelper.getHijriDate(),
    val qiblaDirection: Double = 0.0,
    val distanceToKaabaKm: Double = 0.0,
    val compassAzimuth: Float = 0f,
    val isQiblaAligned: Boolean = false,
    val isSensorAvailable: Boolean = true,
    // Tasbih
    val currentDhikrIndex: Int = 0,
    val tasbihCount: Int = 0,
    val tasbihTarget: Int = 33,
    val tasbihLaps: Int = 0,
    val tasbihVibrationEnabled: Boolean = true,
    // Quran
    val quranSearchQuery: String = "",
    val selectedSurahNumber: Int = 1,
    // Duas
    val duaCategory: String = "All",
    val duaSearchQuery: String = "",
    // Names of Allah
    val nameSearchQuery: String = "",
    // Inspiration
    val dailyVerse: DailyVerseInspiration = DailyVerseInspiration(
        arabic = "إِنَّ الصَّلَاةَ كَانَتْ عَلَى الْمُؤْمِنِينَ كِتَابًا مَّوْقُوتًا",
        translation = "Indeed, prayer has been decreed upon the believers a decree of specified times.",
        reference = "Surah An-Nisa 4:103"
    ),
    val prayerTracker: PrayerTrackerRecord = PrayerTrackerRecord(
        dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    )
)

class MuslimViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MuslimRepository
    val audioPlayer = AudioRecitationPlayer(application)
    private val compassManager = CompassSensorManager(application)
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val _uiState = MutableStateFlow(MuslimUiState())
    val uiState: StateFlow<MuslimUiState> = _uiState.asStateFlow()

    val audioState: StateFlow<AudioPlaybackState> = audioPlayer.playbackState

    val bookmarks: StateFlow<List<Bookmark>>
    val tasbihHistory: StateFlow<List<TasbihRecord>>

    private var timerJob: Job? = null
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    init {
        val database = AppDatabase.getDatabase(application)
        repository = MuslimRepository(database.muslimDao())

        bookmarks = repository.allBookmarks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        tasbihHistory = repository.allTasbihRecords.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        recalculatePrayerTimes()
        recalculateQibla()
        startLiveTimer()
        observePrayerTracker()
        observeCompass()
    }

    private fun observePrayerTracker() {
        val todayKey = dateFormat.format(Date())
        viewModelScope.launch {
            repository.getPrayerTracker(todayKey).collect { record ->
                if (record != null) {
                    _uiState.update { it.copy(prayerTracker = record) }
                } else {
                    val newRecord = PrayerTrackerRecord(dateKey = todayKey)
                    _uiState.update { it.copy(prayerTracker = newRecord) }
                }
            }
        }
    }

    private fun observeCompass() {
        compassManager.startListening()
        viewModelScope.launch {
            compassManager.azimuthFlow.collect { azimuth ->
                val qibla = _uiState.value.qiblaDirection
                var diff = abs(azimuth - qibla.toFloat())
                if (diff > 180f) diff = 360f - diff
                val aligned = diff <= 4.0f

                if (aligned && !_uiState.value.isQiblaAligned) {
                    vibrateGentle()
                }

                _uiState.update {
                    it.copy(
                        compassAzimuth = azimuth,
                        isQiblaAligned = aligned
                    )
                }
            }
        }
        viewModelScope.launch {
            compassManager.isSensorAvailable.collect { available ->
                _uiState.update { it.copy(isSensorAvailable = available) }
            }
        }
    }

    private fun startLiveTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                recalculatePrayerTimes()
            }
        }
    }

    fun setCity(city: CityLocation) {
        _uiState.update { it.copy(selectedCity = city) }
        recalculatePrayerTimes()
        recalculateQibla()
    }

    fun setCalculationMethod(method: CalculationMethod) {
        _uiState.update { it.copy(calculationMethod = method) }
        recalculatePrayerTimes()
    }

    fun setJuristicMethod(method: JuristicMethod) {
        _uiState.update { it.copy(juristicMethod = method) }
        recalculatePrayerTimes()
    }

    private fun recalculatePrayerTimes() {
        val city = _uiState.value.selectedCity
        val times = PrayerCalculator.calculateTimes(
            calendar = Calendar.getInstance(),
            latitude = city.latitude,
            longitude = city.longitude,
            timezoneOffsetHours = city.timezoneOffsetHours,
            method = _uiState.value.calculationMethod,
            juristic = _uiState.value.juristicMethod
        )
        _uiState.update {
            it.copy(
                prayerTimes = times,
                currentHijriDate = HijriCalendarHelper.getHijriDate()
            )
        }
    }

    private fun recalculateQibla() {
        val city = _uiState.value.selectedCity
        val direction = QiblaCalculator.calculateQiblaDirection(city.latitude, city.longitude)
        val distance = QiblaCalculator.calculateDistanceToKaabaKm(city.latitude, city.longitude)
        _uiState.update {
            it.copy(
                qiblaDirection = direction,
                distanceToKaabaKm = distance
            )
        }
    }

    fun togglePrayer(prayerName: String) {
        val current = _uiState.value.prayerTracker
        val updated = when (prayerName.lowercase(Locale.US)) {
            "fajr" -> current.copy(fajrDone = !current.fajrDone)
            "dhuhr" -> current.copy(dhuhrDone = !current.dhuhrDone)
            "asr" -> current.copy(asrDone = !current.asrDone)
            "maghrib" -> current.copy(maghribDone = !current.maghribDone)
            "isha" -> current.copy(ishaDone = !current.ishaDone)
            else -> current
        }
        viewModelScope.launch {
            repository.savePrayerTracker(updated)
            _uiState.update { it.copy(prayerTracker = updated) }
        }
    }

    // Tasbih Actions
    fun incrementTasbih() {
        val currentCount = _uiState.value.tasbihCount + 1
        val target = _uiState.value.tasbihTarget
        var laps = _uiState.value.tasbihLaps

        if (_uiState.value.tasbihVibrationEnabled) {
            vibrateGentle()
        }

        if (currentCount >= target) {
            laps += 1
            vibrateGoalReached()
            _uiState.update {
                it.copy(
                    tasbihCount = 0,
                    tasbihLaps = laps
                )
            }
        } else {
            _uiState.update { it.copy(tasbihCount = currentCount) }
        }

        saveCurrentTasbihSession()
    }

    fun resetTasbih() {
        _uiState.update {
            it.copy(
                tasbihCount = 0,
                tasbihLaps = 0
            )
        }
        saveCurrentTasbihSession()
    }

    fun selectDhikr(index: Int) {
        val preset = TasbihData.PRESETS.getOrNull(index) ?: return
        _uiState.update {
            it.copy(
                currentDhikrIndex = index,
                tasbihTarget = preset.defaultTarget,
                tasbihCount = 0,
                tasbihLaps = 0
            )
        }
    }

    fun setTasbihTarget(target: Int) {
        _uiState.update { it.copy(tasbihTarget = target) }
    }

    fun toggleTasbihVibration() {
        _uiState.update { it.copy(tasbihVibrationEnabled = !it.tasbihVibrationEnabled) }
    }

    private fun saveCurrentTasbihSession() {
        val preset = TasbihData.PRESETS[_uiState.value.currentDhikrIndex]
        viewModelScope.launch {
            repository.saveTasbih(
                TasbihRecord(
                    dhikrText = preset.title,
                    arabicText = preset.arabic,
                    count = _uiState.value.tasbihCount,
                    target = _uiState.value.tasbihTarget,
                    laps = _uiState.value.tasbihLaps
                )
            )
        }
    }

    // Quran Actions
    fun setQuranSearch(query: String) {
        _uiState.update { it.copy(quranSearchQuery = query) }
    }

    fun selectSurah(surahNumber: Int) {
        _uiState.update { it.copy(selectedSurahNumber = surahNumber) }
    }

    fun playSurahAudio(surahNumber: Int) {
        val surah = QuranRepository.ALL_SURAHS.find { it.number == surahNumber } ?: return
        audioPlayer.play(surah.number, "Surah ${surah.nameEnglish} (Mishary Alafasy)", surah.audioUrl)
    }

    // Duas Actions
    fun setDuaCategory(category: String) {
        _uiState.update { it.copy(duaCategory = category) }
    }

    fun setDuaSearch(query: String) {
        _uiState.update { it.copy(duaSearchQuery = query) }
    }

    // Bookmarks
    fun toggleBookmark(type: String, refId: Int, secId: Int = 0, title: String, subtitle: String) {
        viewModelScope.launch {
            val isBookmarked = bookmarks.value.any { it.type == type && it.referenceId == refId && it.secondaryId == secId }
            repository.toggleBookmark(type, refId, secId, title, subtitle, isBookmarked)
        }
    }

    fun isItemBookmarked(type: String, refId: Int, secId: Int = 0): Boolean {
        return bookmarks.value.any { it.type == type && it.referenceId == refId && it.secondaryId == secId }
    }

    private fun vibrateGentle() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(35)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateGoalReached() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 80, 150), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(200)
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        compassManager.stopListening()
        audioPlayer.release()
    }
}
