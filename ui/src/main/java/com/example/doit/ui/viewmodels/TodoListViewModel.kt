package com.example.doit.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodayTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoListPreferencesUseCase
import com.example.doit.domain.usecases.interfaces.SetHideDoneItemsUseCase
import com.example.doit.domain.usecases.interfaces.SetTodoItemSortTypeUseCase
import com.example.doit.domain.usecases.interfaces.UpdateDoneUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TodoListViewModel(
    getTodoListPreferencesUseCase: GetTodoListPreferencesUseCase,
    getTodoItemsFlowUseCase: GetTodoItemsFlowUseCase,
    getTagsFlowUseCase: GetTagsFlowUseCase,
    getTodayTodoItemsFlowUseCase: GetTodayTodoItemsFlowUseCase,
    private val deleteTodoItemsUseCase: DeleteTodoItemsUseCase,
    private val setTodoItemSortTypeUseCase: SetTodoItemSortTypeUseCase,
    private val setHideDoneItemsUseCase: SetHideDoneItemsUseCase,
    private val updateDoneUseCase: UpdateDoneUseCase
) : ViewModel() {

    private val preferences = getTodoListPreferencesUseCase()
    private val todoItems = getTodoItemsFlowUseCase.getItemFlow()
    private val tags = getTagsFlowUseCase.getFlow()
    private val todayItems = getTodayTodoItemsFlowUseCase()

    private val _state = MutableStateFlow(TodoListViewModelState())
    val state = combine(
        _state,
        preferences,
        todoItems,
        tags,
        todayItems
    ) { state, preferences, items, tags, todayItems ->
        val allItems = if (state.todayFilterActive) {
            todayItems
        } else {
            items
        }

        val todayUndone = todayItems.count { !it.done }
        val todayDone = todayItems.count { it.done }

        TodoListState(
            todayFilterActive = state.todayFilterActive,
            todayUndone = todayUndone,
            todayDone = todayDone,
            items = allItems,
            selectedItems = state.selectedItems,
            sortType = preferences.sortType,
            hideDoneItems = preferences.hideDoneItems,
            tags = tags,
            selectedTag = state.selectedTag,
            selectedPriority = state.selectedPriority
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TodoListState(
                todayFilterActive = false,
                todayUndone = 0,
                todayDone = 0,
                items = emptyList(),
                selectedItems = emptyList(),
                sortType = TodoItemSortType.ALPHABETICAL,
                hideDoneItems = false,
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

    fun onTodayInfoCardClicked(filterActive: Boolean) {
        _state.update {
            it.copy(todayFilterActive = filterActive)
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
            updateDoneUseCase(item, done)
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
        viewModelScope.launch {
            setHideDoneItemsUseCase(hideDoneItems)
        }
    }

    fun onSortTypeChanged(sortType: TodoItemSortType) {
        viewModelScope.launch {
            setTodoItemSortTypeUseCase(sortType)
        }
    }
}

data class TodoListViewModelState(
    val todayFilterActive: Boolean = false,
    val selectedItems: List<TodoItem> = emptyList(),
    val selectedTag: Tag? = null,
    val selectedPriority: Priority? = null
)

@Immutable
data class TodoListState(
    val todayFilterActive: Boolean,
    val todayUndone: Int,
    val todayDone: Int,
    val items: List<TodoItem>,
    val selectedItems: List<TodoItem>,
    val sortType: TodoItemSortType,
    val hideDoneItems: Boolean,
    val tags: List<Tag>,
    val selectedTag: Tag?,
    val selectedPriority: Priority?
)