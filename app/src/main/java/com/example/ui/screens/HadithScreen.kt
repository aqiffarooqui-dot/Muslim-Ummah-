package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
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
    onShowStatus: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedBookId by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredHadiths = remember(selectedBookId, searchQuery) {
        val q = searchQuery.trim().lowercase()
        HadithRepository.ALL_HADITHS.filter { hadith ->
            val matchesBook = selectedBookId == "all" || hadith.bookId == selectedBookId
            val matchesSearch = q.isEmpty() ||
                    hadith.englishTranslation.lowercase().contains(q) ||
                    hadith.urduTranslation.contains(q) ||
                    hadith.arabicText.contains(q) ||
                    hadith.narrator.lowercase().contains(q) ||
                    hadith.chapterName.lowercase().contains(q) ||
                    hadith.bookName.lowercase().contains(q)
            matchesBook && matchesSearch
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
                        text = "Authentic prophetic traditions from canonical Islamic collections",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hadith_search_input"),
                placeholder = { Text("Search by topic, narrator, or keyword (English/Urdu/Arabic)...") },
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

            Spacer(modifier = Modifier.height(12.dp))

            // Book Selector Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedBookId == "all",
                        onClick = { selectedBookId = "all" },
                        label = { Text("All Collections (${HadithRepository.ALL_HADITHS.size})") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                items(HadithRepository.MAIN_BOOKS) { book ->
                    val isSelected = selectedBookId == book.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedBookId = book.id },
                        label = { Text(book.nameEnglish) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

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
                        Icon(imageVector = Icons.Default.SearchOff, contentDescription = "No results", modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "No Hadiths Found", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = "Try different search terms or clear the filter.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            items(filteredHadiths, key = { it.id }) { hadith ->
                val bookmarked = isBookmarked("HADITH", hadith.id, 0)
                HadithCard(
                    hadith = hadith,
                    isBookmarked = bookmarked,
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
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            "Hadith ${hadith.hadithNumber}",
                            "${hadith.bookName} (${hadith.hadithNumber})\n\n${hadith.arabicText}\n\n${hadith.englishTranslation}\n\nاردو: ${hadith.urduTranslation}"
                        )
                        clipboard.setPrimaryClip(clip)
                        onShowStatus("Hadith copied to clipboard")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun HadithCard(
    hadith: HadithItem,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onCopy: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hadith_item_${hadith.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Book Name, Reference & Grade
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
                            text = hadith.bookName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = hadith.grade,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
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

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${hadith.hadithNumber} • ${hadith.chapterName}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = hadith.narrator,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Arabic Matn (Text)
            Text(
                text = hadith.arabicText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    lineHeight = 32.sp
                ),
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            // English Translation
            Text(
                text = hadith.englishTranslation,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Urdu Translation (with distinct styling)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "اردو ترجمہ:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = hadith.urduTranslation,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp, lineHeight = 22.sp),
                        fontFamily = FontFamily.Serif,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
