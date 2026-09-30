package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.QuranTranslationLanguage
import com.example.data.model.Surah
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary

/**
 * Genuine classical Mushaf Page View.
 * Renders traditional Uthmani script flowing continuously within an ornate double-lined
 * golden border with traditional Ayah end ornaments (۝), Surah calligraphy header,
 * and tap-to-inspect translation/audio bottom sheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MushafPageView(
    surah: Surah,
    verses: List<Ayah>,
    fontSizeSp: Float,
    selectedLanguage: QuranTranslationLanguage,
    showTranslation: Boolean,
    isBookmarked: (String, Int, Int) -> Boolean,
    onToggleBookmark: (String, Int, Int, String, String) -> Unit,
    onAddNote: (Int, Int, String, String) -> Unit,
    onSaveReadingPosition: (Int, Int, String) -> Unit,
    onPlayAyah: (Int, Int) -> Unit,
    isPremium: Boolean,
    onShowPaywall: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedAyahForSheet by remember { mutableStateOf<Ayah?>(null) }
    var highlightedAyahNumber by remember { mutableStateOf<Int?>(null) }
    var showNoteDialogForAyah by remember { mutableStateOf<Ayah?>(null) }
    var noteInputText by remember { mutableStateOf("") }

    val mushafBackgroundColor = Color(0xFFFBF8F1) // Authentic Warm Mushaf Parchment
    val mushafTextColor = Color(0xFF1B2621)
    val mushafBorderColor = GoldSecondary.copy(alpha = 0.7f)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(mushafBackgroundColor)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("mushaf_page_view"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item {
            // Traditional Mushaf Page Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, mushafBorderColor, RoundedCornerShape(12.dp))
                    .padding(4.dp)
                    .border(1.dp, mushafBorderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Page Header (Juz left, Surah right)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الجزء ${toArabicNumerals(surah.juz)} • Juz ${surah.juz}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "سُورَةُ ${surah.nameArabic}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldSecondary,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    // Traditional Surah Banner
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = GoldSecondary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldSecondary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "سُورَةُ ${surah.nameArabic}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldPrimary,
                                fontFamily = FontFamily.Serif
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Surah ${surah.nameEnglish} • ${surah.revelationType} • ${surah.totalAyahs} آيات",
                                style = MaterialTheme.typography.labelSmall,
                                color = mushafTextColor.copy(alpha = 0.8f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Traditional Bismillah Calligraphy Banner (Surah 9 At-Tawbah does not have Bismillah)
                    if (surah.number != 9) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ",
                            style = MaterialTheme.typography.headlineSmall,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Continuous Flowing Uthmani Verses
                    val annotatedText = buildMushafAnnotatedText(
                        verses = verses,
                        highlightedAyah = highlightedAyahNumber,
                        textColor = mushafTextColor
                    )

                    ClickableText(
                        text = annotatedText,
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontSize = fontSizeSp.sp,
                            lineHeight = (fontSizeSp * 2.1f).sp,
                            textAlign = TextAlign.Justify
                        ),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "AYAH_CLICK", start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    val ayahNum = annotation.item.toIntOrNull()
                                    val ayah = verses.find { it.ayahNumber == ayahNum }
                                    if (ayah != null) {
                                        highlightedAyahNumber = ayah.ayahNumber
                                        selectedAyahForSheet = ayah
                                        onSaveReadingPosition(surah.number, ayah.ayahNumber, surah.nameEnglish)
                                    }
                                }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Mushaf Page Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "—  ${toArabicNumerals(surah.number)}  —",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Serif,
                            color = GoldSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Ayah Action & Translation Bottom Sheet
    if (selectedAyahForSheet != null) {
        val ayah = selectedAyahForSheet!!
        val isAyahBookmarked = isBookmarked("QURAN_AYAH", surah.number, ayah.ayahNumber)

        ModalBottomSheet(
            onDismissRequest = {
                selectedAyahForSheet = null
                highlightedAyahNumber = null
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                // Header: Surah & Ayah info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Surah ${surah.nameEnglish} • Ayah ${ayah.ayahNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = "Juz ${surah.juz} • Revelation: ${surah.revelationType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row {
                        IconButton(
                            onClick = {
                                onToggleBookmark(
                                    "QURAN_AYAH",
                                    surah.number,
                                    ayah.ayahNumber,
                                    "Surah ${surah.nameEnglish} • Ayah ${ayah.ayahNumber}",
                                    ayah.textArabic.take(30) + "..."
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (isAyahBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isAyahBookmarked) GoldSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    "Surah ${surah.nameEnglish} Ayah ${ayah.ayahNumber}",
                                    "${ayah.textArabic}\n\n${ayah.getTranslation(selectedLanguage)}\n\n[Surah ${surah.nameEnglish} ${surah.number}:${ayah.ayahNumber}]"
                                )
                                clipboard.setPrimaryClip(clip)
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Arabic Verse
                Text(
                    text = ayah.textArabic,
                    style = MaterialTheme.typography.headlineSmall,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    lineHeight = 38.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Transliteration
                Text(
                    text = ayah.textTransliteration,
                    style = MaterialTheme.typography.bodySmall,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Selected Translation
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "${selectedLanguage.displayName} Translation:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ayah.getTranslation(selectedLanguage),
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons: Play Recitation, Add Reflection Note, Mark as Current Position
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onPlayAyah(surah.number, ayah.ayahNumber)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Recite Ayah")
                    }

                    OutlinedButton(
                        onClick = {
                            if (!isPremium) {
                                onShowPaywall("Personal Quran Study Notes")
                            } else {
                                showNoteDialogForAyah = ayah
                                noteInputText = ""
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Note")
                    }
                }
            }
        }
    }

    // Personal Note Dialog
    if (showNoteDialogForAyah != null) {
        val ayah = showNoteDialogForAyah!!
        AlertDialog(
            onDismissRequest = { showNoteDialogForAyah = null },
            title = { Text("Add Study Note • Ayah ${ayah.ayahNumber}") },
            text = {
                Column {
                    Text(
                        text = "Save reflections and tafsir insights for Surah ${surah.nameEnglish}:${ayah.ayahNumber}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteInputText,
                        onValueChange = { noteInputText = it },
                        placeholder = { Text("Write personal reflection...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteInputText.isNotBlank()) {
                            onAddNote(surah.number, ayah.ayahNumber, surah.nameEnglish, noteInputText.trim())
                            showNoteDialogForAyah = null
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoteDialogForAyah = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Builds flowing classical Mushaf text with embedded end-of-ayah circular ornaments (۝)
 * and click annotations so tapping an ayah opens the interactive inspection sheet.
 */
