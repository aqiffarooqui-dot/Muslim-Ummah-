package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.HadithBook
import com.example.data.model.HadithChapter
import com.example.data.model.HadithItem
import com.example.data.model.HadithRepository
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.MuslimUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HadithScreen(
    uiState: MuslimUiState,
    onToggleBookmark: (type: String, refId: Int, secId: Int, title: String, subtitle: String) -> Unit,
    isBookmarked: (type: String, refId: Int, secId: Int) -> Boolean,
    onSaveReadingPosition: (bookId: String, chapterName: String, hadithId: Int, hadithNumber: String) -> Unit = { _, _, _, _ -> },
    onAddNote: (bookId: String, hadithId: Int, text: String) -> Unit = { _, _, _ -> },
    onShowPaywall: (String) -> Unit = {},
    onShowStatus: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedBook by remember { mutableStateOf<HadithBook?>(null) }
    var selectedChapter by remember { mutableStateOf<HadithChapter?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var studyModeEnabled by remember { mutableStateOf(false) }
    var showNoteDialogForHadith by remember { mutableStateOf<HadithItem?>(null) }
    var noteInputText by remember { mutableStateOf("") }

    // System Back Handler for Collection -> Chapters -> Hadiths hierarchy
    BackHandler(enabled = selectedChapter != null || selectedBook != null) {
        if (selectedChapter != null) {
            selectedChapter = null
        } else if (selectedBook != null) {
            selectedBook = null
        }
    }

    // LEVEL 3: Hadiths within selected Chapter
    if (selectedChapter != null && selectedBook != null) {
        val hadithsInChapter = remember(selectedChapter) {
            HadithRepository.getHadithsForChapter(selectedChapter!!.id).ifEmpty {
                HadithRepository.getHadithsForBook(selectedBook!!.id)
            }
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .testTag("hadith_chapter_reader_screen"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedChapter = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to chapters")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedChapter!!.nameEnglish,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedBook!!.nameEnglish} • ${selectedChapter!!.nameArabic}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Study Mode Toggle
                    FilterChip(
                        selected = studyModeEnabled,
                        onClick = { studyModeEnabled = !studyModeEnabled },
                        label = { Text(if (studyModeEnabled) "Study Mode ON" else "Study Mode") },
                        leadingIcon = {
                            Icon(
                                if (studyModeEnabled) Icons.Default.CheckCircle else Icons.Default.School,
                                contentDescription = "Study mode",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            items(hadithsInChapter, key = { it.id }) { hadith ->
                val bookmarked = isBookmarked("HADITH", hadith.id, 0)
                
                // Track reading position when viewed
                LaunchedEffect(hadith.id) {
                    onSaveReadingPosition(
                        hadith.bookId,
                        hadith.chapterName,
                        hadith.id,
                        hadith.hadithNumber
                    )
                }

                HadithDetailCard(
                    hadith = hadith,
                    isBookmarked = bookmarked,
                    isStudyMode = studyModeEnabled,
                    onToggleBookmark = {
                        onToggleBookmark(
                            "HADITH",
                            hadith.id,
                            0,
                            hadith.bookName,
                            "${hadith.hadithNumber} - ${hadith.chapterName}"
                        )
                    },
                    onCopy = {
                        copyHadithToClipboard(context, hadith, onShowStatus)
                    },
                    onShare = {
                        shareHadithText(context, hadith)
                    },
                    onAddNoteClick = {
                        if (!uiState.isPremium) {
                            onShowPaywall("Hadith Study Notes")
                        } else {
                            showNoteDialogForHadith = hadith
                            noteInputText = ""
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
    // LEVEL 2: Chapters within selected Collection
    else if (selectedBook != null) {
        val chapters = remember(selectedBook) {
            HadithRepository.getChaptersForBook(selectedBook!!.id)
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .testTag("hadith_chapters_screen"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedBook = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to collections")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selectedBook!!.nameEnglish,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedBook!!.nameArabic} • ${selectedBook!!.hadithCountApprox}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = selectedBook!!.author,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = selectedBook!!.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Chapters (أبواب الحديث)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (chapters.isEmpty()) {
                item {
                    val allBookHadiths = HadithRepository.getHadithsForBook(selectedBook!!.id)
                    Text(
                        text = "Viewing ${allBookHadiths.size} preserved Hadiths in this collection:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                val allBookHadiths = HadithRepository.getHadithsForBook(selectedBook!!.id)
                items(allBookHadiths) { hadith ->
                    HadithDetailCard(
                        hadith = hadith,
                        isBookmarked = isBookmarked("HADITH", hadith.id, 0),
                        isStudyMode = studyModeEnabled,
                        onToggleBookmark = {
                            onToggleBookmark("HADITH", hadith.id, 0, hadith.bookName, hadith.hadithNumber)
                        },
                        onCopy = { copyHadithToClipboard(context, hadith, onShowStatus) },
                        onShare = { shareHadithText(context, hadith) },
                        onAddNoteClick = {
                            if (!uiState.isPremium) onShowPaywall("Hadith Notes")
                            else { showNoteDialogForHadith = hadith; noteInputText = "" }
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                items(chapters, key = { it.id }) { chapter ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedChapter = chapter },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = chapter.id.toString(),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = chapter.nameEnglish,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${chapter.nameArabic} • ${chapter.hadithCount} traditions",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open chapter",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
    // LEVEL 1: Hadith Home with Collections & Continue Reading
    else {
        val filteredHadiths = remember(searchQuery) {
            val q = searchQuery.trim().lowercase()
            if (q.isEmpty()) emptyList()
            else {
                HadithRepository.ALL_HADITHS.filter { hadith ->
                    hadith.englishTranslation.lowercase().contains(q) ||
                            hadith.urduTranslation.contains(q) ||
                            hadith.hindiTranslation.lowercase().contains(q) ||
                            hadith.arabicText.contains(q) ||
                            hadith.narrator.lowercase().contains(q) ||
                            hadith.chapterName.lowercase().contains(q) ||
                            hadith.bookName.lowercase().contains(q) ||
                            hadith.hadithNumber.lowercase().contains(q)
                }
            }
        }

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .testTag("hadith_screen"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Kutub al-Hadith (كتب الحديث)",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Authentic Sunnah from Kutub al-Sittah & classical scholars",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Continue Reading Card
                if (uiState.lastHadithPosition != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val lastBook = HadithRepository.MAIN_BOOKS.find { it.id == uiState.lastHadithPosition.bookId }
                                    ?: HadithRepository.MAIN_BOOKS.first()
                                val lastChap = HadithRepository.getChaptersForBook(lastBook.id).firstOrNull()
                                selectedBook = lastBook
                                if (lastChap != null) selectedChapter = lastChap
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldPrimary,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CONTINUE READING",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                                Text(
                                    text = "${uiState.lastHadithPosition.bookName} • ${uiState.lastHadithPosition.hadithNumber}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.lastHadithPosition.chapterName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Resume", tint = EmeraldPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Global Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hadith_search_input"),
                    placeholder = { Text("Search by topic, narrator, English, Urdu, Hindi...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (searchQuery.isBlank()) {
                    Text(
                        text = "Canonical Collections (الكتب الستة)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Search Results Mode
            if (searchQuery.isNotBlank()) {
                if (filteredHadiths.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "No Hadiths Matched", fontWeight = FontWeight.Bold)
                                Text(text = "Try searching keywords like 'wudu', 'salah', 'niyyah', or 'jannah'.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Search Results (${filteredHadiths.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    items(filteredHadiths, key = { it.id }) { hadith ->
                        HadithDetailCard(
                            hadith = hadith,
                            isBookmarked = isBookmarked("HADITH", hadith.id, 0),
                            isStudyMode = true,
                            onToggleBookmark = {
                                onToggleBookmark("HADITH", hadith.id, 0, hadith.bookName, hadith.hadithNumber)
                            },
                            onCopy = { copyHadithToClipboard(context, hadith, onShowStatus) },
                            onShare = { shareHadithText(context, hadith) },
                            onAddNoteClick = {
                                if (!uiState.isPremium) onShowPaywall("Hadith Notes")
                                else { showNoteDialogForHadith = hadith; noteInputText = "" }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            // Collections Cards Mode
            else {
                items(HadithRepository.MAIN_BOOKS) { book ->
                    HadithCollectionCard(
                        book = book,
                        onClick = { selectedBook = book }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    // Add Note Dialog (Premium Feature)
    if (showNoteDialogForHadith != null) {
        val targetHadith = showNoteDialogForHadith!!
        AlertDialog(
            onDismissRequest = { showNoteDialogForHadith = null },
            title = {
                Text(
                    text = "Personal Study Note",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "${targetHadith.bookName} • ${targetHadith.hadithNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteInputText,
                        onValueChange = { noteInputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        placeholder = { Text("Write your reflections, scholarly explanations, or personal action points...") },
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteInputText.isNotBlank()) {
                            onAddNote(targetHadith.bookId, targetHadith.id, noteInputText.trim())
                        }
                        showNoteDialogForHadith = null
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Note")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialogForHadith = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HadithCollectionCard(
    book: HadithBook,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = EmeraldPrimary.copy(alpha = 0.15f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.LibraryBooks,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.nameEnglish,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.nameArabic,
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = FontFamily.Serif,
                        color = GoldSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = book.hadithCountApprox,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = "${book.totalChapters} Chapters",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HadithDetailCard(
    hadith: HadithItem,
    isBookmarked: Boolean,
    isStudyMode: Boolean,
    onToggleBookmark: () -> Unit,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onAddNoteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Book name, Hadith number, Authenticity Badge & Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${hadith.bookName} • ${hadith.hadithNumber}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = hadith.grade,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Bookmarked" else "Bookmark",
                            tint = if (isBookmarked) GoldSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onAddNoteClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Add study note",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Narrator (Sanad)
            Text(
                text = hadith.narrator,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Matn
            Text(
                text = hadith.arabicText,
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.End,
                lineHeight = 36.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // English Translation
            Text(
                text = hadith.englishTranslation,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Urdu Translation
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "اردو: ${hadith.urduTranslation}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.End,
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Hindi Translation (if study mode or Indian user)
            if (isStudyMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "हिंदी अनुवाद (Hindi Translation):",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = hadith.hindiTranslation,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Reference: ${hadith.reference}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Actions (Copy, Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy")
                }
                Spacer(modifier = Modifier.width(6.dp))
                TextButton(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
            }
        }
    }
}

private fun copyHadithToClipboard(context: Context, hadith: HadithItem, onShowStatus: (String) -> Unit) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(
        "Hadith ${hadith.hadithNumber}",
        "${hadith.bookName} (${hadith.hadithNumber})\n${hadith.narrator}\n\n${hadith.arabicText}\n\nEnglish:\n${hadith.englishTranslation}\n\nاردو:\n${hadith.urduTranslation}\n\nहिंदी:\n${hadith.hindiTranslation}\n\nReference: ${hadith.reference}"
    )
    clipboard.setPrimaryClip(clip)
    onShowStatus("Hadith copied to clipboard")
}

private fun shareHadithText(context: Context, hadith: HadithItem) {
    val text = "${hadith.bookName} (${hadith.hadithNumber})\n${hadith.narrator}\n\n${hadith.arabicText}\n\nEnglish:\n${hadith.englishTranslation}\n\nاردو:\n${hadith.urduTranslation}\n\nReference: ${hadith.reference}\n— Muslim Ummah App"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "${hadith.bookName} ${hadith.hadithNumber}")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Hadith"))
}
