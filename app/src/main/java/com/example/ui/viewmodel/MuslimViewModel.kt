package com.example.ui.viewmodel

import android.app.Activity
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
import com.example.data.subscription.PremiumEntitlement
import com.example.data.subscription.SubscriptionManager
import com.example.data.subscription.SubscriptionTier
import com.example.data.sync.FirebaseSyncManager
import com.example.ui.theme.AppThemeType
import com.example.ui.theme.ThemeManager
import com.example.ui.util.AudioPlaybackState
import com.example.ui.util.AudioRecitationPlayer
import com.example.ui.util.CompassSensorManager
import com.example.ui.util.DeviceLocationProvider
import com.example.ui.util.GoogleAuthManager
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

data class PremiumFeatureItem(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val status: String = "ACTIVE"
)

data class MuslimUiState(
    val selectedCity: CityLocation = PrayerCalculator.POPULAR_CITIES[0], // Mumbai, India
    val calculationMethod: CalculationMethod = CalculationMethod.KARACHI,
    val juristicMethod: JuristicMethod = JuristicMethod.HANAFI,
    val prayerTimes: DailyPrayerTimes? = null,
    val currentHijriDate: HijriDate = HijriCalendarHelper.getHijriDate(),
    val qiblaDirection: Double = 0.0,
    val distanceToKaabaKm: Double = 0.0,
    val compassAzimuth: Float = 0f,
    val isQiblaAligned: Boolean = false,
    val isSensorAvailable: Boolean = true,
    val compassAccuracy: Int = 3, // 3 = SENSOR_STATUS_ACCURACY_HIGH
    val compassOffsetDegrees: Float = 0f,
    val isLocating: Boolean = false,
    // Theme
    val currentTheme: AppThemeType = AppThemeType.EMERALD,
    // Tasbih
    val currentDhikrIndex: Int = 0,
    val tasbihCount: Int = 0,
    val tasbihTarget: Int = 33,
    val tasbihLaps: Int = 0,
    val tasbihVibrationEnabled: Boolean = true,
    // Quran
    val quranSearchQuery: String = "",
    val selectedSurahNumber: Int = 1,
    val selectedReciter: String = "Sheikh Mishary Rashid Alafasy",
    val lastQuranPosition: QuranReadingPosition? = null,
    // Hadith
    val lastHadithPosition: HadithReadingPosition? = null,
    // Khatam
    val khatamProgress: KhatamProgress? = null,
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
    ),
    // Auth & Subscription
    val currentUser: AppUser? = null,
    val isAuthLoading: Boolean = false,
    val authErrorMessage: String? = null,
    val isAuthInitialized: Boolean = false,
    val entitlement: PremiumEntitlement = PremiumEntitlement(tier = SubscriptionTier.FREE, isAdFree = false),
    val isAdmin: Boolean = false,
    val isPremium: Boolean = false,
    val showPaywallModal: Boolean = false,
    val paywallTriggerFeature: String = "",
    val selectedAdhanSound: String = "Makkah Al-Mukarramah Adhan",
    // Qada Prayers & Fasting Tracker (Premium)
    val qadaFajr: Int = 0,
    val qadaDhuhr: Int = 0,
    val qadaAsr: Int = 0,
    val qadaMaghrib: Int = 0,
    val qadaIsha: Int = 0,
    val qadaFasts: Int = 0,
    // Admin management UI state
    val adminUserSearchQuery: String = "",
    val adminFilterPlan: String = "All",
    val statusMessage: String? = null
)

class MuslimViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        val PREMIUM_FEATURES_CATALOG = listOf(
            PremiumFeatureItem(
                id = "ad_free",
                title = "100% Ad-Free Sacred Experience",
                description = "Uninterrupted focus and reflection without advertisements.",
                iconName = "Block"
            ),
            PremiumFeatureItem(
                id = "multiple_reciters",
                title = "5 Elite Quran Reciters & Offline Audio",
                description = "Download and stream Mishary Alafasy, Abdul Basit, As-Sudais, Ash-Shuraim, and Al-Ghamdi in studio high-bitrate.",
                iconName = "RecordVoiceOver"
            ),
            PremiumFeatureItem(
                id = "adhan_voices",
                title = "Historic Adhans & Pre-Prayer Alerts",
                description = "Authentic Adhan audio from Makkah, Madinah, Al-Aqsa, and Cairo with custom reminders 15 minutes before prayer.",
                iconName = "NotificationsActive"
            ),
            PremiumFeatureItem(
                id = "ramadan_qada",
                title = "Ramadan & Qada Prayer Tracker",
                description = "Suhoor & Iftar live countdowns, missed prayer (Qada) calculator, and fasting logs.",
                iconName = "Restaurant"
            ),
            PremiumFeatureItem(
                id = "qibla_ar",
                title = "3D Compass & Precision Calibration",
                description = "Calibrated sensor accuracy, local magnetic declination tuning, and angle to Kaaba.",
                iconName = "Explore"
            ),
            PremiumFeatureItem(
                id = "unlimited_tasbih",
                title = "Custom Dhikr Builder & Cloud Sync",
                description = "Create and record unlimited personalized dhikr routines with audio counter.",
                iconName = "RadioButtonChecked"
            ),
            PremiumFeatureItem(
                id = "ruqyah_duas",
                title = "Audio Ruqyah & Deep Duas Commentary",
                description = "Complete audio recitations of Hisn al-Muslim prayers and Quranic Ruqyah protection.",
                iconName = "VolunteerActivism"
            )
        )
    }

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
    val allUsers: StateFlow<List<AppUser>>
    val quranNotes: StateFlow<List<QuranNote>>
    val hadithNotes: StateFlow<List<HadithNote>>

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

        allUsers = repository.allUsers.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        quranNotes = repository.allQuranNotes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        hadithNotes = repository.allHadithNotes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        viewModelScope.launch {
            repository.seedInitialUsersIfNeeded()
            val currentFbUser = GoogleAuthManager.getCurrentAppUser()
            if (currentFbUser != null) {
                repository.saveUser(currentFbUser)
                checkAndUpdateUserStatus(currentFbUser)
                FirebaseSyncManager.pullAndSyncAllData(currentFbUser.uid, repository)
            } else {
                checkAndUpdateUserStatus(null)
            }
            _uiState.update { it.copy(isAuthInitialized = true) }
        }

        viewModelScope.launch {
            repository.quranReadingPosition.collect { pos ->
                _uiState.update { it.copy(lastQuranPosition = pos) }
            }
        }

        viewModelScope.launch {
            repository.hadithReadingPosition.collect { pos ->
                _uiState.update { it.copy(lastHadithPosition = pos) }
            }
        }

        viewModelScope.launch {
            repository.khatamProgress.collect { progress ->
                _uiState.update { it.copy(khatamProgress = progress) }
            }
        }

        viewModelScope.launch {
            SubscriptionManager.entitlementFlow.collect { ent ->
                _uiState.update {
                    it.copy(
                        entitlement = ent,
                        isPremium = ent.isPremiumActive || it.isAdmin
                    )
                }
            }
        }

        recalculatePrayerTimes()
        recalculateQibla()
        startLiveTimer()
        observePrayerTracker()
        observeCompass()
    }

    private fun checkAndUpdateUserStatus(user: AppUser?) {
        if (user == null) {
            _uiState.update {
                it.copy(
                    currentUser = null,
                    isAdmin = false,
                    isPremium = false
                )
            }
            SubscriptionManager.updateEntitlementForUser("", false, "Free", null)
            return
        }

        viewModelScope.launch {
            val serverUser = FirebaseSyncManager.applyServerUserProfile(user)
            val isAdmin = serverUser.role == "ADMIN"
            val isPremium = serverUser.isPremium

            SubscriptionManager.updateEntitlementForUser(serverUser.email, isPremium, serverUser.planType, serverUser.expiresAt)

            _uiState.update {
                it.copy(
                    currentUser = serverUser,
                    isAdmin = isAdmin,
                    isPremium = isPremium
                )
            }
        }
    }

    // ==========================================
    // Real Google Sign-In Actions
    // ==========================================

    fun signInWithGoogle(activity: Activity) {
        _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
        viewModelScope.launch {
            val result = GoogleAuthManager.signInWithGoogleCredentialManager(getApplication(), activity)
            result.onSuccess { user ->
                repository.saveUser(user)
                checkAndUpdateUserStatus(user)
                FirebaseSyncManager.syncUserProfile(user)
                FirebaseSyncManager.pullAndSyncAllData(user.uid, repository)
                _uiState.update { it.copy(isAuthLoading = false, authErrorMessage = null) }
                showStatus("Signed in with Google: ${user.displayName}")
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authErrorMessage = err.message ?: "Authentication failed. Please try again."
                    )
                }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.update { it.copy(authErrorMessage = "Please enter a valid email address.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(authErrorMessage = "Password must be at least 6 characters.") }
            return
        }

        _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
        viewModelScope.launch {
            val result = GoogleAuthManager.signInWithEmailAndPassword(cleanEmail, password)
            result.onSuccess { user ->
                repository.saveUser(user)
                checkAndUpdateUserStatus(user)
                FirebaseSyncManager.syncUserProfile(user)
                FirebaseSyncManager.pullAndSyncAllData(user.uid, repository)
                _uiState.update { it.copy(isAuthLoading = false, authErrorMessage = null) }
                showStatus("Welcome back, ${user.displayName}!")
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authErrorMessage = err.message ?: "Invalid email or password."
                    )
                }
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, displayName: String) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.update { it.copy(authErrorMessage = "Please enter a valid email address.") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(authErrorMessage = "Password must be at least 6 characters.") }
            return
        }

        _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
        viewModelScope.launch {
            val result = GoogleAuthManager.createUserWithEmailAndPassword(cleanEmail, password, displayName)
            result.onSuccess { user ->
                repository.saveUser(user)
                checkAndUpdateUserStatus(user)
                FirebaseSyncManager.syncUserProfile(user)
                FirebaseSyncManager.pullAndSyncAllData(user.uid, repository)
                _uiState.update { it.copy(isAuthLoading = false, authErrorMessage = null) }
                showStatus("Account created! Welcome, ${user.displayName}")
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authErrorMessage = err.message ?: "Failed to create account."
                    )
                }
            }
        }
    }

    fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        val cleanEmail = email.trim()
        if (cleanEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            onResult(false, "Please enter a valid email address.")
            return
        }
        viewModelScope.launch {
            val result = GoogleAuthManager.sendPasswordResetEmail(cleanEmail)
            result.onSuccess {
                onResult(true, "Password reset link sent to $cleanEmail. Please check your inbox.")
            }.onFailure { err ->
                onResult(false, err.message ?: "Failed to send reset email.")
            }
        }
    }

    fun dismissAuthError() {
        _uiState.update { it.copy(authErrorMessage = null) }
    }

    fun signOutUser(context: Context) {
        viewModelScope.launch {
            GoogleAuthManager.signOut(context)
            _uiState.update {
                it.copy(
                    currentUser = null,
                    isAdmin = false,
                    isPremium = false
                )
            }
            SubscriptionManager.updateEntitlementForUser("", false, "Free", null)
            showStatus("Signed out successfully.")
        }
    }


    // ==========================================
    // GPS Device Location
    // ==========================================

    fun fetchDeviceLocation(context: Context) {
        _uiState.update { it.copy(isLocating = true) }
        viewModelScope.launch {
            val result = DeviceLocationProvider.getCurrentDeviceLocation(context)
            _uiState.update { it.copy(isLocating = false) }
            result.onSuccess { location ->
                setCity(location)
                showStatus("GPS Location updated: ${location.name}, ${location.country}")
            }.onFailure { error ->
                showStatus("Location error: ${error.message}")
            }
        }
    }

    // ==========================================
    // Compass Calibration
    // ==========================================

    fun setCompassOffset(offsetDegrees: Float) {
        compassManager.setCalibrationOffset(offsetDegrees)
        _uiState.update { it.copy(compassOffsetDegrees = offsetDegrees) }
        showStatus("Compass calibration offset: ${if (offsetDegrees >= 0) "+$offsetDegrees" else "$offsetDegrees"}°")
    }

    fun resetCompassCalibration() {
        compassManager.setCalibrationOffset(0f)
        _uiState.update { it.copy(compassOffsetDegrees = 0f) }
        showStatus("Compass calibration reset to 0°")
    }

    // ==========================================
    // Theme Selection
    // ==========================================

    fun setTheme(theme: AppThemeType) {
        ThemeManager.setTheme(theme)
        _uiState.update { it.copy(currentTheme = theme) }
        showStatus("Theme updated to ${theme.title}")
    }

    // ==========================================
    // Quran & Hadith Reading Positions & Notes
    // ==========================================

    fun saveQuranReadingPosition(surahNumber: Int, ayahNumber: Int, surahName: String) {
        viewModelScope.launch {
            val pos = QuranReadingPosition(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                surahName = surahName,
                paraNumber = 1
            )
            repository.saveQuranReadingPosition(pos)
            val uid = _uiState.value.currentUser?.uid ?: ""
            if (uid.isNotBlank()) {
                FirebaseSyncManager.saveQuranPosition(uid, pos)
            }
        }
    }

    fun addQuranNote(surahNumber: Int, ayahNumber: Int, surahName: String, text: String) {
        viewModelScope.launch {
            val note = QuranNote(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                surahName = surahName,
                noteText = text
            )
            repository.addQuranNote(note)
            val uid = _uiState.value.currentUser?.uid ?: ""
            if (uid.isNotBlank()) {
                FirebaseSyncManager.saveQuranNote(uid, note)
            }
            showStatus("Note saved for Surah $surahName Ayah $ayahNumber")
        }
    }

    fun saveHadithReadingPosition(bookId: String, chapterName: String, hadithId: Int, hadithNumber: String) {
        viewModelScope.launch {
            val pos = HadithReadingPosition(
                bookId = bookId,
                chapterName = chapterName,
                hadithId = hadithId,
                hadithNumber = hadithNumber
            )
            repository.saveHadithReadingPosition(pos)
            val uid = _uiState.value.currentUser?.uid ?: ""
            if (uid.isNotBlank()) {
                FirebaseSyncManager.saveHadithPosition(uid, pos)
            }
        }
    }

    fun addHadithNote(bookId: String, hadithId: Int, text: String) {
        viewModelScope.launch {
            val note = HadithNote(
                bookId = bookId,
                hadithId = hadithId,
                noteText = text
            )
            repository.addHadithNote(note)
            val uid = _uiState.value.currentUser?.uid ?: ""
            if (uid.isNotBlank()) {
                FirebaseSyncManager.saveHadithNote(uid, note)
            }
            showStatus("Note saved for Hadith #$hadithId")
        }
    }

    fun updateKhatamPages(completed: Int) {
        viewModelScope.launch {
            val current = _uiState.value.khatamProgress ?: KhatamProgress()
            val updated = current.copy(
                completedPages = completed.coerceIn(0, 604),
                isCompleted = completed >= 604,
                lastUpdated = System.currentTimeMillis()
            )
            repository.saveKhatamProgress(updated)
            showStatus("Khatam progress updated: $completed / 604 pages")
        }
    }

    // ==========================================
    // Subscription & Paywall
    // ==========================================

    fun showPaywall(featureName: String = "Muslim Ummah Premium") {
        _uiState.update {
            it.copy(
                showPaywallModal = true,
                paywallTriggerFeature = featureName
            )
        }
    }

    fun dismissPaywall() {
        _uiState.update { it.copy(showPaywallModal = false) }
    }

    fun launchSubscriptionPurchase(activity: android.app.Activity, planId: String) {
        val user = _uiState.value.currentUser
        if (user == null) {
            showStatus("Please sign in before subscribing.")
            return
        }
        val plan = com.example.data.subscription.SubscriptionPricingManager.plansState.value.find { it.id == planId }
        val googlePlayId = plan?.googlePlayProductId ?: "muslim_ummah_sub_1m"

        com.example.data.subscription.PlayBillingManager.launchPurchaseFlow(activity, googlePlayId) { success, errorMsg ->
            if (!success && errorMsg != null) {
                showStatus(errorMsg)
            }
        }
    }

    fun subscribePlan(planId: String) {
        // Fallback for non-activity calls
        val plan = com.example.data.subscription.SubscriptionPricingManager.plansState.value.find { it.id == planId }
        showStatus("To subscribe to ${plan?.name ?: planId}, complete checkout via Google Play.")
    }

    fun restorePurchases() {
        com.example.data.subscription.PlayBillingManager.restorePurchases { count, message ->
            showStatus(message)
        }
    }

    fun triggerManualCloudSync() {
        val user = _uiState.value.currentUser
        if (user == null) {
            showStatus("Please sign in to sync with cloud.")
            return
        }
        viewModelScope.launch {
            showStatus("Syncing data with cloud...")
            try {
                FirebaseSyncManager.syncUserProfile(user)
                FirebaseSyncManager.pullAndSyncAllData(user.uid, repository)
                showStatus("Cloud sync complete! Alhamdulillah.")
            } catch (e: Exception) {
                showStatus("Sync finished locally.")
            }
        }
    }

    // ==========================================
    // Admin User & Subscription Management
    // ==========================================

    fun setAdminUserSearch(query: String) {
        _uiState.update { it.copy(adminUserSearchQuery = query) }
    }

    fun setAdminFilterPlan(plan: String) {
        _uiState.update { it.copy(adminFilterPlan = plan) }
    }

    fun updateUserSubscription(email: String, isPremium: Boolean, planType: String, durationDays: Int? = null) {
        viewModelScope.launch {
            val expiresAt = if (durationDays != null) {
                System.currentTimeMillis() + (durationDays.toLong() * 24 * 3600 * 1000)
            } else null

            repository.updateSubscription(email, isPremium, planType, expiresAt)

            if (_uiState.value.currentUser?.email.equals(email, ignoreCase = true)) {
                val updated = repository.getUserByEmail(email)
                checkAndUpdateUserStatus(updated)
            }
            showStatus("Updated subscription for $email -> $planType")
        }
    }

    fun deleteUserByAdmin(email: String) {
        viewModelScope.launch {
            repository.deleteUser(email)
            showStatus("User $email has been removed.")
        }
    }

    fun addNewUserByAdmin(email: String, displayName: String, isPremium: Boolean, planType: String) {
        viewModelScope.launch {
            val existing = repository.getUserByEmail(email)
            if (existing != null) {
                showStatus("User with email $email already exists.")
                return@launch
            }
            val newUser = AppUser(
                email = email.trim(),
                displayName = displayName.ifBlank { email.substringBefore("@") },
                isPremium = isPremium,
                planType = planType,
                role = "USER",
                registeredDate = System.currentTimeMillis()
            )
            repository.saveUser(newUser)
            showStatus("Created user $email successfully.")
        }
    }

    // ==========================================
    // Qada Prayers & Fasting Tracker (Premium)
    // ==========================================

    fun incrementQada(prayerName: String) {
        _uiState.update {
            when (prayerName.lowercase(Locale.US)) {
                "fajr" -> it.copy(qadaFajr = it.qadaFajr + 1)
                "dhuhr" -> it.copy(qadaDhuhr = it.qadaDhuhr + 1)
                "asr" -> it.copy(qadaAsr = it.qadaAsr + 1)
                "maghrib" -> it.copy(qadaMaghrib = it.qadaMaghrib + 1)
                "isha" -> it.copy(qadaIsha = it.qadaIsha + 1)
                "fasts" -> it.copy(qadaFasts = it.qadaFasts + 1)
                else -> it
            }
        }
    }

    fun decrementQada(prayerName: String) {
        _uiState.update {
            when (prayerName.lowercase(Locale.US)) {
                "fajr" -> it.copy(qadaFajr = (it.qadaFajr - 1).coerceAtLeast(0))
                "dhuhr" -> it.copy(qadaDhuhr = (it.qadaDhuhr - 1).coerceAtLeast(0))
                "asr" -> it.copy(qadaAsr = (it.qadaAsr - 1).coerceAtLeast(0))
                "maghrib" -> it.copy(qadaMaghrib = (it.qadaMaghrib - 1).coerceAtLeast(0))
                "isha" -> it.copy(qadaIsha = (it.qadaIsha - 1).coerceAtLeast(0))
                "fasts" -> it.copy(qadaFasts = (it.qadaFasts - 1).coerceAtLeast(0))
                else -> it
            }
        }
    }

    fun setReciter(reciter: String) {
        _uiState.update { it.copy(selectedReciter = reciter) }
        showStatus("Quran reciter set to $reciter")
    }

    fun setAdhanSound(adhan: String) {
        _uiState.update { it.copy(selectedAdhanSound = adhan) }
        showStatus("Adhan sound set to $adhan")
    }

    fun showStatus(msg: String) {
        _uiState.update { it.copy(statusMessage = msg) }
        viewModelScope.launch {
            delay(3500L)
            _uiState.update { if (it.statusMessage == msg) it.copy(statusMessage = null) else it }
        }
    }

    // ==========================================
    // Existing Prayer, Qibla, Tasbih logic
    // ==========================================

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
        viewModelScope.launch {
            compassManager.accuracyFlow.collect { accuracy ->
                _uiState.update { it.copy(compassAccuracy = accuracy) }
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
        try {
            com.example.ui.util.PrayerNotificationManager.schedulePrayerNotifications(
                getApplication(),
                times,
                true
            )
        } catch (e: Throwable) {
            // Gracefully ignore during unit tests / headless environments
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
        val reciter = QuranReciter.ALL_RECITERS.find { it.name == _uiState.value.selectedReciter } ?: QuranReciter.ALL_RECITERS[0]
        audioPlayer.play(surah.number, "Surah ${surah.nameEnglish} (${reciter.name})", surah.getAudioUrl(reciter.id))
    }

    fun playNextSurah() {
        val current = audioPlayer.playbackState.value.currentSurahNumber ?: _uiState.value.selectedSurahNumber ?: 1
        val next = if (current >= 114) 1 else current + 1
        playSurahAudio(next)
    }

    fun playPreviousSurah() {
        val current = audioPlayer.playbackState.value.currentSurahNumber ?: _uiState.value.selectedSurahNumber ?: 1
        val prev = if (current <= 1) 114 else current - 1
        playSurahAudio(prev)
    }

    fun seekAudio(positionMs: Long) {
        audioPlayer.seekTo(positionMs)
    }

    fun setAudioRepeat(count: Int) {
        audioPlayer.setRepeatCount(count)
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
            val uid = _uiState.value.currentUser?.uid ?: ""
            if (uid.isNotBlank()) {
                if (isBookmarked) {
                    FirebaseSyncManager.deleteBookmark(uid, type, refId, secId)
                } else {
                    FirebaseSyncManager.saveBookmark(
                        uid,
                        Bookmark(type = type, referenceId = refId, secondaryId = secId, title = title, subtitle = subtitle)
                    )
                }
            }
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
