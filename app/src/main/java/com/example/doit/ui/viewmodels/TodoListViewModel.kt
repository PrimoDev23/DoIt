package com.example.doit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    getTodoItemsFlowUseCase: GetTodoItemsFlowUseCase,
    private val saveTodoItemUseCase: SaveTodoItemUseCase,
    private val deleteTodoItemsUseCase: DeleteTodoItemsUseCase
) : ViewModel() {

    private val todoItems = getTodoItemsFlowUseCase.getItemFlow()

    private val _state = MutableStateFlow(TodoListViewModelState())
    val state = combine(_state, todoItems) { state, items ->
        TodoListState(
            items = items,
            selectedItems = state.selectedItems
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TodoListState(
                items = emptyList(),
                selectedItems = emptyList()
            )
        )

    fun onItemSelected(item: TodoItem) {
        _state.update {
            val newList = it.selectedItems.toMutableList()

            if (newList.contains(item)) {
                newList.remove(item)
            } else {
                newList.add(item)
            }

            it.copy(selectedItems = newList)
        }
    }

    fun onDoneChanged(item: TodoItem, done: Boolean) {
        viewModelScope.launch {
            val newItem = item.copy(done = done)

            saveTodoItemUseCase.save(newItem)
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            val items = state.value.selectedItems

            deleteTodoItemsUseCase.delete(items)

            _state.update {
                it.copy(selectedItems = emptyList())
            }
        }
    }

    fun onEditClicked() {
        _state.update {
            it.copy(selectedItems = emptyList())
        }
    }
}

data class TodoListViewModelState(
    val selectedItems: List<TodoItem> = emptyList()
)

data class TodoListState(
    val items: List<TodoItem>,
    val selectedItems: List<TodoItem>
)