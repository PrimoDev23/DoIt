package com.example.doit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    getTodoItemsFlowUseCase: GetTodoItemsFlowUseCase,
    getTagsFlowUseCase: GetTagsFlowUseCase,
    private val saveTodoItemUseCase: SaveTodoItemUseCase,
    private val deleteTodoItemsUseCase: DeleteTodoItemsUseCase
) : ViewModel() {

    private val todoItems = getTodoItemsFlowUseCase.getItemFlow()
    private val tags = getTagsFlowUseCase.getFlow()

    private val _state = MutableStateFlow(TodoListViewModelState())
    val state = combine(_state, todoItems, tags) { state, items, tags ->
        TodoListState(
            items = items,
            selectedItems = state.selectedItems,
            tags = tags,
            selectedTag = state.selectedTag,
            selectedPriority = state.selectedPriority
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TodoListState(
                items = emptyList(),
                selectedItems = emptyList(),
                tags = emptyList(),
                selectedTag = null,
                selectedPriority = null
            )
        )

    init {
        viewModelScope.launch {
            updateSelectedTag()
        }
    }

    private suspend fun updateSelectedTag() {
        tags.collectLatest {
            val selectedTag = _state.value.selectedTag

            if (selectedTag != null && !it.contains(selectedTag)) {
                _state.update { state ->
                    state.copy(selectedTag = null)
                }
            }
        }
    }

    fun onClearSelectionClicked() {
        _state.update {
            it.copy(selectedItems = emptyList())
        }
    }

    fun onTagFilterClicked(tag: Tag?) {
        _state.update {
            it.copy(selectedTag = tag)
        }
    }

    fun onPrioritySelected(priority: Priority?) {
        _state.update {
            it.copy(selectedPriority = priority)
        }
    }

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
    val selectedItems: List<TodoItem> = emptyList(),
    val selectedTag: Tag? = null,
    val selectedPriority: Priority? = null
)

data class TodoListState(
    val items: List<TodoItem>,
    val selectedItems: List<TodoItem>,
    val tags: List<Tag>,
    val selectedTag: Tag?,
    val selectedPriority: Priority?
)