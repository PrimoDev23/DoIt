package com.example.doit.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Subtask
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemFlowUseCase
import com.example.doit.domain.usecases.interfaces.UpdateSubtaskDoneUseCase
import com.example.doit.ui.arguments.TodoDetailNavArgs
import com.example.doit.ui.composables.screens.navArgs
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoDetailViewModel(
    savedStateHandle: SavedStateHandle,
    getTodoItemFlowUseCase: GetTodoItemFlowUseCase,
    private val updateSubtaskDoneUseCase: UpdateSubtaskDoneUseCase,
    private val deleteTodoItemsUseCase: DeleteTodoItemsUseCase
) : ViewModel() {

    private val navArgs = savedStateHandle.navArgs<TodoDetailNavArgs>()

    private val _events = Channel<TodoDetailEvent>()
    val events = _events.receiveAsFlow()

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

    fun onDeleteClicked() {
        viewModelScope.launch {
            state.value.item?.let { item ->
                deleteTodoItemsUseCase.delete(listOf(item))

                _events.send(TodoDetailEvent.PopBackStack)
            }
        }
    }

}

@Immutable
data class TodoDetailState(
    val item: TodoItem? = null
)

sealed interface TodoDetailEvent {
    data object PopBackStack : TodoDetailEvent
}