package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun MuslimProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeType: AppThemeType? = null,
    content: @Composable () -> Unit
) {
    val activeTheme by ThemeManager.currentTheme.collectAsStateWithLifecycle()
    val chosenTheme = themeType ?: activeTheme
    val colorScheme = ThemeManager.getColorScheme(chosenTheme, darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