private fun buildMushafAnnotatedText(
    verses: List<Ayah>,
    highlightedAyah: Int?,
    textColor: Color
): AnnotatedString {
    return buildAnnotatedString {
        verses.forEach { ayah ->
            val isHighlighted = ayah.ayahNumber == highlightedAyah
            val startIdx = length

            pushStringAnnotation(tag = "AYAH_CLICK", annotation = ayah.ayahNumber.toString())

            // Arabic text of the verse
            append(ayah.textArabic.trim())
            append(" ")

            // Ayah End Symbol (Traditional ۝ with Arabic numerals)
            val ayahMarker = " ۝${toArabicNumerals(ayah.ayahNumber)} "
            append(ayahMarker)

            pop()

            val endIdx = length
            if (isHighlighted) {
                addStyle(
                    style = SpanStyle(
                        background = GoldSecondary.copy(alpha = 0.35f),
                        color = Color(0xFF0F5A44),
                        fontWeight = FontWeight.Bold
                    ),
                    start = startIdx,
                    end = endIdx
                )
            } else {
                addStyle(
                    style = SpanStyle(color = textColor),
                    start = startIdx,
                    end = endIdx
                )
            }
            append(" ")
        }
    }
}

/**
 * Converts Western digits (1, 2, 3) to Arabic-Indic digits (١, ٢, ٣)
 */
fun toArabicNumerals(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val s = number.toString()
    val sb = StringBuilder()
    for (ch in s) {
        if (ch in '0'..'9') {
            sb.append(arabicDigits[ch - '0'])
        } else {
            sb.append(ch)
        }
    }
    return sb.toString()
}
