package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.QuranRepository
import com.example.data.model.Surah
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.util.AudioPlaybackState
import com.example.ui.viewmodel.MuslimUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranScreen(
    uiState: MuslimUiState,
    audioState: AudioPlaybackState,
    onSearchChange: (String) -> Unit,
    onPlaySurah: (Int) -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onToggleBookmark: (String, Int, Int, String, String) -> Unit,
    isBookmarked: (String, Int, Int) -> Boolean,
    modifier: Modifier = Modifier
) {
    var selectedSurah by remember { mutableStateOf<Surah?>(null) }
    var selectedTab by remember { mutableStateOf("All") } // "All", "Makki", "Madani", "Bookmarked"

    val context = LocalContext.current

    BackHandler(enabled = selectedSurah != null) {
        selectedSurah = null
    }

    if (selectedSurah != null) {
        // Surah Reader View
        SurahReaderView(
            surah = selectedSurah!!,
            audioState = audioState,
            onBack = { selectedSurah = null },
            onPlaySurah = { onPlaySurah(selectedSurah!!.number) },
            onPauseAudio = onPauseAudio,
            onResumeAudio = onResumeAudio,
            onToggleBookmark = onToggleBookmark,
            isBookmarked = isBookmarked,
            modifier = modifier
        )
    } else {
        // Surah Browser List
        val filteredSurahs = remember(uiState.quranSearchQuery, selectedTab) {
            val q = uiState.quranSearchQuery.trim().lowercase()
            QuranRepository.ALL_SURAHS.filter { surah ->
                val matchesQuery = q.isEmpty() ||
                        surah.nameEnglish.lowercase().contains(q) ||
                        surah.englishMeaning.lowercase().contains(q) ||
                        surah.nameArabic.contains(q) ||
                        surah.number.toString() == q

                val matchesTab = when (selectedTab) {
                    "Makki" -> surah.revelationType == "Makki"
                    "Madani" -> surah.revelationType == "Madani"
                    "Bookmarked" -> isBookmarked("QURAN_SURAH", surah.number, 0)
                    else -> true
                }
                matchesQuery && matchesTab
            }
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("quran_browser_screen"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp)
        ) {
            item {
                Text(
                    text = "The Holy Quran",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "القرآن الكريم • 114 Surahs with Mishary Alafasy Recitation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = uiState.quranSearchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quran_search_field"),
                    placeholder = { Text("Search Surah name or number...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (uiState.quranSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Makki", "Madani", "Bookmarked").forEach { tab ->
                        val isSelected = selectedTab == tab
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTab = tab },
                            label = { Text(tab) },
                            modifier = Modifier.testTag("quran_chip_$tab"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Audio Player Active Banner
            if (audioState.currentSurahOrDuaId != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Audio playing",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = audioState.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = if (audioState.isLoading) "Loading stream..." else if (audioState.isPlaying) "Playing now" else "Paused",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            if (audioState.isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                IconButton(
                                    onClick = {
                                        if (audioState.isPlaying) onPauseAudio() else onResumeAudio()
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (audioState.isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                        contentDescription = "Play/Pause",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            items(filteredSurahs, key = { it.number }) { surah ->
                SurahItemCard(
                    surah = surah,
                    isPlaying = audioState.currentSurahOrDuaId == surah.number && audioState.isPlaying,
                    isBookmarked = isBookmarked("QURAN_SURAH", surah.number, 0),
                    onClick = { selectedSurah = surah },
                    onPlayAudio = { onPlaySurah(surah.number) },
                    onToggleBookmark = {
                        onToggleBookmark(
                            "QURAN_SURAH",
                            surah.number,
                            0,
                            surah.nameEnglish,
                            "${surah.nameArabic} • ${surah.totalAyahs} Ayahs"
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SurahItemCard(
    surah: Surah,
    isPlaying: Boolean,
    isBookmarked: Boolean,
    onClick: () -> Unit,
    onPlayAudio: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("surah_card_${surah.number}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Surah Number Diamond/Circle Badge
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isPlaying) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${surah.number}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isPlaying) Color.White else MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = surah.nameEnglish,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${surah.englishMeaning} • ${surah.totalAyahs} Ayahs • ${surah.revelationType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = surah.nameArabic,
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )

                IconButton(
                    onClick = onPlayAudio,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.PauseCircle else Icons.Outlined.PlayCircle,
                        contentDescription = "Play recitation",
                        tint = if (isPlaying) EmeraldPrimary else MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) GoldSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SurahReaderView(
    surah: Surah,
    audioState: AudioPlaybackState,
    onBack: () -> Unit,
    onPlaySurah: () -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onToggleBookmark: (String, Int, Int, String, String) -> Unit,
    isBookmarked: (String, Int, Int) -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val verses = remember(surah.number) { QuranRepository.getVersesForSurah(surah.number) }
    val isPlayingThisSurah = audioState.currentSurahOrDuaId == surah.number && audioState.isPlaying

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("surah_reader_view"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp)
    ) {
        item {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("surah_reader_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Surah ${surah.nameEnglish}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${surah.englishMeaning} • ${surah.totalAyahs} Verses (${surah.revelationType})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        if (isPlayingThisSurah) onPauseAudio() else onPlaySurah()
                    }
                ) {
                    Icon(
                        imageVector = if (isPlayingThisSurah) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = "Audio Recitation",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Surah Card Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = surah.nameArabic,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Reciter: Sheikh Mishary Rashid Alafasy",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )

                    if (surah.number != 9) { // At-Tawbah does not start with Bismillah
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Verse items
        items(verses, key = { it.ayahNumber }) { ayah ->
            val ayahBookmarked = isBookmarked("QURAN_AYAH", surah.number, ayah.ayahNumber)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("ayah_${surah.number}_${ayah.ayahNumber}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Ayah number & action bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${ayah.ayahNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Ayah", "${ayah.textArabic}\n\n${ayah.textEnglish} [${surah.nameEnglish} ${surah.number}:${ayah.ayahNumber}]")
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Ayah copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Ayah",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    onToggleBookmark(
                                        "QURAN_AYAH",
                                        surah.number,
                                        ayah.ayahNumber,
                                        "${surah.nameEnglish} [${surah.number}:${ayah.ayahNumber}]",
                                        ayah.textEnglish.take(60) + "..."
                                    )
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (ayahBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (ayahBookmarked) GoldSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Arabic Text
                    Text(
                        text = ayah.textArabic,
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp, lineHeight = 36.sp),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Transliteration
                    Text(
                        text = ayah.textTransliteration,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // English Translation
                    Text(
                        text = ayah.textEnglish,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
