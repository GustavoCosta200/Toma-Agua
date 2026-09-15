package com.example.tomagua.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class TomaAguaExtraColors(
    val ringTrack: Color,
    val ringGradientEnd: Color,
    val confirmed: Color
)

private val LocalTomaAguaExtraColors = staticCompositionLocalOf {
    TomaAguaExtraColors(
        ringTrack = Color.Unspecified,
        ringGradientEnd = Color.Unspecified,
        confirmed = Color.Unspecified
    )
}

// Uso: MaterialTheme.extraColors.confirmed — segue o mesmo padrão de acesso
// de MaterialTheme.colorScheme e MaterialTheme.typography.
val MaterialTheme.extraColors: TomaAguaExtraColors
    @Composable
    get() = LocalTomaAguaExtraColors.current

private val TomaAguaColorScheme = lightColorScheme(
    primary = AccentTeal,
    onPrimary = Color.White,
    background = BgBlueGrey,
    onBackground = TextPrimaryNavy,
    surface = SurfaceWhite,
    onSurface = TextPrimaryNavy,
    surfaceVariant = SurfaceVariantMist,
    onSurfaceVariant = TextMutedSlate,
    error = ErrorRed,
    onError = Color.White
)

private val TomaAguaExtraColorsInstance = TomaAguaExtraColors(
    ringTrack = SurfaceVariantMist,
    ringGradientEnd = AccentTealLight,
    confirmed = ConfirmedGreen
)

@Composable
fun TomaAguaTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalTomaAguaExtraColors provides TomaAguaExtraColorsInstance) {
        MaterialTheme(
            colorScheme = TomaAguaColorScheme,
            typography = Typography(),
            content = content
        )
    }
}