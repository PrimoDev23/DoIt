package com.example.doit.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Subtask
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.GetTodoItemFlowUseCase
import com.example.doit.domain.usecases.interfaces.UpdateSubtaskDoneUseCase
import com.example.doit.ui.arguments.TodoDetailNavArgs
import com.example.doit.ui.composables.screens.navArgs
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoDetailViewModel(
    savedStateHandle: SavedStateHandle,
    getTodoItemFlowUseCase: GetTodoItemFlowUseCase,
    private val updateSubtaskDoneUseCase: UpdateSubtaskDoneUseCase
) : ViewModel() {

    private val navArgs = savedStateHandle.navArgs<TodoDetailNavArgs>()

    private val todoItemFlow = getTodoItemFlowUseCase(navArgs.id)
    val state = todoItemFlow
        .map {
            TodoDetailState(
                item = it
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = TodoDetailState()
        )

    fun onSubtaskDoneChanged(
        subtask: Subtask,
        done: Boolean
    ) {
        viewModelScope.launch {
            updateSubtaskDoneUseCase(navArgs.id, subtask, done)
        }
    }

}

data class TodoDetailState(
    val item: TodoItem? = null
)