package com.example.doit.ui.composables.calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
private const val WEEKS_PER_MONTH = 6

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    state: CalendarState = rememberCalendarState(),
    header: @Composable () -> Unit = {
        CalendarHeader(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
    },
    calendarWeek: @Composable ColumnScope.(List<LocalDate?>) -> Unit = { week ->
        CalendarWeek(
            modifier = Modifier
                .height(ITEM_SIZE.dp)
                .fillMaxWidth(),
            dates = week,
            calendarDay = { date ->
                val baseModifier = Modifier.size(ITEM_SIZE.dp)

                if (date != null) {
                    CalendarDay(
                        modifier = baseModifier,
                        date = date,
                        selected = state.selectedDate == date
                    )
                } else {
                    Spacer(modifier = baseModifier)
                }
            }
        )
    },
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp)
) {
    LaunchedEffect(state.pagerState.settledPage) {
        state.onSettledPageChanged(state.pagerState.settledPage)
    }

    Column(
        modifier = modifier,
        verticalArrangement = verticalArrangement
    ) {
        header()

        HorizontalPager(
            modifier = Modifier.fillMaxWidth(),
            state = state.pagerState
        ) { page ->
            val weeks = state.months[page]

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = verticalArrangement
            ) {
                repeat(WEEKS_PER_MONTH) { index ->
                    if (index <= weeks.lastIndex) {
                        val week = weeks[index]

                        calendarWeek(week)
                    } else {
                        calendarWeek(emptyList())
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarHeader(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly
) {
    val days = remember {
        DayOfWeek.entries.map {
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
    calendarDay: @Composable RowScope.(LocalDate?) -> Unit,
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
    Calendar(modifier = Modifier.fillMaxWidth())
}