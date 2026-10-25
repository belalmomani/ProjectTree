package com.belalmomani.projecttree.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ProjectTreeColors = darkColorScheme(
    primary = Color(0xFF22D3EE),
    onPrimary = Color(0xFF062033),
    secondaryContainer = Color(0xFF0E4A5A),
    onSecondaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFF0C182E),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF14243C),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1B2F4B),
    onSurfaceVariant = Color(0xFFC5D3E6),
    outline = Color(0xFF4A6285),
)

@Composable
fun ProjectTreeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ProjectTreeColors, content = content)
}
