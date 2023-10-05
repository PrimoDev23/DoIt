package com.example.doit.ui.composables

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.doit.R

@Composable
fun ToggleableDropDownMenuItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        modifier = modifier,
        text = {
            Text(text = text)
        },
        leadingIcon = {
            if (checked) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_check_24),
                    contentDescription = null
                )
            }
        },
        onClick = {
            onCheckedChange(!checked)
        }
    )
}