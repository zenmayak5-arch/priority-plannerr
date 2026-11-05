package com.priorityplanner.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.priorityplanner.app.data.model.ThirdColorOption

// Priority 1 Red Colors (Must-Do)
val PriorityRedLight = Color(0xFFDC2626)
val PriorityRedDark = Color(0xFFF87171)
val PriorityRedContainerLight = Color(0xFFFEE2E2)
val PriorityRedContainerDark = Color(0xFF450A0A)
val PriorityRedBorder = Color(0xFFEF4444)

// Priority 2 Yellow/Amber Colors (Should-Do)
val PriorityYellowLight = Color(0xFFD97706)
val PriorityYellowDark = Color(0xFFFBBF24)
val PriorityYellowContainerLight = Color(0xFFFEF3C7)
val PriorityYellowContainerDark = Color(0xFF451A03)
val PriorityYellowBorder = Color(0xFFF59E0B)

// Neutral Base Colors
val DarkBackground = Color(0xFF0F172A)
val DarkSurface = Color(0xFF1E293B)
val DarkSurfaceVariant = Color(0xFF334155)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)

val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF64748B)

data class PriorityColorScheme(
    val color: Color,
    val containerColor: Color,
    val contentColor: Color,
    val borderColor: Color
)

fun getPriorityColors(
    priority: Int,
    thirdColorOption: ThirdColorOption,
    isDark: Boolean
): PriorityColorScheme {
    return when (priority) {
        1 -> PriorityColorScheme(
            color = if (isDark) PriorityRedDark else PriorityRedLight,
            containerColor = if (isDark) PriorityRedContainerDark else PriorityRedContainerLight,
            contentColor = if (isDark) PriorityRedDark else PriorityRedLight,
            borderColor = PriorityRedBorder
        )
        2 -> PriorityColorScheme(
            color = if (isDark) PriorityYellowDark else PriorityYellowLight,
            containerColor = if (isDark) PriorityYellowContainerDark else PriorityYellowContainerLight,
            contentColor = if (isDark) PriorityYellowDark else PriorityYellowLight,
            borderColor = PriorityYellowBorder
        )
        else -> {
            val mainColor = Color(if (isDark) thirdColorOption.darkColor else thirdColorOption.lightColor)
            val container = Color(if (isDark) thirdColorOption.containerDark else thirdColorOption.containerLight)
            PriorityColorScheme(
                color = mainColor,
                containerColor = container,
                contentColor = mainColor,
                borderColor = mainColor
            )
        }
    }
}
