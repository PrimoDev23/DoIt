package com.example.doit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    getTodoItemsFlowUseCase: GetTodoItemsFlowUseCase,
    private val saveTodoItemUseCase: SaveTodoItemUseCase,
    private val deleteTodoItemUseCase: DeleteTodoItemUseCase
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

    fun onDoneChanged(item: TodoItem, done: Boolean) {
        viewModelScope.launch {
            val newItem = item.copy(done = done)

            saveTodoItemUseCase.save(newItem)
        }
    }

    fun onItemDismissed(item: TodoItem) {
        viewModelScope.launch {
            deleteTodoItemUseCase.delete(item)
        }
    }

}

data class TodoListState(
    val items: List<TodoItem> = emptyList()
)