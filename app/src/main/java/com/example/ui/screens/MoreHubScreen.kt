package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.MuslimUiState

@Composable
fun MoreHubScreen(
    uiState: MuslimUiState,
    onNavigate: (String) -> Unit,
    onOpenPaywall: () -> Unit = {},
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
                text = "AI assistant, spiritual milestones, Tasbih & supplications",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))
        }

        // 1. Muslim Ummah AI
        item {
            MoreNavigationCard(
                title = "✨ Muslim Ummah AI",
                subtitle = "Ask Islamic questions with verified Quran & Hadith citations",
                icon = Icons.Default.AutoAwesome,
                accentColor = Color(0xFF673AB7),
                isHighlight = true,
                onClick = { onNavigate("ai_assistant") }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 2. My Journey (Personal Dashboard)
        item {
            MoreNavigationCard(
                title = "🌿 My Journey",
                subtitle = "Private statistics: Quran reading time, Khatam, prayers & Tasbih",
                icon = Icons.Default.Timeline,
                accentColor = EmeraldPrimary,
                onClick = { onNavigate("my_journey") }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 3. Digital Tasbih
        item {
            MoreNavigationCard(
                title = "Digital Tasbih Counter",
                subtitle = "SubhanAllah, Alhamdulillah, Allahu Akbar circular counter with haptics",
                icon = Icons.Default.RadioButtonChecked,
                accentColor = EmeraldPrimary,
                onClick = { onNavigate("tasbih") }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 4. Duas & Azkar (Hisn al-Muslim)
        item {
            MoreNavigationCard(
                title = "Duas & Supplications",
                subtitle = "Hisn al-Muslim (حصن المسلم) prayers for morning, evening & protection",
                icon = Icons.Default.VolunteerActivism,
                accentColor = Color(0xFF5E35B1),
                onClick = { onNavigate("duas") }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 5. 99 Names of Allah
        item {
            MoreNavigationCard(
                title = "99 Names of Allah",
                subtitle = "Asma'ul Husna (أسماء الله الحسنى) with meanings and reflections",
                icon = Icons.Default.Star,
                accentColor = Color(0xFFE65100),
                onClick = { onNavigate("names") }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 6. Hijri Calendar & Fasting
        item {
            MoreNavigationCard(
                title = "Islamic Hijri Calendar",
                subtitle = "1447 AH moon phases, Sunnah fasts timeline, and Eid holidays",
                icon = Icons.Default.CalendarMonth,
                accentColor = Color(0xFF00796B),
                onClick = { onNavigate("calendar") }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 7. Ramadan & Qada Tracker
        item {
            MoreNavigationCard(
                title = "Ramadan & Qada Tracker",
                subtitle = "Suhoor & Iftar times, missed Salah counter, and fasting logs",
                icon = Icons.Default.Restaurant,
                accentColor = Color(0xFFD84315),
                onClick = { onNavigate("fasting_qada") }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 8. Super Admin Console (if Admin)
        if (uiState.isAdmin) {
            item {
                MoreNavigationCard(
                    title = "👑 Super Admin Console",
                    subtitle = "User database, subscription manager & analytics for aqiffarooqui@gmail.com",
                    icon = Icons.Default.AdminPanelSettings,
                    accentColor = GoldSecondary,
                    isHighlight = true,
                    onClick = { onNavigate("admin") }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // 9. Settings & Themes
        item {
            MoreNavigationCard(
                title = "Settings & Appearance",
                subtitle = "Themes, calculation methods, notifications, and account sync",
                icon = Icons.Default.Settings,
                accentColor = Color(0xFF455A64),
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
    isHighlight: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight) accentColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = accentColor.copy(alpha = 0.15f),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
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
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
