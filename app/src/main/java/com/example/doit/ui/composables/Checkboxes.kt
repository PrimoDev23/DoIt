package com.example.doit.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.doit.R

@Composable
fun DoItCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = CircleShape,
    colors: DoItCheckboxColors = DoItCheckboxDefaults.colors()
) {
    val borderColor = remember(enabled, checked, colors) {
        when {
            enabled && checked -> colors.checkedColor
            enabled && !checked -> colors.uncheckedColor
            !enabled && checked -> colors.disabledCheckedColor
            else -> colors.disabledUncheckedColor
        }
    }
    val boxColor = remember(enabled, checked, colors) {
        when {
            enabled && checked -> colors.checkedColor
            !enabled && checked -> colors.disabledCheckedColor
            else -> Color.Transparent
        }
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        label = "CheckboxBorderAnimation"
    )
    val animatedBoxColor by animateColorAsState(
        targetValue = boxColor,
        label = "CheckboxBoxAnimation"
    )

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .requiredSize(20.dp)
            .clip(shape)
            .border(
                width = 2.dp,
                color = animatedBorderColor,
                shape = shape
            )
            .background(
                color = animatedBoxColor,
                shape = shape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = {
                    onCheckedChange(!checked)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(visible = checked) {
            Icon(
                modifier = Modifier.padding(2.dp),
                painter = painterResource(id = R.drawable.baseline_check_24),
                contentDescription = null,
                tint = colors.checkmarkColor
            )
        }
    }
}

@Preview
@Composable
fun DoItCheckboxPreview() {
    DoItCheckbox(checked = true,
        onCheckedChange = {})
}

object DoItCheckboxDefaults {
    @Composable
    fun colors(
        checkedColor: Color = MaterialTheme.colorScheme.primary,
        uncheckedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        checkmarkColor: Color = MaterialTheme.colorScheme.onPrimary,
        disabledCheckedColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
        disabledUncheckedColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    ): DoItCheckboxColors {
        return DoItCheckboxColors(
            checkedColor = checkedColor,
            uncheckedColor = uncheckedColor,
            checkmarkColor = checkmarkColor,
            disabledCheckedColor = disabledCheckedColor,
            disabledUncheckedColor = disabledUncheckedColor
        )
    }
}

data class DoItCheckboxColors(
    val checkedColor: Color,
    val uncheckedColor: Color,
    val checkmarkColor: Color,
    val disabledCheckedColor: Color,
    val disabledUncheckedColor: Color
)