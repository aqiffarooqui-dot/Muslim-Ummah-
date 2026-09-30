package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranReciter
import com.example.data.model.QuranTranslationLanguage
import com.example.ui.theme.EmeraldPrimary

enum class QuranReaderMode(val title: String) {
    AYAH_WISE("Ayah by Ayah"),
    MUSHAF_PAGE("Mushaf Page View")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranReaderSettingsDialog(
    readerMode: QuranReaderMode,
    onReaderModeChange: (QuranReaderMode) -> Unit,
    fontSizeSp: Float,
    onFontSizeChange: (Float) -> Unit,
    selectedLanguage: QuranTranslationLanguage,
    onLanguageChange: (QuranTranslationLanguage) -> Unit,
    showTranslation: Boolean,
    onShowTranslationChange: (Boolean) -> Unit,
    repeatCount: Int,
    onRepeatCountChange: (Int) -> Unit,
    selectedReciter: String,
    onReciterChange: (String) -> Unit,
    isPremium: Boolean,
    onShowPaywall: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Reader Settings",
                        tint = EmeraldPrimary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Quran Reader Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = onDismiss) {
                    Text("Done")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Reading Mode: Ayah by Ayah vs Mushaf Page
            Text(
                text = "READER MODE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    onClick = { onReaderModeChange(QuranReaderMode.AYAH_WISE) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (readerMode == QuranReaderMode.AYAH_WISE) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatListNumbered,
                            contentDescription = null,
                            tint = if (readerMode == QuranReaderMode.AYAH_WISE) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ayah View",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (readerMode == QuranReaderMode.AYAH_WISE) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    onClick = { onReaderModeChange(QuranReaderMode.MUSHAF_PAGE) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (readerMode == QuranReaderMode.MUSHAF_PAGE) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = if (readerMode == QuranReaderMode.MUSHAF_PAGE) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mushaf Mode",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (readerMode == QuranReaderMode.MUSHAF_PAGE) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Arabic Font Size Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FONT SIZE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${fontSizeSp.toInt()} sp",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "A", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = fontSizeSp,
                    onValueChange = onFontSizeChange,
                    valueRange = 18f..38f,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                )
                Text(text = "A", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Translation Visibility & Language
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SHOW TRANSLATION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Switch(
                    checked = showTranslation,
                    onCheckedChange = onShowTranslationChange
                )
            }

            if (showTranslation) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(QuranTranslationLanguage.values()) { lang ->
                        FilterChip(
                            selected = selectedLanguage == lang,
                            onClick = { onLanguageChange(lang) },
                            label = { Text(lang.displayName) },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Quran Reciter Selection
            Text(
                text = "RECITATION RECITER",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(QuranReciter.ALL_RECITERS) { reciter ->
                    FilterChip(
                        selected = selectedReciter == reciter.name,
                        onClick = {
                            if (reciter.isPremium && !isPremium) {
                                onShowPaywall("Elite Quran Reciters (${reciter.name})")
                            } else {
                                onReciterChange(reciter.name)
                            }
                        },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(reciter.name.substringAfter("Sheikh "))
                                if (reciter.isPremium && !isPremium) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("⭐", fontSize = 10.sp)
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }
    }
}
