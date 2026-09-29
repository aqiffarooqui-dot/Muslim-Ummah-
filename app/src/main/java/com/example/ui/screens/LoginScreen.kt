package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onGoogleSignInClick: (Activity) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Mandatory Login: Back button cannot bypass login
    BackHandler(enabled = true) {
        // Prevent back navigation to unauthenticated screens
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF062016),
                        Color(0xFF0F3A2A),
                        Color(0xFF09281C)
                    )
                )
            )
            .testTag("mandatory_login_screen")
    ) {
        // Decorative background geometric accents
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x15D4AF37),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.9f, size.height * 0.1f)
            )
            drawCircle(
                color = Color(0x101B5E20),
                radius = size.width * 0.5f,
                center = Offset(size.width * 0.1f, size.height * 0.85f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Islamic Crest & App Branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = EmeraldPrimary.copy(alpha = 0.25f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(GoldSecondary, EmeraldPrimary))),
                    modifier = Modifier.size(92.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(46.dp)) {
                            // Draw Islamic Crescent & Star Emblem
                            val path = Path().apply {
                                moveTo(size.width * 0.7f, size.height * 0.15f)
                                cubicTo(
                                    size.width * 0.2f, size.height * 0.25f,
                                    size.width * 0.2f, size.height * 0.75f,
                                    size.width * 0.7f, size.height * 0.85f
                                )
                                cubicTo(
                                    size.width * 0.4f, size.height * 0.75f,
                                    size.width * 0.4f, size.height * 0.25f,
                                    size.width * 0.7f, size.height * 0.15f
                                )
                                close()
                            }
                            drawPath(path, color = GoldSecondary)
                            drawCircle(
                                color = GoldSecondary,
                                radius = size.width * 0.08f,
                                center = Offset(size.width * 0.68f, size.height * 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Muslim Ummah",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "أمة المسلمين",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    color = GoldSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your Sacred Islamic Companion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFE0E0E0),
                    textAlign = TextAlign.Center
                )
            }

            // Middle: Core Islamic Values / Features
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeaturePill(
                    icon = Icons.Default.MenuBook,
                    title = "The Holy Quran",
                    subtitle = "30 Paras, 114 Surahs, Translations & Audio Recitations"
                )
                FeaturePill(
                    icon = Icons.Default.LibraryBooks,
                    title = "Canonical Hadith (Kutub al-Sittah)",
                    subtitle = "Sahih al-Bukhari, Muslim, Sunan & Authentic Traditions"
                )
                FeaturePill(
                    icon = Icons.Default.AccessTimeFilled,
                    title = "Accurate Prayer & Qibla",
                    subtitle = "Location-based timing, countdowns & calibrated compass"
                )
                FeaturePill(
                    icon = Icons.Default.CloudSync,
                    title = "Secure Cloud Synchronization",
                    subtitle = "Bookmarks, notes & reading position synced to your account"
                )
            }

            // Bottom: Google Sign-In Action & Error Handling
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Error Alert Banner
                AnimatedVisibility(
                    visible = errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    if (errorMessage != null) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.ErrorOutline,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = onDismissError,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Mandatory Continue with Google Button
                Button(
                    onClick = {
                        if (activity != null && !isLoading) {
                            onGoogleSignInClick(activity)
                        }
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF1F1F1F)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("google_sign_in_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.5.dp,
                            color = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Connecting to Google...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1F1F1F)
                        )
                    } else {
                        // Google "G" Icon Badge
                        GoogleGLogo()
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Mandatory authentication required to safeguard your reading progress, notes, and preferences across devices.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFB0BEC5),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun FeaturePill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF123829).copy(alpha = 0.75f),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0x33D4AF37), Color(0x221B5E20)))),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = EmeraldPrimary.copy(alpha = 0.3f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = GoldSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCFD8DC),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun GoogleGLogo() {
    Canvas(modifier = Modifier.size(22.dp)) {
        val width = size.width
        val height = size.height
        // Draw 4-color Google G emblem
        drawCircle(color = Color(0xFF4285F4), radius = width * 0.48f, center = Offset(width * 0.5f, height * 0.5f))
        drawCircle(color = Color.White, radius = width * 0.28f, center = Offset(width * 0.5f, height * 0.5f))
        // Center blue bar
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(width * 0.45f, height * 0.38f),
            size = androidx.compose.ui.geometry.Size(width * 0.52f, height * 0.24f)
        )
    }
}
