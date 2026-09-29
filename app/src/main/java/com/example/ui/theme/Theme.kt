package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = EmeraldOnPrimary,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = GoldSecondary,
    onSecondary = GoldOnSecondary,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = OnGoldContainer,
    tertiary = DeepTeal,
    onTertiary = Color.White,
    background = LightIvoryBackground,
    onBackground = TextPrimary,
    surface = SurfaceCrisp,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantMuted,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFFC4D1CB)
)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldDarkPrimary,
    onPrimary = Color(0xFF00382B),
    primaryContainer = Color(0xFF00513F),
    onPrimaryContainer = Color(0xFF73F9C0),
    secondary = GoldDarkSecondary,
    onSecondary = Color(0xFF432C00),
    secondaryContainer = Color(0xFF604100),
    onSecondaryContainer = Color(0xFFFFDF9D),
    tertiary = MintAccent,
    onTertiary = Color(0xFF00382B),
    background = EmeraldDarkBackground,
    onBackground = TextDarkPrimary,
    surface = EmeraldDarkSurface,
    onSurface = TextDarkPrimary,
    surfaceVariant = EmeraldDarkSurfaceVariant,
    onSurfaceVariant = TextDarkSecondary,
    outline = Color(0xFF3B4E47)
)

@Composable
fun MuslimProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
