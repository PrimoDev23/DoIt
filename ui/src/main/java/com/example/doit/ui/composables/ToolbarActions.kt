package com.example.doit.ui.composables

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.doit.common.R

@Composable
fun DeleteToolbarItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_delete_24),
            contentDescription = stringResource(id = R.string.general_delete)
        )
    }
}

@Composable
fun EditToolbarItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_edit_24),
            contentDescription = stringResource(id = R.string.general_edit)
        )
    }
}

@Composable
fun FilterToolbarItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.outline_filter_alt_24),
            contentDescription = stringResource(id = R.string.general_filter)
        )
    }
}