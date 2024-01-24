package com.example.doit.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.UpdateDoneUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(
    getTodoItemsFlowUseCase: GetTodoItemsFlowUseCase,
    private val updateDoneUseCase: UpdateDoneUseCase
) : ViewModel() {

    val calendarState =
        com.example.doit.ui.composables.calendar.CalendarState(initialSelectedDate = LocalDate.now())

    private val selectedDateFlow = snapshotFlow { calendarState.selectedDate }

    private val items = getTodoItemsFlowUseCase.getItemFlow()
    private val datesWithItems = items.map { items ->
        items
            .distinctBy { it.dueDate }
            .mapNotNull { it.dueDate }
    }
    private val selectedDateItems = combine(items, selectedDateFlow) { items, selectedDate ->
        items.filter {
            it.dueDate == selectedDate
        }.toPersistentList()
    }

    val state = combine(datesWithItems, selectedDateItems) { dates, items ->
        CalendarState(
            datesWithItems = dates,
            items = items
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CalendarState(
                datesWithItems = emptyList(),
                items = persistentListOf()
            )
        )

    fun onDoneChanged(item: TodoItem, done: Boolean) {
        viewModelScope.launch {
            updateDoneUseCase(item, done)
        }
    }
}

@Immutable
data class CalendarState(
    val datesWithItems: List<LocalDate>,
    val items: PersistentList<TodoItem>
)