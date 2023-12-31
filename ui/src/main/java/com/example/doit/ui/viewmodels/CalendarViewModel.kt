package com.example.doit.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.UpdateDoneUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CalendarViewModel(
    getTodoItemsFlowUseCase: GetTodoItemsFlowUseCase,
    private val updateDoneUseCase: UpdateDoneUseCase
) : ViewModel() {

    private val items = getTodoItemsFlowUseCase.getItemFlow()

    val state = items
        .map {
            CalendarState(
                items = it
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CalendarState(
                items = emptyList()
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
    val items: List<TodoItem>
)