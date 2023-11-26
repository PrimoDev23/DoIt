package com.example.doit.ui.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private const val ITEM_SIZE = 32

@Composable
fun Calendar(
    displayMonth: LocalDate,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit = {
        CalendarHeader(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
    },
    calendarWeek: @Composable ColumnScope.(List<LocalDate?>) -> Unit = { week ->
        CalendarWeek(
            modifier = Modifier.fillMaxWidth(),
            dates = week,
            calendarDay = { date ->
                val baseModifier = Modifier.size(ITEM_SIZE.dp)

                if (date != null) {
                    CalendarDay(
                        modifier = baseModifier,
                        date = date,
                        selected = displayMonth == date
                    )
                } else {
                    Spacer(modifier = baseModifier)
                }
            }
        )
    },
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp)
) {
    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        header()

        val weeks = remember(displayMonth) {
            getDatesForMonth(displayMonth)
        }

        weeks.forEach { dates ->
            calendarWeek(dates)
        }
    }
}

@Composable
fun CalendarHeader(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly
) {
    val days = remember {
        DayOfWeek.values().map {
            it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement
    ) {
        days.forEach { day ->
            Box(
                modifier = Modifier.size(ITEM_SIZE.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = day
                )
            }
        }
    }
}

@Composable
fun CalendarWeek(
    dates: List<LocalDate?>,
    calendarDay: @Composable (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement
    ) {
        dates.forEach { date ->
            calendarDay(date)
        }
    }
}

@Composable
fun CalendarDay(
    date: LocalDate,
    selected: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        } else {
            Color.Unspecified
        },
        label = "CalendarDayBackgroundColor"
    )

    Box(
        modifier = modifier.background(
            color = backgroundColor,
            shape = shape
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = date.dayOfMonth.toString())
    }
}

@Preview
@Composable
fun CalendarPreview() {
    val selectedDate = LocalDate.now()

    Calendar(
        modifier = Modifier.fillMaxWidth(),
        displayMonth = selectedDate
    )
}

private fun getDatesForMonth(date: LocalDate): List<List<LocalDate?>> {
    var currentDate = date.withDayOfMonth(1)
    val lengthOfMonth = date.lengthOfMonth()
    val endDate = date.withDayOfMonth(lengthOfMonth)

    val month = mutableListOf<List<LocalDate?>>()
    var week = mutableListOf<LocalDate?>()

    do {
        week.add(currentDate)

        if (currentDate.dayOfWeek == DayOfWeek.SUNDAY) {
            if (week.size < 7) {
                val diff = 7 - week.size

                repeat(diff) {
                    week.add(0, null)
                }
            }

            month.add(week)
            week = mutableListOf()
        }

        currentDate = currentDate.plusDays(1)
    } while (currentDate <= endDate)

    if (week.isNotEmpty()) {
        if (week.size < 7) {
            val diff = 7 - week.size

            repeat(diff) {
                week.add(null)
            }
        }

        month.add(week)
    }

    return month
}