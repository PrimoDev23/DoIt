package com.example.doit.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.doit.R
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DateTimeDialog(
    value: LocalDateTime?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDateTime?) -> Unit
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberSaveable(
        value?.hour,
        value?.minute,
        saver = TimePickerState.Saver()
    ) {
        TimePickerState(
            initialHour = value?.hour ?: 0,
            initialMinute = value?.minute ?: 0,
            is24Hour = android.text.format.DateFormat.is24HourFormat(context),
        )
    }
    val confirmEnabled by remember {
        derivedStateOf {
            datePickerState.selectedDateMillis != null
        }
    }

    LaunchedEffect(value) {
        datePickerState.setSelection(value?.toInstant(ZoneOffset.UTC)?.toEpochMilli())
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val dateMs = datePickerState.selectedDateMillis

                    if (dateMs != null) {
                        val date = Instant.ofEpochMilli(dateMs).atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val time = LocalTime.of(timePickerState.hour, timePickerState.minute)

                        val dateTime = LocalDateTime.of(date, time)

                        onConfirm(dateTime)
                    }

                    onDismiss()
                },
                enabled = confirmEnabled
            ) {
                Text(text = stringResource(id = R.string.general_ok))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onConfirm(null)
                    onDismiss()
                }
            ) {
                Text(text = stringResource(id = R.string.general_clear))
            }
        }
    ) {
        TabRow(
            modifier = Modifier.height(56.dp),
            selectedTabIndex = pagerState.currentPage
        ) {
            Tab(
                modifier = Modifier.height(56.dp),
                selected = pagerState.currentPage == 0,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.date_time_dialog_date))
            }

            Tab(
                modifier = Modifier.height(56.dp),
                selected = pagerState.currentPage == 1,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.date_time_dialog_time))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalPager(
            modifier = Modifier.height(456.dp),
            state = pagerState
        ) { tab ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                when (tab) {
                    0 -> {
                        DatePicker(
                            state = datePickerState,
                            title = null
                        )
                    }

                    else -> {
                        TimePicker(
                            state = timePickerState
                        )
                    }
                }
            }
        }
    }
}