package com.priorityplanner.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.priorityplanner.app.data.model.Priority
import com.priorityplanner.app.data.model.ThirdColorOption
import com.priorityplanner.app.ui.theme.getPriorityColors

@Composable
fun PriorityBadge(
    priority: Int,
    thirdColorOption: ThirdColorOption,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val colors = getPriorityColors(priority, thirdColorOption, isDark)
    val label = when (priority) {
        1 -> "P1 • MUST DO"
        2 -> "P2 • IMPORTANT"
        else -> "P3 • TODAY"
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.containerColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(colors.color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colors.contentColor
        )
    }
}

@Composable
fun PrioritySelectionCard(
    priority: Priority,
    selectedPriority: Priority,
    thirdColorOption: ThirdColorOption,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = priority == selectedPriority
    val colors = getPriorityColors(priority.level, thirdColorOption, isDark)

    val borderColor = if (isSelected) colors.color else MaterialTheme.colorScheme.surfaceVariant
    val bgColor = if (isSelected) colors.containerColor else MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(colors.color)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = priority.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isSelected) colors.contentColor else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = priority.subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = colors.color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
