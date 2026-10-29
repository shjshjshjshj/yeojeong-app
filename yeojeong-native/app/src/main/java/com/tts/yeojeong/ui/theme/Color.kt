package com.tts.yeojeong.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFF0D70FF)
val PrimaryAlt = Color(0xFF0075FF)
val Accent = Color(0xFF516ED3)
val Ink = Color(0xFF000000)
val TextSecondary = Color(0xFF727996)
val TextTertiary = Color(0xFF8E8E8E)
val Disabled = Color(0xFFDCDEE5)
val Soft = Color(0xFFEAECF5)
val Page = Color(0xFFF7F7F7)
val PageAlt = Color(0xFFF8F9FB)
val SurfaceWhite = Color(0xFFFFFFFF)
val Highlight = Color(0xFFFFD12E)
val SelectedChip = Color(0xFF1D192B)
val Overlay = Color(0xCC4E4E4E)

val Line1 = Color(0xFF0052A4)
val Line2 = Color(0xFF00A84D)
val Line3 = Color(0xFFEF7C1C)
val Line4 = Color(0xFF00A5DE)
val Line5 = Color(0xFF996CAC)
val Line6 = Color(0xFFCD7C2F)
val Line7 = Color(0xFF747F00)
val Line8 = Color(0xFFE6186C)
val Line9 = Color(0xFFBB8336)

fun lineColor(line: String): Color = when {
    line.startsWith("1") -> Line1
    line.startsWith("2") -> Line2
    line.startsWith("3") -> Line3
    line.startsWith("4") -> Line4
    line.startsWith("5") -> Line5
    line.startsWith("6") -> Line6
    line.startsWith("7") -> Line7
    line.startsWith("8") -> Line8
    line.startsWith("9") -> Line9
    else -> Primary
}
