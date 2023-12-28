package com.example.doit.ui.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.doit.common.R

@Composable
fun ToolbarItem(
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = isVisible,
        enter = scaleIn(
            animationSpec = tween(durationMillis = 100)
        ),
        exit = scaleOut(
            animationSpec = tween(durationMillis = 100)
        ),
        content = content
    )
}

@Composable
fun DeleteToolbarItem(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ToolbarItem(
        modifier = modifier,
        isVisible = isVisible
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = R.drawable.outline_delete_24),
                contentDescription = stringResource(id = R.string.general_delete)
            )
        }
    }
}

@Composable
fun EditToolbarItem(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ToolbarItem(
        modifier = modifier,
        isVisible = isVisible
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = R.drawable.outline_edit_24),
                contentDescription = stringResource(id = R.string.general_edit)
            )
        }
    }
}

@Composable
fun FilterToolbarItem(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ToolbarItem(
        modifier = modifier,
        isVisible = isVisible
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_alt_24),
                contentDescription = stringResource(id = R.string.general_filter)
            )
        }
    }
}