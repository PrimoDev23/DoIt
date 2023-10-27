package com.example.doit.ui.composables

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.doit.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DateTextField(
    value: LocalDate?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    formatter: DateTimeFormatter = remember {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    },
    emptyDate: String = stringResource(id = R.string.general_no_date)
) {
    val text = remember(value) {
        value?.format(formatter) ?: emptyDate
    }

    DoItTextField(
        modifier = modifier,
        value = text,
        onValueChange = {},
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = onClick) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_edit_calendar_24),
                    contentDescription = null
                )
            }
        }
    )
}