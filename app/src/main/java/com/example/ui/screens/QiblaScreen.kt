package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
    onRequestLocation: () -> Unit = {},
    onSetOffset: (Float) -> Unit = {},
    onResetOffset: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val qiblaDirection = uiState.qiblaDirection
    val currentAzimuth = uiState.compassAzimuth
    val isAligned = uiState.isQiblaAligned
    var showCalibrationDialog by remember { mutableStateOf(false) }

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
                text = "Qibla Compass",
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

            Spacer(modifier = Modifier.height(10.dp))

            // Location Chip & GPS Refresh
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
                            text = "${uiState.selectedCity.name} • ${QiblaCalculator.getCompassDirectionString(qiblaDirection)} ${String.format(Locale.US, "%.1f", qiblaDirection)}°",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Calibrate / Accuracy pill button
                Surface(
                    onClick = { showCalibrationDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    color = if (uiState.compassOffsetDegrees != 0f) GoldSecondary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.testTag("calibrate_compass_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Calibrate",
                            modifier = Modifier.size(14.dp),
                            tint = if (uiState.compassOffsetDegrees != 0f) GoldSecondary else MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (uiState.compassOffsetDegrees != 0f) "Calibrated" else "Calibrate",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.compassOffsetDegrees != 0f) GoldSecondary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Compass Centerpiece
        Box(
            modifier = Modifier
                .size(300.dp)
                .testTag("compass_dial"),
            contentAlignment = Alignment.Center
        ) {
            // Background glow when aligned
            if (isAligned) {
                Box(
                    modifier = Modifier
                        .size(290.dp)
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
                    .size(270.dp)
                    .rotate(dialRotation)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension / 2f

                    // Degree tick marks
                    for (deg in 0 until 360 step 5) {
                        val angleRad = Math.toRadians(deg.toDouble() - 90.0)
                        val isMajor = deg % 30 == 0
                        val isCardinal = deg % 90 == 0
                        val tickLength = if (isCardinal) 16.dp.toPx() else if (isMajor) 10.dp.toPx() else 5.dp.toPx()

                        val startX = center.x + (radius - tickLength) * cos(angleRad).toFloat()
                        val startY = center.y + (radius - tickLength) * sin(angleRad).toFloat()
                        val endX = center.x + radius * cos(angleRad).toFloat()
                        val endY = center.y + radius * sin(angleRad).toFloat()

                        drawLine(
                            color = if (isCardinal) EmeraldPrimary else Color(0xFF9EAFA8),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (isCardinal) 3.dp.toPx() else if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                        )
                    }
                }

                // Cardinal Labels (N, E, S, W)
                Text(
                    text = "N",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD32F2F),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 18.dp)
                )
                Text(
                    text = "E",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 18.dp)
                )
                Text(
                    text = "S",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 18.dp)
                )
                Text(
                    text = "W",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 18.dp)
                )

                // Kaaba Icon at the calculated direction on the dial ring
                val kaabaRad = Math.toRadians(qiblaDirection - 90.0)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(26.dp)
                ) {
                    val offsetX = (108.dp * cos(kaabaRad).toFloat())
                    val offsetY = (108.dp * sin(kaabaRad).toFloat())
                    Surface(
                        shape = CircleShape,
                        color = if (isAligned) GoldSecondary else Color(0xFF233932),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD54F)),
                        modifier = Modifier
                            .size(34.dp)
                            .align(Alignment.Center)
                            .offset(x = offsetX, y = offsetY)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🕋", fontSize = 16.sp)
                        }
                    }
                }
            }

            // Fixed Center Device Pointer Needle (Arrow pointing up)
            Canvas(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val arrowPath = Path().apply {
                    moveTo(center.x, center.y - 36.dp.toPx())
                    lineTo(center.x + 12.dp.toPx(), center.y + 12.dp.toPx())
                    lineTo(center.x, center.y + 4.dp.toPx())
                    lineTo(center.x - 12.dp.toPx(), center.y + 12.dp.toPx())
                    close()
                }

                drawPath(
                    path = arrowPath,
                    color = if (isAligned) EmeraldPrimary else Color(0xFF2C3E50)
                )

                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = center
                )
            }
        }

        // Bottom Telemetry & Status Card
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Alignment Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isAligned) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isAligned) Icons.Default.CheckCircle else Icons.Default.RotateRight,
                        contentDescription = "Status",
                        tint = if (isAligned) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAligned) "ALIGNED WITH KAABA" else "Rotate phone to align arrow with 🕋",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isAligned) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Telemetry Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "QIBLA BEARING",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${String.format(Locale.US, "%.1f", qiblaDirection)}°",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "DISTANCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${String.format(Locale.US, "%,.0f", uiState.distanceToKaabaKm)} km",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onRequestLocation() }
                    ) {
                        Text(
                            text = "DEVICE GPS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Locate",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Update",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }

    // Compass Calibration Dialog
    if (showCalibrationDialog) {
        var sliderValue by remember { mutableStateOf(uiState.compassOffsetDegrees) }

        AlertDialog(
            onDismissRequest = { showCalibrationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tune, contentDescription = "Calibration", tint = EmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Compass Calibration", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "1. Figure-8 Wave Gesture:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Move your phone smoothly in a Figure-8 (∞) pattern in the air 3 to 4 times to recalibrate the magnetic sensors against interference.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "2. Manual Magnetic Offset: ${if (sliderValue >= 0) "+${sliderValue.toInt()}°" else "${sliderValue.toInt()}°"}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Fine-tune to match true north or compensate for magnetic phone covers.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = -30f..30f,
                        steps = 60,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            sliderValue = 0f
                            onResetOffset()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset Offset to 0°")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSetOffset(sliderValue)
                        showCalibrationDialog = false
                    }
                ) {
                    Text("Apply Calibration")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCalibrationDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
