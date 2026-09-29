package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.MuslimUiState

@Composable
fun FastingQadaScreen(
    uiState: MuslimUiState,
    onIncrementQada: (String) -> Unit,
    onDecrementQada: (String) -> Unit,
    onShowPaywall: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPremium = uiState.isPremium

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("fasting_qada_screen"),
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
                        text = "Ramadan & Qada Tracker",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Suhoor, Iftar, and Missed Prayers Calculator",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isPremium) Color(0xFFFFD54F) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (isPremium) "PRO UNLOCKED" else "PREMIUM",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isPremium) Color(0xFF1F3A32) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Suhoor & Iftar Banner
            val suhoorTime = uiState.prayerTimes?.fajr?.timeString ?: "05:00 AM"
            val iftarTime = uiState.prayerTimes?.maghrib?.timeString ?: "06:20 PM"

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0F6E56), Color(0xFF084B3A))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "SUHOOR ENDS",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFD4EFE6),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = suhoorTime,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "At Fajr Dawn",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .width(1.dp)
                                .background(Color.White.copy(alpha = 0.3f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "IFTAR (FAST BREAK)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFFFD54F),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = iftarTime,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD54F)
                            )
                            Text(
                                text = "At Maghrib Sunset",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Qada Prayers Section
            Text(
                text = "Missed Prayers (Qada Salah)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Keep an accurate tally of obligatory prayers you need to make up",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            QadaCounterItem("Fajr", "صلاة الفجر", uiState.qadaFajr, onIncrementQada, onDecrementQada)
            Spacer(modifier = Modifier.height(8.dp))
            QadaCounterItem("Dhuhr", "صلاة الظهر", uiState.qadaDhuhr, onIncrementQada, onDecrementQada)
            Spacer(modifier = Modifier.height(8.dp))
            QadaCounterItem("Asr", "صلاة العصر", uiState.qadaAsr, onIncrementQada, onDecrementQada)
            Spacer(modifier = Modifier.height(8.dp))
            QadaCounterItem("Maghrib", "صلاة المغرب", uiState.qadaMaghrib, onIncrementQada, onDecrementQada)
            Spacer(modifier = Modifier.height(8.dp))
            QadaCounterItem("Isha", "صلاة العشاء", uiState.qadaIsha, onIncrementQada, onDecrementQada)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Qada Fasts Section
        item {
            Text(
                text = "Missed Fasts (Qada Sawm)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            QadaCounterItem("Fasts", "أيام الصيام المقضي", uiState.qadaFasts, onIncrementQada, onDecrementQada)
        }
    }
}

@Composable
private fun QadaCounterItem(
    name: String,
    arabic: String,
    count: Int,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = arabic, style = MaterialTheme.typography.bodySmall, color = EmeraldPrimary)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onDecrement(name) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "$count",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = { onIncrement(name) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
