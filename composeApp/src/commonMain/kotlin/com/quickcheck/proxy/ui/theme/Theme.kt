package com.quickcheck.proxy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightScheme = lightColorScheme(
    primary = Color(0xFFC15F3C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF6E1D4),
    onPrimaryContainer = Color(0xFF3A1A0E),
    secondary = Color(0xFF7D6759),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF4A6B4F),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDDEAD8),
    error = Color(0xFFB42318),
    errorContainer = Color(0xFFFCDED9),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFAF9F5),
    onBackground = Color(0xFF1F1F1E),
    surface = Color(0xFFFAF9F5),
    onSurface = Color(0xFF1F1F1E),
    surfaceVariant = Color(0xFFF0EBE2),
    onSurfaceVariant = Color(0xFF514538),
    outline = Color(0xFFB5A89C),
    outlineVariant = Color(0xFFE0D8CC),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF8F4EC),
    surfaceContainer = Color(0xFFF3EEE5),
    surfaceContainerHigh = Color(0xFFEDE7DC),
    surfaceContainerHighest = Color(0xFFE6DFD2),
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFE89B7C),
    onPrimary = Color(0xFF522817),
    primaryContainer = Color(0xFF6E3B26),
    onPrimaryContainer = Color(0xFFFFDBCC),
    secondary = Color(0xFFD7BFB1),
    tertiary = Color(0xFFA9C4A8),
    background = Color(0xFF1A1916),
    onBackground = Color(0xFFEAE5DC),
    surface = Color(0xFF1A1916),
    onSurface = Color(0xFFEAE5DC),
    surfaceVariant = Color(0xFF453F36),
    onSurfaceVariant = Color(0xFFD1C7BA),
    outline = Color(0xFF9C9182),
    outlineVariant = Color(0xFF45403A),
    surfaceContainer = Color(0xFF252320),
    surfaceContainerHigh = Color(0xFF302D29),
)

private val AppTypography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium, fontSize = 22.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium, fontSize = 20.sp),
    headlineSmall = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Medium, fontSize = 18.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp),
    bodyLarge = TextStyle(fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
    bodySmall = TextStyle(fontSize = 11.sp, lineHeight = 15.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 13.sp, letterSpacing = 0.4.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.8.sp),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = AppTypography,
        content = content,
    )
}
