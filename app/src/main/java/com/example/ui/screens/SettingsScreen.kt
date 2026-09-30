package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalculationMethod
import com.example.data.model.CityLocation
import com.example.data.model.JuristicMethod
import com.example.data.model.PrayerCalculator
import com.example.data.model.QuranTranslationLanguage
import com.example.ui.theme.AppThemeType
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.MuslimUiState

@Composable
fun SettingsScreen(
    uiState: MuslimUiState,
    onSelectCity: (CityLocation) -> Unit,
    onSetMethod: (CalculationMethod) -> Unit,
    onSetJuristic: (JuristicMethod) -> Unit,
    onSetTheme: (AppThemeType) -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onOpenAdmin: () -> Unit = {},
    onOpenPaywall: () -> Unit = {},
    onRestorePurchases: () -> Unit = {},
    onSelectReciter: (String) -> Unit = {},
    onSelectAdhan: (String) -> Unit = {},
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showCityDialog by remember { mutableStateOf(false) }
    var showMethodDialog by remember { mutableStateOf(false) }
    var showJuristicDialog by remember { mutableStateOf(false) }
    var showReciterDialog by remember { mutableStateOf(false) }
    var showAdhanDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    // Notification Toggles State
    var prayerNotifsEnabled by remember { mutableStateOf(true) }
    var dailyQuranReminder by remember { mutableStateOf(true) }
    var dailyHadithReminder by remember { mutableStateOf(true) }
    var dailyDuaReminder by remember { mutableStateOf(true) }
    var tahajjudReminder by remember { mutableStateOf(false) }

    val recitersList = listOf(
        "Sheikh Mishary Rashid Alafasy",
        "Sheikh Abdul Basit Abdul Samad",
        "Sheikh Abdur-Rahman As-Sudais",
        "Sheikh Saud Ash-Shuraim",
        "Sheikh Saad Al-Ghamdi"
    )

    val adhanList = listOf(
        "Makkah Al-Mukarramah Adhan",
        "Madinah Al-Munawwarah Adhan",
        "Al-Aqsa Mosque (Jerusalem) Adhan",
        "Cairo Al-Azhar Historic Adhan"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text(
                text = "Settings & Preferences",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Personalize your Muslim Ummah experience",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Appearance & Themes (Section 13)
        item {
            SettingsCategoryHeader(title = "Appearance & Themes", icon = Icons.Default.Palette)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Visual Theme",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choose your preferred aesthetic palette",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(AppThemeType.values()) { theme ->
                            val isSelected = uiState.currentTheme == theme
                            Surface(
                                onClick = {
                                    if (theme.isPremium && !uiState.isPremium) {
                                        onOpenPaywall()
                                    } else {
                                        onSetTheme(theme)
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = theme.primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    ) {}
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = theme.title,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (theme.isPremium) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "👑", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Account & Subscription Status
        item {
            SettingsCategoryHeader(title = "Account & Subscription", icon = Icons.Default.AccountCircle)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = uiState.currentUser?.displayName ?: "Guest User",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.currentUser?.email ?: "Not signed in",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (uiState.isPremium) GoldSecondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (uiState.isPremium) "👑 PRO ACTIVE" else "FREE TIER",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isPremium) Color(0xFFC5A059) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onOpenAuth,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Manage Account")
                        }
                        OutlinedButton(
                            onClick = onRestorePurchases,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Restore Purchases")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Prayer Timing & Juristic Options
        item {
            SettingsCategoryHeader(title = "Prayer Calculation", icon = Icons.Default.AccessTime)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column {
                    SettingsRowItem(
                        title = "Location",
                        subtitle = "${uiState.selectedCity.name}, ${uiState.selectedCity.country}",
                        icon = Icons.Default.LocationOn,
                        onClick = { showCityDialog = true }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsRowItem(
                        title = "Calculation Method",
                        subtitle = uiState.calculationMethod.title,
                        icon = Icons.Default.Calculate,
                        onClick = { showMethodDialog = true }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsRowItem(
                        title = "Asr Juristic Rule",
                        subtitle = uiState.juristicMethod.title,
                        icon = Icons.Default.Balance,
                        onClick = { showJuristicDialog = true }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsRowItem(
                        title = "Adhan Voice",
                        subtitle = uiState.selectedAdhanSound,
                        icon = Icons.Default.NotificationsActive,
                        onClick = { showAdhanDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Notification Preferences
        item {
            SettingsCategoryHeader(title = "Notifications & Reminders", icon = Icons.Default.Notifications)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    NotificationToggleRow(
                        title = "Prayer Adhan Notifications",
                        subtitle = "Alert at Fajr, Dhuhr, Asr, Maghrib, Isha",
                        checked = prayerNotifsEnabled,
                        onCheckedChange = { prayerNotifsEnabled = it }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                    NotificationToggleRow(
                        title = "Daily Quran Ayah Reminder",
                        subtitle = "Daily inspiration verse morning reminder",
                        checked = dailyQuranReminder,
                        onCheckedChange = { dailyQuranReminder = it }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                    NotificationToggleRow(
                        title = "Daily Hadith Reminder",
                        subtitle = "Prophetic wisdom from Kutub al-Sittah",
                        checked = dailyHadithReminder,
                        onCheckedChange = { dailyHadithReminder = it }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 8.dp))
                    NotificationToggleRow(
                        title = "Tahajjud & Night Vigil Reminder",
                        subtitle = "Pre-Fajr wake up notification",
                        checked = tahajjudReminder,
                        onCheckedChange = { tahajjudReminder = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Quran & Reciters
        item {
            SettingsCategoryHeader(title = "Quran & Audio Recitation", icon = Icons.Default.RecordVoiceOver)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column {
                    SettingsRowItem(
                        title = "Quran Reciter",
                        subtitle = uiState.selectedReciter,
                        icon = Icons.Default.Mic,
                        onClick = { showReciterDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Privacy Policy & Terms
        item {
            SettingsCategoryHeader(title = "About & Privacy", icon = Icons.Default.Info)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column {
                    SettingsRowItem(
                        title = "About Muslim Ummah",
                        subtitle = "Developer Mohd Aqif Farooqui • Authentic Islamic companion",
                        icon = Icons.Default.Info,
                        onClick = { showAboutDialog = true }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsRowItem(
                        title = "Privacy Policy & Terms",
                        subtitle = "Offline-first, zero tracking, secure data handling",
                        icon = Icons.Default.Shield,
                        onClick = { showPrivacyDialog = true }
                    )
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsRowItem(
                        title = "App Version",
                        subtitle = "Muslim Ummah v1.0.0 (Production)",
                        icon = Icons.Default.Smartphone,
                        onClick = { showAboutDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }

        // Account & Authentication Section
        item {
            val user = uiState.currentUser
            SettingsCategoryHeader(title = "Account & Authentication", icon = Icons.Default.AccountCircle)
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary,
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = user?.displayName?.take(1)?.uppercase() ?: "U",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user?.displayName ?: "Google User",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user?.email ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Status: ${user?.planType ?: "Free"} • Cloud Sync Active",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onSignOut,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Log Out", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out from Muslim Ummah", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Dialogs
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("Select City") },
            text = {
                LazyColumn(modifier = Modifier.height(260.dp)) {
                    items(PrayerCalculator.POPULAR_CITIES) { city ->
                        Surface(
                            onClick = {
                                onSelectCity(city)
                                showCityDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = if (city.name == uiState.selectedCity.name) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ) {
                            Text(
                                text = "${city.name}, ${city.country}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showCityDialog = false }) { Text("Close") } }
        )
    }

    if (showMethodDialog) {
        AlertDialog(
            onDismissRequest = { showMethodDialog = false },
            title = { Text("Calculation Method") },
            text = {
                LazyColumn(modifier = Modifier.height(260.dp)) {
                    items(CalculationMethod.values()) { method ->
                        Surface(
                            onClick = {
                                onSetMethod(method)
                                showMethodDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = if (method == uiState.calculationMethod) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ) {
                            Text(
                                text = method.title,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showMethodDialog = false }) { Text("Close") } }
        )
    }

    if (showJuristicDialog) {
        AlertDialog(
            onDismissRequest = { showJuristicDialog = false },
            title = { Text("Asr Juristic Method") },
            text = {
                Column {
                    JuristicMethod.values().forEach { juristic ->
                        Surface(
                            onClick = {
                                onSetJuristic(juristic)
                                showJuristicDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = if (juristic == uiState.juristicMethod) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ) {
                            Text(
                                text = juristic.title,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showJuristicDialog = false }) { Text("Close") } }
        )
    }

    if (showReciterDialog) {
        AlertDialog(
            onDismissRequest = { showReciterDialog = false },
            title = { Text("Select Quran Reciter") },
            text = {
                Column {
                    recitersList.forEach { reciter ->
                        Surface(
                            onClick = {
                                onSelectReciter(reciter)
                                showReciterDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = if (reciter == uiState.selectedReciter) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ) {
                            Text(
                                text = reciter,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showReciterDialog = false }) { Text("Close") } }
        )
    }

    if (showAdhanDialog) {
        AlertDialog(
            onDismissRequest = { showAdhanDialog = false },
            title = { Text("Select Adhan Sound") },
            text = {
                Column {
                    adhanList.forEach { adhan ->
                        Surface(
                            onClick = {
                                onSelectAdhan(adhan)
                                showAdhanDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            color = if (adhan == uiState.selectedAdhanSound) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                        ) {
                            Text(
                                text = adhan,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAdhanDialog = false }) { Text("Close") } }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy & Terms") },
            text = {
                Column {
                    Text(
                        text = "Muslim Ummah is built with deep reverence for user privacy:\n\n" +
                                "• Offline-First: Prayer times, Holy Quran, Hadiths, and Tasbih counters work without internet access.\n" +
                                "• Zero Unnecessary Data: Location coordinates are processed locally on-device via Android GPS services for astronomical prayer and Qibla calculation.\n" +
                                "• Authenticity: All Quran texts and canonical Hadiths are preserved with rigorous verification.\n" +
                                "• Cloud Sync: Secure cloud synchronization for bookmarks and reading positions is encrypted.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = { TextButton(onClick = { showPrivacyDialog = false }) { Text("Understood") } }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Mosque, contentDescription = null, tint = GoldSecondary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "About Muslim Ummah",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldPrimary.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Developer",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Mohd Aqif Farooqui",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Lead Android Architect & Islamic Software Engineer",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Purpose & Mission",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Muslim Ummah (أمة المسلمين) is built as a pure, reverent, and distractions-free digital Islamic sanctuary. Designed with modern Android architecture and native Jetpack Compose to accompany every believer in their daily prayers, Quranic study, Hadith contemplation, and personal remembrance of Allah (SWT).",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Text(
                            text = "Authentic Core Features",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Complete Holy Quran: All 114 Surahs, 30 Juz/Paras, verified Uthmani script with translations (English, Urdu, Hindi, Hinglish), audio reciter streaming, Mushaf page view, and continue-reading memory.\n" +
                                   "• Canonical Hadith Library: Complete Kutub al-Sittah collections (Sahih al-Bukhari, Sahih Muslim, Sunan an-Nasa'i, Sunan Abi Dawud, Jami` at-Tirmidhi, Sunan Ibn Majah) with chapter navigation, multilingual translations, and full-text search.\n" +
                                   "• Astronomical Prayer Times: High-precision astronomical calculations with location-aware countdowns, next prayer tracking, and adhan reminders.\n" +
                                   "• Precision Qibla Compass: Device sensor magnetics and accelerometer calibrated to the Holy Kaaba in Makkah.\n" +
                                   "• Authentic Daily Duas & Ruqyah: Hisn al-Muslim supplications with Arabic, transliterations, translations, and category search.\n" +
                                   "• 99 Names of Allah (Asma-ul-Husna): Arabic text, meanings, and spiritual benefits.\n" +
                                   "• Digital Tasbih: Tactile haptic-enabled counter with target intervals and local persistence.\n" +
                                   "• Worship & Progress Tracking (My Journey): Daily Salah tracker, Fasting/Sawm log, Ramadan tracker, and Qada calculator.\n" +
                                   "• Source-Grounded Islamic AI: AI assistant with strict Quranic and Hadith citations.",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Text(
                            text = "Premium Experience Overview",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Muslim Ummah offers 5 clear subscription plans (7 Days: ₹49, 1 Month: ₹129, 3 Months: ₹299, 9 Months: ₹649, 1 Year: ₹799) unlocking 100% ad-free experience, unlimited source-grounded Islamic AI, multiple world-class Quran reciters, advanced study tools, and cross-device cloud sync.",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Text(
                            text = "Credits & Attributions",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Quran text & translations: Tanzil Project & King Fahd Quran Printing Complex (KFQPC).\n" +
                                   "• Hadith texts: Shamela & Sunnah.com corpus archives.\n" +
                                   "• Prayer calculation algorithms: PrayTimes.org astronomical formulas.\n" +
                                   "• Audio recitations: EveryAyah.com & Quran Central open repositories.",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item {
                        Text(
                            text = "Version: 1.0.0 (Build 1) • Production",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun SettingsCategoryHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = title, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingsRowItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Open", tint = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
