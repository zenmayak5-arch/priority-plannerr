package com.priorityplanner.app.data.model

import androidx.compose.ui.graphics.Color

/**
 * Priority represents the 3-tier task importance system:
 * - RED: Priority 1 - Must Do / Non-negotiable tasks
 * - YELLOW: Priority 2 - Important secondary goals
 * - COLOR_3: Priority 3 - Today's routine & bonus tasks (customizable color)
 */
enum class Priority(val level: Int, val title: String, val subtitle: String) {
    RED(
        level = 1,
        title = "Priority 1 (Must Do)",
        subtitle = "Critical & absolutely necessary tasks"
    ),
    YELLOW(
        level = 2,
        title = "Priority 2 (Important)",
        subtitle = "Secondary high-value goals"
    ),
    COLOR_3(
        level = 3,
        title = "Priority 3 (Today)",
        subtitle = "Daily routine & other tasks for today"
    );

    companion object {
        fun fromLevel(level: Int): Priority {
            return entries.find { it.level == level } ?: COLOR_3
        }
    }
}

/**
 * Palette options available for the 3rd priority color
 */
enum class ThirdColorOption(
    val id: String,
    val displayName: String,
    val lightColor: Long,
    val darkColor: Long,
    val containerLight: Long,
    val containerDark: Long
) {
    EMERALD_GREEN(
        id = "green",
        displayName = "Emerald Green",
        lightColor = 0xFF10B981,
        darkColor = 0xFF34D399,
        containerLight = 0xFFD1FAE5,
        containerDark = 0xFF064E3B
    ),
    SAPPHIRE_BLUE(
        id = "blue",
        displayName = "Sapphire Blue",
        lightColor = 0xFF3B82F6,
        darkColor = 0xFF60A5FA,
        containerLight = 0xFFDBEAFE,
        containerDark = 0xFF1E3A8A
    ),
    AMETHYST_PURPLE(
        id = "purple",
        displayName = "Amethyst Purple",
        lightColor = 0xFF8B5CF6,
        darkColor = 0xFFA78BFA,
        containerLight = 0xFFEDE9FE,
        containerDark = 0xFF4C1D95
    ),
    OCEAN_TEAL(
        id = "teal",
        displayName = "Ocean Teal",
        lightColor = 0xFF14B8A6,
        darkColor = 0xFF2DD4BF,
        containerLight = 0xFFCCFBF1,
        containerDark = 0xFF134E4A
    ),
    SUNSET_PINK(
        id = "pink",
        displayName = "Rose Pink",
        lightColor = 0xFFEC4899,
        darkColor = 0xFFF472B6,
        containerLight = 0xFFFCE7F3,
        containerDark = 0xFF831843
    );

    companion object {
        fun fromId(id: String): ThirdColorOption {
            return entries.find { it.id == id } ?: EMERALD_GREEN
        }
    }
}
