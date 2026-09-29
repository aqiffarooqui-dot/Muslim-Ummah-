package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QiblaCalculator
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.MuslimUiState
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaScreen(
    uiState: MuslimUiState,
    modifier: Modifier = Modifier
) {
    val qiblaDirection = uiState.qiblaDirection
    val currentAzimuth = uiState.compassAzimuth
    val isAligned = uiState.isQiblaAligned

    // Smooth compass dial rotation
    val dialRotation by animateFloatAsState(
        targetValue = -currentAzimuth,
        animationSpec = spring(stiffness = 150f),
        label = "dial_rot"
    )

    // Kaaba pointer angle relative to phone top
    val kaabaAngle = (qiblaDirection - currentAzimuth + 360) % 360

    val ringColor by animateColorAsState(
        targetValue = if (isAligned) EmeraldPrimary else Color(0xFFC4D1CB),
        label = "ring_color"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .testTag("qibla_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Location and Info
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Qibla Direction",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Facing the Holy Kaaba in Makkah al-Mukarramah",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${uiState.selectedCity.name} • ${QiblaCalculator.getCompassDirectionString(qiblaDirection)} ${String.format("%.1f", qiblaDirection)}°",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Compass Centerpiece
        Box(
            modifier = Modifier
                .size(310.dp)
                .testTag("compass_dial"),
            contentAlignment = Alignment.Center
        ) {
            // Background glow when aligned
            if (isAligned) {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    EmeraldPrimary.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Outer decorative ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = (size.minDimension / 2f) - 16.dp.toPx()

                drawCircle(
                    color = ringColor.copy(alpha = 0.3f),
                    radius = radius + 8.dp.toPx(),
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )

                drawCircle(
                    color = ringColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = if (isAligned) 6.dp.toPx() else 3.dp.toPx())
                )
            }

            // Rotating Compass Rose & Ticks
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .rotate(dialRotation),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension / 2f - 24.dp.toPx()

                    for (i in 0 until 360 step 5) {
                        val angleRad = Math.toRadians((i - 90).toDouble())
                        val isCardinal = i % 90 == 0
                        val isSemiCardinal = i % 30 == 0
                        val tickLength = if (isCardinal) 18.dp.toPx() else if (isSemiCardinal) 12.dp.toPx() else 6.dp.toPx()
                        val strokeW = if (isCardinal) 3.dp.toPx() else if (isSemiCardinal) 2.dp.toPx() else 1.dp.toPx()

                        val startX = center.x + (radius - tickLength) * cos(angleRad).toFloat()
                        val startY = center.y + (radius - tickLength) * sin(angleRad).toFloat()
                        val endX = center.x + radius * cos(angleRad).toFloat()
                        val endY = center.y + radius * sin(angleRad).toFloat()

                        val tickColor = if (i == 0) Color(0xFFD32F2F) else Color.Gray.copy(alpha = 0.6f)
                        drawLine(
                            color = tickColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = strokeW
                        )
                    }
                }

                // Cardinal Letters
                Text(
                    text = "N",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                )
                Text(
                    text = "S",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 10.dp)
                )
                Text(
                    text = "E",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp)
                )
                Text(
                    text = "W",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                )
            }

            // Qibla Indicator / Kaaba marker needle
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .rotate(kaabaAngle.toFloat()),
                contentAlignment = Alignment.Center
            ) {
                // Kaaba marker at top
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = CircleShape,
                        color = GoldSecondary,
                        shadowElevation = 6.dp,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Square,
                                contentDescription = "Kaaba",
                                tint = Color(0xFF1B1B1B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Needle pointing down to center
                    Canvas(modifier = Modifier.size(width = 10.dp, height = 40.dp)) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, size.height)
                            lineTo(0f, 0f)
                            lineTo(size.width, 0f)
                            close()
                        }
                        drawPath(path, color = GoldSecondary)
                    }
                }
            }

            // Center Pivot Dial Hub
            Surface(
                shape = CircleShape,
                color = if (isAligned) EmeraldPrimary else MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(68.dp)
                    .border(
                        width = 3.dp,
                        color = if (isAligned) GoldSecondary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${currentAzimuth.toInt()}°",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isAligned) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Bottom Status & Calibration Card
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isAligned) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldPrimary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Aligned",
                            tint = Color(0xFFFFD54F),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "You are facing the Holy Kaaba!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.RotateRight,
                            contentDescription = "Rotate",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Rotate phone until needle aligns with Kaaba icon",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Distance: ${String.format(Locale.getDefault(), "%,.0f", uiState.distanceToKaabaKm)} km to Makkah",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tip: Keep the phone flat on a level surface away from magnetic objects for maximum precision.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
