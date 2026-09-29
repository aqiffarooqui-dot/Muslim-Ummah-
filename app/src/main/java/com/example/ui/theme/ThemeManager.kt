package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeType(
    val title: String,
    val description: String,
    val isPremium: Boolean,
    val primaryColor: Color
) {
    EMERALD("Emerald Noor (Classic)", "Sacred green with soft ivory surface", false, Color(0xFF1B5E20)),
    ISLAMIC_GOLD("Imperial Gold", "Royal gold accents with luminous parchment", true, Color(0xFFC5A059)),
    PREMIUM_DARK("Nocturne Black", "Deep OLED dark with serene mint accents", true, Color(0xFF26A69A)),
    QURAN_PAPER("Warm Mushaf", "Traditional parchment paper for prolonged reading", true, Color(0xFF8D6E63)),
    MIDNIGHT("Celestial Midnight", "Deep sapphire navy for late-night Tahajjud", true, Color(0xFF1565C0)),
    MINIMAL("Clean Slate", "Apple-style monochromatic clarity", false, Color(0xFF37474F))
}

object ThemeManager {
    private val _currentTheme = MutableStateFlow(AppThemeType.EMERALD)
    val currentTheme: StateFlow<AppThemeType> = _currentTheme.asStateFlow()

    fun setTheme(theme: AppThemeType) {
        _currentTheme.value = theme
    }

    fun getColorScheme(theme: AppThemeType, isSystemDark: Boolean): ColorScheme {
        return when (theme) {
            AppThemeType.EMERALD -> if (isSystemDark) EmeraldDarkScheme else EmeraldLightScheme
            AppThemeType.ISLAMIC_GOLD -> if (isSystemDark) GoldDarkScheme else GoldLightScheme
            AppThemeType.PREMIUM_DARK -> PremiumDarkScheme
            AppThemeType.QURAN_PAPER -> QuranPaperScheme
            AppThemeType.MIDNIGHT -> MidnightDarkScheme
            AppThemeType.MINIMAL -> if (isSystemDark) MinimalDarkScheme else MinimalLightScheme
        }
    }

