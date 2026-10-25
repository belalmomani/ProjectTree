package com.belalmomani.projecttree.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ProjectTreeColors = darkColorScheme(
    primary = Color(0xFF22D3EE),
    onPrimary = Color(0xFF062033),
    background = Color(0xFF0F1B2D),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF16263B),
    onSurface = Color(0xFFFFFFFF),
)

@Composable
fun ProjectTreeTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ProjectTreeColors, content = content)
}
