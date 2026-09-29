package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
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
    onSaveReadingPosition: (surahNumber: Int, ayahNumber: Int, surahName: String) -> Unit = { _, _, _ -> },
    onAddNote: (surahNumber: Int, ayahNumber: Int, surahName: String, text: String) -> Unit = { _, _, _, _ -> },
    onShowPaywall: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedPara by remember { mutableStateOf<Para?>(null) }
    var selectedSurah by remember { mutableStateOf<Surah?>(null) }
    var primaryQuranTab by remember { mutableStateOf("Paras") } // "Paras", "Surahs", "Khatam", "Memorize"
    var surahTypeFilter by remember { mutableStateOf("All") } // "All", "Makki", "Madani", "Saved"

    // System Back Handler through the Quran -> Para -> Surah hierarchy
    BackHandler(enabled = selectedSurah != null || selectedPara != null) {
        if (selectedSurah != null) {
            selectedSurah = null
        } else if (selectedPara != null) {
            selectedPara = null
        }
    }

    if (selectedSurah != null) {
        // Deepest level: Ayat Reader
        SurahReaderView(
            surah = selectedSurah!!,
            audioState = audioState,
            isPremium = uiState.isPremium,
            selectedReciter = uiState.selectedReciter,
            onBack = { selectedSurah = null },
            onPlaySurah = { onPlaySurah(selectedSurah!!.number) },
            onPauseAudio = onPauseAudio,
            onResumeAudio = onResumeAudio,
            onToggleBookmark = onToggleBookmark,
            isBookmarked = isBookmarked,
            onSaveReadingPosition = onSaveReadingPosition,
            onAddNote = onAddNote,
            onShowPaywall = onShowPaywall,
            modifier = modifier
        )
    } else if (selectedPara != null) {
        // Mid level: Surahs inside selected Para
        val surahsInPara = remember(selectedPara) {
            QuranRepository.getSurahsForPara(selectedPara!!.number)
        }
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .testTag("surahs_in_para_screen"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { selectedPara = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Paras")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Para ${selectedPara!!.number} • ${selectedPara!!.nameEnglish}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedPara!!.nameArabic} • Starting at Surah ${selectedPara!!.startSurahName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(surahsInPara) { surah ->
                SurahItemCard(
                    surah = surah,
                    isPlaying = audioState.currentSurahNumber == surah.number && audioState.isPlaying,
                    isBookmarked = isBookmarked("QURAN_SURAH", surah.number, 0),
                    onClick = {
                        selectedSurah = surah
                        onSaveReadingPosition(surah.number, 1, surah.nameEnglish)
                    },
                    onBookmarkClick = {
                        onToggleBookmark("QURAN_SURAH", surah.number, 0, "Surah ${surah.nameEnglish}", surah.nameArabic)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    } else {
        // Top level: Quran Hub (Paras, Surahs, Khatam Planner, Memorization)
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .testTag("quran_hub_screen"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                Text(
                    text = "The Holy Quran (القرآن الكريم)",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "30 Paras (Juz) • 114 Surahs • Arabic Text & Multi-Language Translations",
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
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (uiState.quranSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Primary Flow Tabs: Paras | Surahs | Khatam | Memorize
                ScrollableTabRow(
                    selectedTabIndex = listOf("Paras", "Surahs", "Khatam", "Memorize").indexOf(primaryQuranTab),
                    edgePadding = 0.dp,
                    divider = {},
                    containerColor = Color.Transparent
                ) {
                    Tab(
                        selected = primaryQuranTab == "Paras",
                        onClick = { primaryQuranTab = "Paras" },
                        text = { Text("30 Paras (Juz)", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = primaryQuranTab == "Surahs",
                        onClick = { primaryQuranTab = "Surahs" },
                        text = { Text("114 Surahs", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = primaryQuranTab == "Khatam",
                        onClick = { primaryQuranTab = "Khatam" },
                        text = { Text("Khatam Planner", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = primaryQuranTab == "Memorize",
                        onClick = { primaryQuranTab = "Memorize" },
                        text = { Text("Memorize", fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            when (primaryQuranTab) {
                "Paras" -> {
                    items(QuranRepository.ALL_PARAS) { para ->
                        ParaItemCard(
                            para = para,
                            onClick = { selectedPara = para }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                "Surahs" -> {
                    item {
                        // Sub Filter Chips (All, Makki, Madani, Saved)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Makki", "Madani", "Saved").forEach { tab ->
                                val isSelected = surahTypeFilter == tab
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { surahTypeFilter = tab },
                                    label = { Text(tab) },
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }
                        }
                    }

                    val filteredSurahs = QuranRepository.ALL_SURAHS.filter { surah ->
                        val q = uiState.quranSearchQuery.trim().lowercase()
                        val matchesQuery = q.isEmpty() ||
                                surah.nameEnglish.lowercase().contains(q) ||
                                surah.englishMeaning.lowercase().contains(q) ||
                                surah.nameArabic.contains(q) ||
                                surah.number.toString() == q
                        val matchesFilter = when (surahTypeFilter) {
                            "Makki" -> surah.revelationType == "Makki"
                            "Madani" -> surah.revelationType == "Madani"
                            "Saved" -> isBookmarked("QURAN_SURAH", surah.number, 0)
                            else -> true
                        }
                        matchesQuery && matchesFilter
                    }

                    items(filteredSurahs) { surah ->
                        SurahItemCard(
                            surah = surah,
                            isPlaying = audioState.currentSurahNumber == surah.number && audioState.isPlaying,
                            isBookmarked = isBookmarked("QURAN_SURAH", surah.number, 0),
                            onClick = {
                                selectedSurah = surah
                                onSaveReadingPosition(surah.number, 1, surah.nameEnglish)
                            },
                            onBookmarkClick = {
                                onToggleBookmark("QURAN_SURAH", surah.number, 0, "Surah ${surah.nameEnglish}", surah.nameArabic)
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                "Khatam" -> {
                    item {
                        KhatamPlannerCard(
                            isPremium = uiState.isPremium,
                            onShowPaywall = onShowPaywall
                        )
                    }
                }
                "Memorize" -> {
                    item {
                        MemorizationModeCard(
                            isPremium = uiState.isPremium,
                            onShowPaywall = onShowPaywall,
                            onStartSurah = { surahNum ->
                                selectedSurah = QuranRepository.ALL_SURAHS.find { it.number == surahNum }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ParaItemCard(
    para: Para,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("para_item_${para.number}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${para.number}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "Para ${para.number} • ${para.nameEnglish}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Starts: Surah ${para.startSurahName} (Ayah ${para.startAyahNumber})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = para.nameArabic,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
                Text(
                    text = "${para.surahsInPara.size} Surahs",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
    onBookmarkClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("surah_item_${surah.number}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
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
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isPlaying) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${surah.number}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isPlaying) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = surah.nameEnglish,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (isPlaying) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "🎵", fontSize = 12.sp)
                        }
                    }
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
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = onBookmarkClick, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) EmeraldPrimary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SurahReaderView(
    surah: Surah,
    audioState: AudioPlaybackState,
    isPremium: Boolean,
    selectedReciter: String,
    onBack: () -> Unit,
    onPlaySurah: () -> Unit,
    onPauseAudio: () -> Unit,
    onResumeAudio: () -> Unit,
    onToggleBookmark: (String, Int, Int, String, String) -> Unit,
    isBookmarked: (String, Int, Int) -> Boolean,
    onSaveReadingPosition: (Int, Int, String) -> Unit,
    onAddNote: (Int, Int, String, String) -> Unit,
    onShowPaywall: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val verses = remember(surah.number) { QuranRepository.getVersesForSurah(surah.number) }

    var selectedLanguage by remember { mutableStateOf(QuranTranslationLanguage.ENGLISH) }
    var fontSizeSp by remember { mutableFloatStateOf(22f) }
    var showTranslation by remember { mutableStateOf(true) }
    var repeatCount by remember { mutableIntStateOf(1) } // 1x, 5x, 10x
    var noteDialogAyah by remember { mutableStateOf<Ayah?>(null) }
    var noteText by remember { mutableStateOf("") }

    val isPlayingThisSurah = audioState.currentSurahNumber == surah.number && audioState.isPlaying

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Surah ${surah.nameEnglish} (${surah.nameArabic})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${surah.revelationType} • ${surah.totalAyahs} Ayahs • Juz ${surah.juz}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Audio Play/Pause Button
                    IconButton(
                        onClick = {
                            if (isPlayingThisSurah) onPauseAudio()
                            else if (audioState.currentSurahNumber == surah.number) onResumeAudio()
                            else onPlaySurah()
                        }
                    ) {
                        Icon(
                            imageVector = if (isPlayingThisSurah) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                            contentDescription = "Audio Recitation",
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 18.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Reader Settings Bar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Translation Language",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Hide Translation", style = MaterialTheme.typography.labelSmall)
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = showTranslation,
                                    onCheckedChange = { showTranslation = it },
                                    modifier = Modifier.height(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Language Selector Chips (English, Urdu, Hindi, Hinglish)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(QuranTranslationLanguage.values()) { lang ->
                                FilterChip(
                                    selected = selectedLanguage == lang,
                                    onClick = { selectedLanguage = lang },
                                    label = { Text(lang.displayName) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Font Size Slider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "A", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Slider(
                                value = fontSizeSp,
                                onValueChange = { fontSizeSp = it },
                                valueRange = 18f..32f,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )
                            Text(text = "A", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }

                        // Audio Repeat Selection (Premium Quran feature)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recitation Repeat: ${repeatCount}x",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf(1, 5, 10).forEach { count ->
                                    Surface(
                                        onClick = {
                                            if (count > 1 && !isPremium) {
                                                onShowPaywall("Repeat Recitation (5x & 10x)")
                                            } else {
                                                repeatCount = count
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (repeatCount == count) EmeraldPrimary else MaterialTheme.colorScheme.surface
                                    ) {
                                        Text(
                                            text = "${count}x",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (repeatCount == count) Color.White else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Ayat List
            items(verses) { ayah ->
                val ayahBookmarked = isBookmarked("QURAN_AYAH", surah.number, ayah.ayahNumber)
                AyahCard(
                    ayah = ayah,
                    fontSizeSp = fontSizeSp,
                    showTranslation = showTranslation,
                    translationLanguage = selectedLanguage,
                    isBookmarked = ayahBookmarked,
                    onToggleBookmark = {
                        onToggleBookmark(
                            "QURAN_AYAH",
                            surah.number,
                            ayah.ayahNumber,
                            "Surah ${surah.nameEnglish} • Ayah ${ayah.ayahNumber}",
                            ayah.textArabic.take(30) + "..."
                        )
                    },
                    onAddNote = {
                        if (!isPremium) {
                            onShowPaywall("Personal Quran Ayah Notes")
                        } else {
                            noteDialogAyah = ayah
                            noteText = ""
                        }
                    },
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            "Surah ${surah.nameEnglish} Ayah ${ayah.ayahNumber}",
                            "${ayah.textArabic}\n\n${ayah.getTranslation(selectedLanguage)}\n\n[Surah ${surah.nameEnglish} ${surah.number}:${ayah.ayahNumber}]"
                        )
                        clipboard.setPrimaryClip(clip)
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }

    // Personal Note Dialog
    if (noteDialogAyah != null) {
        AlertDialog(
            onDismissRequest = { noteDialogAyah = null },
            title = { Text("Add Note • Ayah ${noteDialogAyah!!.ayahNumber}") },
            text = {
                Column {
                    Text(
                        text = "Add your personal reflections, tafsir notes, or study tags to this verse.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("Write personal reflection...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            onAddNote(surah.number, noteDialogAyah!!.ayahNumber, surah.nameEnglish, noteText.trim())
                            noteDialogAyah = null
                        }
                    }
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { noteDialogAyah = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AyahCard(
    ayah: Ayah,
    fontSizeSp: Float,
    showTranslation: Boolean,
    translationLanguage: QuranTranslationLanguage,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onAddNote: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ayah_item_${ayah.ayahNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Verse Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${ayah.surahNumber}:${ayah.ayahNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onAddNote, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.EditNote, contentDescription = "Add Note", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) EmeraldPrimary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic Text
            Text(
                text = ayah.textArabic,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = fontSizeSp.sp,
                    lineHeight = (fontSizeSp * 1.6f).sp
                ),
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // Translation
            if (showTranslation) {
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = ayah.getTranslation(translationLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (ayah.textTransliteration.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ayah.textTransliteration,
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun KhatamPlannerCard(
    isPremium: Boolean,
    onShowPaywall: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📖", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Quran Khatam Planner",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldSecondary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "PRO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC5A059),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Complete reading the entire Holy Quran (604 pages) with customized daily schedules and progress milestones.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 30 Days Target
            LinearProgressIndicator(
                progress = { 45f / 604f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = EmeraldPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Completed: 45 / 604 pages", style = MaterialTheme.typography.labelMedium)
                Text(text = "Daily Target: 20 pages", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    if (!isPremium) {
                        onShowPaywall("Quran Khatam Planner")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isPremium) "Update Khatam Schedule" else "Unlock Khatam Planner (Pro)")
            }
        }
    }
}

@Composable
private fun MemorizationModeCard(
    isPremium: Boolean,
    onShowPaywall: (String) -> Unit,
    onStartSurah: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🧠", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hifz & Memorization Mode",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldSecondary.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "PRO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC5A059),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tested spaced-repetition techniques: conceal Arabic or English verses, test your recall, and loop audio recitations.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Surah selections for Hifz
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Al-Mulk" to 67, "Ya-Sin" to 36, "Al-Kahf" to 18).forEach { (name, num) ->
                    OutlinedButton(
                        onClick = {
                            if (!isPremium) onShowPaywall("Quran Memorization Mode")
                            else onStartSurah(num)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(name, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
