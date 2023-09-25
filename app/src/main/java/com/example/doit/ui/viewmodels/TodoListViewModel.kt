package com.example.doit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    getTodoItemsFlowUseCase: GetTodoItemsFlowUseCase
) : ViewModel() {

    private val todoItems = getTodoItemsFlowUseCase.getItemFlow()

    val state = todoItems
        .map {
            TodoListState(
                items = it
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TodoListState()
        )

}

data class TodoListState(
    val items: List<TodoItem> = emptyList()
)