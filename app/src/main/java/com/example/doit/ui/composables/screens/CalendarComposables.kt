package com.example.doit.ui.composables.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.doit.R
import com.example.doit.domain.models.TodoItem
import com.example.doit.ui.composables.DrawerMenuButton
import com.example.doit.ui.composables.RootScaffold
import com.example.doit.ui.composables.calendar.Calendar
import com.example.doit.ui.composables.calendar.CalendarWeek
import com.example.doit.ui.composables.calendar.rememberCalendarState
import com.example.doit.ui.viewmodels.CalendarViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import org.koin.androidx.compose.koinViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

private const val CALENDAR_ITEM_SIZE = 32
private const val DAY_SPACING = 8

@RootNavGraph
@Destination
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val calendarState = rememberCalendarState()

    RootScaffold(
        modifier = Modifier.fillMaxSize(),
        title = stringResource(id = R.string.calendar_title),
        navigationIcon = {
            DrawerMenuButton()
        },
        actions = {
            IconButton(
                onClick = {
                    calendarState.updateDisplayedMonth(LocalDate.now())
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_today_24),
                    contentDescription = stringResource(id = R.string.calendar_today_button)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            MonthSelection(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                selectedMonth = calendarState.displayMonth
            )

            val itemsForDate by remember {
                derivedStateOf {
                    state.items.filter { item ->
                        item.dueDate == calendarState.selectedDate
                    }
                }
            }

            Calendar(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .animateContentSize(),
                state = calendarState,
                header = {
                    CalendarHeader(modifier = Modifier.fillMaxWidth())
                },
                calendarWeek = { week ->
                    CalendarWeek(
                        modifier = Modifier
                            .fillMaxWidth(),
                        dates = week,
                        calendarDay = { date ->
                            val baseModifier = Modifier
                                .weight(1f)
                                .height(CALENDAR_ITEM_SIZE.dp)
                                .clip(RoundedCornerShape(8.dp))

                            if (date != null) {
                                val hasEntries by remember(date) {
                                    derivedStateOf {
                                        state.items.any { item ->
                                            item.dueDate == date
                                        }
                                    }
                                }

                                CalendarDay(
                                    modifier = baseModifier.clickable {
                                        calendarState.updateSelectedDate(date)
                                    },
                                    date = date,
                                    selected = calendarState.selectedDate == date,
                                    hasEntries = hasEntries
                                )
                            } else {
                                Spacer(modifier = baseModifier)
                            }
                        },
                        horizontalArrangement = Arrangement.spacedBy(DAY_SPACING.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            CalendarBottomSection(
                modifier = Modifier.weight(1f),
                itemsForDate = itemsForDate,
                onDoneChanged = { item, done ->
                    viewModel.onDoneChanged(item, done)
                }
            )
        }
    }
}

@Composable
fun MonthSelection(
    selectedMonth: LocalDate,
    modifier: Modifier = Modifier
) {
    val month = selectedMonth.month.getDisplayName(
        TextStyle.FULL,
        Locale.getDefault()
    )
    val text = "$month ${selectedMonth.year}"

    Crossfade(
        modifier = modifier,
        targetState = text,
        label = "MonthYearText"
    ) { innerText ->
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = innerText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CalendarHeader(modifier: Modifier = Modifier) {
    val days = remember {
        DayOfWeek.values().map {
            it.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DAY_SPACING.dp)
    ) {
        days.forEach { day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(CALENDAR_ITEM_SIZE.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(text = day)
            }
        }
    }
}

@Composable
fun CalendarDay(
    date: LocalDate,
    selected: Boolean,
    hasEntries: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp)
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    } else {
        Color.Unspecified
    }

    Box(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = shape
            )
            .then(
                if (hasEntries) {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = shape
                    )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = date.dayOfMonth.toString())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarBottomSection(
    itemsForDate: List<TodoItem>,
    onDoneChanged: (TodoItem, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        modifier = modifier,
        targetState = itemsForDate.isEmpty(),
        label = "CalendarBottomSection"
    ) { showEmptyState ->
        val baseModifier = Modifier.fillMaxSize()

        if (showEmptyState) {
            Box(
                modifier = baseModifier,
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(id = R.string.calendar_list_empty))
            }
        } else {
            LazyColumn(
                modifier = baseModifier,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(
                    items = itemsForDate,
                    key = { item ->
                        item.id
                    }
                ) { item ->
                    TodoItemListEntry(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(),
                        selected = false,
                        priority = item.priority,
                        done = item.done,
                        onDoneChanged = { done ->
                            onDoneChanged(item, done)
                        },
                        title = item.title,
                        description = item.description,
                        onClick = {},
                        onLongClick = {}
                    )
                }
            }
        }
    }
}