    // 1. Emerald
    val EmeraldLightScheme = lightColorScheme(
        primary = Color(0xFF1B5E20),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFC8E6C9),
        onPrimaryContainer = Color(0xFF003300),
        secondary = Color(0xFFC5A059),
        onSecondary = Color(0xFF183830),
        secondaryContainer = Color(0xFFFFF3D0),
        onSecondaryContainer = Color(0xFF4A3800),
        tertiary = Color(0xFF00796B),
        background = Color(0xFFFBFBF9),
        surface = Color.White,
        onSurface = Color(0xFF1C2522),
        surfaceVariant = Color(0xFFEEF2F0),
        onSurfaceVariant = Color(0xFF556560),
        outline = Color(0xFFC4D1CB)
    )

    val EmeraldDarkScheme = darkColorScheme(
        primary = Color(0xFF66BB6A),
        onPrimary = Color(0xFF00382B),
        primaryContainer = Color(0xFF00513F),
        onPrimaryContainer = Color(0xFF73F9C0),
        secondary = Color(0xFFFFD54F),
        onSecondary = Color(0xFF432C00),
        secondaryContainer = Color(0xFF604100),
        onSecondaryContainer = Color(0xFFFFDF9D),
        tertiary = Color(0xFF80CBC4),
        background = Color(0xFF0F1E19),
        surface = Color(0xFF152822),
        onSurface = Color(0xFFE2E9E6),
        surfaceVariant = Color(0xFF1C342C),
        onSurfaceVariant = Color(0xFFB0C0BA),
        outline = Color(0xFF3B4E47)
    )

    // 2. Gold
    val GoldLightScheme = lightColorScheme(
        primary = Color(0xFF9E7C2B),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFF9E7BA),
        onPrimaryContainer = Color(0xFF423000),
        secondary = Color(0xFF1B5E20),
        onSecondary = Color.White,
        background = Color(0xFFFDFBF7),
        surface = Color.White,
        onSurface = Color(0xFF262016),
        surfaceVariant = Color(0xFFF5EFE3),
        onSurfaceVariant = Color(0xFF6E6454),
        outline = Color(0xFFD9CDBC)
    )

    val GoldDarkScheme = darkColorScheme(
        primary = Color(0xFFFFD54F),
        onPrimary = Color(0xFF453000),
        primaryContainer = Color(0xFF634600),
        onPrimaryContainer = Color(0xFFFFE082),
        secondary = Color(0xFF81C784),
        onSecondary = Color(0xFF00382B),
        background = Color(0xFF1A160E),
        surface = Color(0xFF252016),
        onSurface = Color(0xFFEDE5D6),
        surfaceVariant = Color(0xFF342D20),
        onSurfaceVariant = Color(0xFFC7BBA8),
        outline = Color(0xFF594F3D)
    )

    // 3. Premium Dark (OLED Black)
    val PremiumDarkScheme = darkColorScheme(
        primary = Color(0xFF26A69A),
        onPrimary = Color.Black,
        primaryContainer = Color(0xFF004D40),
        onPrimaryContainer = Color(0xFF80CBC4),
        secondary = Color(0xFFFFD54F),
        onSecondary = Color.Black,
        background = Color(0xFF0A0A0A),
        surface = Color(0xFF121212),
        onSurface = Color(0xFFEEEEEE),
        surfaceVariant = Color(0xFF1E1E1E),
        onSurfaceVariant = Color(0xFFAAAAAA),
        outline = Color(0xFF333333)
    )

    // 4. Quran Paper (Mushaf Parchment)
    val QuranPaperScheme = lightColorScheme(
        primary = Color(0xFF6D4C41),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD7CCC8),
        onPrimaryContainer = Color(0xFF3E2723),
        secondary = Color(0xFF2E7D32),
        onSecondary = Color.White,
        background = Color(0xFFF7F3E9),
        surface = Color(0xFFFFFDF8),
        onSurface = Color(0xFF372E27),
        surfaceVariant = Color(0xFFEBE3D3),
        onSurfaceVariant = Color(0xFF685C50),
        outline = Color(0xFFC7BCAD)
    )

    // 5. Celestial Midnight
    val MidnightDarkScheme = darkColorScheme(
        primary = Color(0xFF42A5F5),
        onPrimary = Color(0xFF0D47A1),
        primaryContainer = Color(0xFF1565C0),
        onPrimaryContainer = Color(0xFFBBDEFB),
        secondary = Color(0xFFFFD54F),
        onSecondary = Color(0xFF432C00),
        background = Color(0xFF0B1426),
        surface = Color(0xFF101C36),
        onSurface = Color(0xFFE1E8F5),
        surfaceVariant = Color(0xFF18284C),
        onSurfaceVariant = Color(0xFFA5B5D6),
        outline = Color(0xFF283B6A)
    )

    // 6. Minimal
    val MinimalLightScheme = lightColorScheme(
        primary = Color(0xFF37474F),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFECEFF1),
        onPrimaryContainer = Color(0xFF263238),
        secondary = Color(0xFF1B5E20),
        onSecondary = Color.White,
        background = Color(0xFFFAFAFA),
        surface = Color.White,
        onSurface = Color(0xFF212121),
        surfaceVariant = Color(0xFFF0F0F0),
        onSurfaceVariant = Color(0xFF616161),
        outline = Color(0xFFE0E0E0)
    )

    val MinimalDarkScheme = darkColorScheme(
        primary = Color(0xFFCFD8DC),
        onPrimary = Color(0xFF263238),
        primaryContainer = Color(0xFF37474F),
        onPrimaryContainer = Color(0xFFECEFF1),
        secondary = Color(0xFF81C784),
        onSecondary = Color(0xFF00382B),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onSurface = Color(0xFFEEEEEE),
        surfaceVariant = Color(0xFF2C2C2C),
        onSurfaceVariant = Color(0xFFB0B0B0),
        outline = Color(0xFF444444)
    )
}
