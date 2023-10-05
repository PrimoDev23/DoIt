package com.example.doit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.models.TodoItemSortType
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
        val filteredItems = if (state.hideDoneItems) {
            items.filter {
                !it.done
            }
        } else {
            items
        }
        val sortedItems = filteredItems.sort(state.sortType)

        TodoListState(
            items = sortedItems,
            selectedItems = state.selectedItems,
            sortType = state.sortType,
            hideDoneItems = state.hideDoneItems,
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
                sortType = TodoItemSortType.ALPHABETICAL,
                hideDoneItems = false,
                tags = emptyList(),
                selectedTag = null,
                selectedPriority = null
            )
        )

    private fun List<TodoItem>.sort(type: TodoItemSortType): List<TodoItem> {
        return when (type) {
            TodoItemSortType.ALPHABETICAL -> this.sortedBy {
                it.title.lowercase()
            }

            TodoItemSortType.PRIORITY -> this.sortedByDescending {
                it.priority
            }
        }
    }

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

    fun onHideDoneItemsChanged(hideDoneItems: Boolean) {
        _state.update {
            it.copy(hideDoneItems = hideDoneItems)
        }
    }

    fun onSortTypeChanged(sortType: TodoItemSortType) {
        _state.update {
            it.copy(sortType = sortType)
        }
    }
}

data class TodoListViewModelState(
    val selectedItems: List<TodoItem> = emptyList(),
    val sortType: TodoItemSortType = TodoItemSortType.ALPHABETICAL,
    val hideDoneItems: Boolean = false,
    val selectedTag: Tag? = null,
    val selectedPriority: Priority? = null
)

data class TodoListState(
    val items: List<TodoItem>,
    val selectedItems: List<TodoItem>,
    val sortType: TodoItemSortType,
    val hideDoneItems: Boolean,
    val tags: List<Tag>,
    val selectedTag: Tag?,
    val selectedPriority: Priority?
)