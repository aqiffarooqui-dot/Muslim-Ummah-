package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CalculationMethod
import com.example.data.model.CityLocation
import com.example.data.model.JuristicMethod
import com.example.data.model.PrayerCalculator
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.viewmodel.MuslimUiState

@Composable
fun SettingsScreen(
    uiState: MuslimUiState,
    onSelectCity: (CityLocation) -> Unit,
    onSetMethod: (CalculationMethod) -> Unit,
    onSetJuristic: (JuristicMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCityDialog by remember { mutableStateOf(false) }
    var showMethodDialog by remember { mutableStateOf(false) }
    var showJuristicDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            Text(
                text = "Settings & Preferences",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Customize prayer times, methods, and app behavior",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Location & Prayer Calculations",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            SettingsClickableItem(
                icon = Icons.Default.LocationOn,
                title = "City Location",
                subtitle = "${uiState.selectedCity.name}, ${uiState.selectedCity.country}",
                onClick = { showCityDialog = true }
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsClickableItem(
                icon = Icons.Default.Calculate,
                title = "Prayer Calculation Method",
                subtitle = uiState.calculationMethod.title,
                onClick = { showMethodDialog = true }
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsClickableItem(
                icon = Icons.Default.Balance,
                title = "Asr Juristic Method",
                subtitle = uiState.juristicMethod.title,
                onClick = { showJuristicDialog = true }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Recitations & Audio",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            SettingsClickableItem(
                icon = Icons.Default.RecordVoiceOver,
                title = "Quran Reciter",
                subtitle = "Sheikh Mishary Rashid Alafasy (EveryAyah CDN)",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsClickableItem(
                icon = Icons.Default.Notifications,
                title = "Prayer Alerts & Adhan",
                subtitle = "Enabled for all 5 daily prayers",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "About Muslim Pro",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Muslim Pro Android",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Version 1.0.0 • Production Quality",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "A complete, modern Islamic assistant featuring accurate astronomical prayer times, dynamic Qibla compass, complete Holy Quran with high-quality streaming recitations, Hisn al-Muslim supplications, digital Tasbih counter with tactile haptics, and the 99 Names of Allah.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // City Selection Dialog
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("Select City") },
            text = {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    items(PrayerCalculator.POPULAR_CITIES) { city ->
                        val selected = city.name == uiState.selectedCity.name
                        Surface(
                            onClick = {
                                onSelectCity(city)
                                showCityDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "${city.name}, ${city.country}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) { Text("Close") }
            }
        )
    }

    // Method Dialog
    if (showMethodDialog) {
        AlertDialog(
            onDismissRequest = { showMethodDialog = false },
            title = { Text("Select Method") },
            text = {
                LazyColumn(modifier = Modifier.height(260.dp)) {
                    items(CalculationMethod.values()) { method ->
                        val selected = method == uiState.calculationMethod
                        Surface(
                            onClick = {
                                onSetMethod(method)
                                showMethodDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = method.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMethodDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Juristic Dialog
    if (showJuristicDialog) {
        AlertDialog(
            onDismissRequest = { showJuristicDialog = false },
            title = { Text("Asr Calculation Rule") },
            text = {
                Column {
                    JuristicMethod.values().forEach { juristic ->
                        val selected = juristic == uiState.juristicMethod
                        Surface(
                            onClick = {
                                onSetJuristic(juristic)
                                showJuristicDialog = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = juristic.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showJuristicDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = EmeraldPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
