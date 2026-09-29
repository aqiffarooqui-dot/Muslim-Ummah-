package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.MuslimUiState

@Composable
fun MoreHubScreen(
    uiState: MuslimUiState,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("more_hub_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text(
                text = "More Islamic Features",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Explore supplications, Ramadan tracker, and administration",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            if (uiState.isAdmin) {
                MoreNavigationCard(
                    title = "👑 Super Admin Console",
                    subtitle = "User database, subscription manager & analytics for aqiffarooqui@gmail.com",
                    icon = Icons.Default.AdminPanelSettings,
                    accentColor = GoldSecondary,
                    onClick = { onNavigate("admin") }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            MoreNavigationCard(
                title = "Ramadan & Qada Tracker",
                subtitle = "Suhoor & Iftar times, missed Salah counter, and fasting logs",
                icon = Icons.Default.Restaurant,
                accentColor = Color(0xFFD84315),
                onClick = { onNavigate("fasting_qada") }
            )
            Spacer(modifier = Modifier.height(12.dp))

            MoreNavigationCard(
                title = "Duas & Supplications",
                subtitle = "Hisn al-Muslim (حصن المسلم) prayers for day & night",
                icon = Icons.Default.VolunteerActivism,
                accentColor = Color(0xFF5E35B1),
                onClick = { onNavigate("duas") }
            )
            Spacer(modifier = Modifier.height(12.dp))

            MoreNavigationCard(
                title = "Hadith Books (كتب الحديث)",
                subtitle = "Sahih al-Bukhari, Sahih Muslim, Tirmidhi, Abu Dawud, Nasa'i & Nawawi",
                icon = Icons.Default.MenuBook,
                accentColor = Color(0xFF00796B),
                onClick = { onNavigate("hadith") }
            )
            Spacer(modifier = Modifier.height(12.dp))

            MoreNavigationCard(
                title = "99 Names of Allah",
                subtitle = "Asma'ul Husna (أسماء الله الحسنى) with meanings and reflections",
                icon = Icons.Default.Star,
                accentColor = Color(0xFFE65100),
                onClick = { onNavigate("names") }
            )
            Spacer(modifier = Modifier.height(12.dp))

            MoreNavigationCard(
                title = "Islamic Hijri Calendar",
                subtitle = "Hijri conversion, lunar phases, and holy events",
                icon = Icons.Default.CalendarMonth,
                accentColor = Color(0xFF0277BD),
                onClick = { onNavigate("calendar") }
            )
            Spacer(modifier = Modifier.height(12.dp))

            MoreNavigationCard(
                title = "Settings & Calculation Rules",
                subtitle = "Calculation methods (MWL, ISNA, Umm al-Qura), Asr juristic rule, city",
                icon = Icons.Default.Settings,
                accentColor = EmeraldPrimary,
                onClick = { onNavigate("settings") }
            )
        }
    }
}

@Composable
private fun MoreNavigationCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .minimumInteractiveComponentSize(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
