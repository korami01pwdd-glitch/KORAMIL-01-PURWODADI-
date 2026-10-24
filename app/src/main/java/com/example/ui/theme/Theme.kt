package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF042103),
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = Color(0xFFB5F8AB),
    secondary = CamoKhakiContainer,
    onSecondary = Color(0xFF222B1E),
    tertiary = GoldContainer,
    onTertiary = OnGoldContainer,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    outline = DarkOutline
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ArmyGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = ArmyContainer,
    onPrimaryContainer = OnArmyContainer,
    secondary = CamoKhaki,
    onSecondary = Color.White,
    secondaryContainer = CamoKhakiContainer,
    onSecondaryContainer = Color(0xFF131D11),
    tertiary = GoldDark,
    onTertiary = Color.White,
    tertiaryContainer = GoldContainer,
    onTertiaryContainer = OnGoldContainer,
    background = NeutralBackground,
    onBackground = Color(0xFF171D15),
    surface = NeutralSurface,
    onSurface = Color(0xFF171D15),
    surfaceVariant = NeutralSurfaceVariant,
    onSurfaceVariant = Color(0xFF41493E),
    outline = NeutralOutline
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep Babinsa thematic green scheme consistent
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
