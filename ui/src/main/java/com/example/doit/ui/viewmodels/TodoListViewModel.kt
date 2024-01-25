package com.example.doit.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.FilterTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodayTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoListPreferencesUseCase
import com.example.doit.domain.usecases.interfaces.SetHideDoneItemsUseCase
import com.example.doit.domain.usecases.interfaces.SetTodoItemSortTypeUseCase
import com.example.doit.domain.usecases.interfaces.SortTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.UpdateDoneUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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
    private val updateDoneUseCase: UpdateDoneUseCase,
    private val filterTodoItemsUseCase: FilterTodoItemsUseCase,
    private val sortTodoItemsUseCase: SortTodoItemsUseCase
) : ViewModel() {

    private val preferences = getTodoListPreferencesUseCase()
    private val hideDoneItems = preferences.map {
        it.hideDoneItems
    }
    private val sortType = preferences.map {
        it.sortType
    }

    private val todoItems = getTodoItemsFlowUseCase.getItemFlow()
    private val todayItems = getTodayTodoItemsFlowUseCase()

    private val _state = MutableStateFlow(TodoListViewModelState())

    private val todayFilterActive = MutableStateFlow(false)
    private val todayFilterStats = combine(todayFilterActive, todayItems) { filterActive, items ->
        val todayDone = items.count { it.done }
        val todayUndone = items.size - todayDone

        TodayFilterStats(
            todayFilterActive = filterActive,
            todayDone = todayDone,
            todayUndone = todayUndone
        )
    }

    private val unfilteredItems =
        combine(todayFilterActive, todoItems, todayItems) { todayFilterActive, items, todayItems ->
            if (todayFilterActive) {
                todayItems
            } else {
                items
            }
        }

    private val filterState = MutableStateFlow(TodoListFilterState())
    private val filteredItems =
        combine(unfilteredItems, filterState, hideDoneItems) { items, filter, hideDoneItems ->
            filterTodoItemsUseCase(
                items,
                filter.selectedTags,
                filter.selectedPriorities,
                hideDoneItems
            )
        }
    private val sortedItems = combine(filteredItems, sortType) { items, sortType ->
        sortTodoItemsUseCase(items, sortType)
    }

    private val tags = getTagsFlowUseCase.getFlow()

    val state = com.example.doit.domain.extensions.combine(
        _state,
        preferences,
        sortedItems,
        tags,
        todayFilterStats,
        filterState
    ) { state, preferences, items, tags, todayFilterStats, filterState ->
        TodoListState(
            todayFilterActive = todayFilterStats.todayFilterActive,
            todayUndone = todayFilterStats.todayUndone,
            todayDone = todayFilterStats.todayDone,
            items = items.toPersistentList(),
            selectedItems = state.selectedItems,
            sortType = preferences.sortType,
            hideDoneItems = preferences.hideDoneItems,
            tags = tags.toPersistentList(),
            selectedTags = filterState.selectedTags,
            selectedPriorities = filterState.selectedPriorities
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TodoListState(
                todayFilterActive = false,
                todayUndone = 0,
                todayDone = 0,
                items = persistentListOf(),
                selectedItems = persistentListOf(),
                sortType = TodoItemSortType.ALPHABETICAL,
                hideDoneItems = false,
                tags = persistentListOf(),
                selectedTags = persistentListOf(),
                selectedPriorities = persistentListOf()
            )
        )

    fun onClearSelectionClicked() {
        _state.update {
            it.copy(selectedItems = persistentListOf())
        }
    }

    fun onTodayInfoCardClicked(filterActive: Boolean) {
        viewModelScope.launch {
            todayFilterActive.emit(filterActive)
        }
    }

    fun onTagClicked(tag: Tag) {
        filterState.update { state ->
            val tags = state.selectedTags

            val newTags = if (tags.contains(tag)) {
                tags - tag
            } else {
                tags + tag
            }

            state.copy(selectedTags = newTags)
        }
    }

    fun onResetTagsClicked() {
        filterState.update {
            it.copy(selectedTags = persistentListOf())
        }
    }

    fun onPriorityClicked(priority: Priority) {
        filterState.update { state ->
            val priorities = state.selectedPriorities

            val newPriorities = if (priorities.contains(priority)) {
                priorities - priority
            } else {
                priorities + priority
            }

            state.copy(selectedPriorities = newPriorities)
        }
    }

    fun onResetPrioritiesClicked() {
        filterState.update {
            it.copy(selectedPriorities = persistentListOf())
        }
    }

    fun onItemSelected(item: TodoItem) {
        _state.update {
            val oldList = it.selectedItems
            val newList = if (oldList.contains(item)) {
                oldList - item
            } else {
                oldList + item
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
                it.copy(selectedItems = persistentListOf())
            }
        }
    }

    fun onEditClicked() {
        _state.update {
            it.copy(selectedItems = persistentListOf())
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
    val selectedItems: PersistentList<TodoItem> = persistentListOf()
)

data class TodoListFilterState(
    val selectedTags: PersistentList<Tag> = persistentListOf(),
    val selectedPriorities: PersistentList<Priority> = persistentListOf()
)

data class TodayFilterStats(
    val todayFilterActive: Boolean,
    val todayDone: Int,
    val todayUndone: Int
)

@Immutable
data class TodoListState(
    val todayFilterActive: Boolean,
    val todayUndone: Int,
    val todayDone: Int,
    val items: PersistentList<TodoItem>,
    val selectedItems: PersistentList<TodoItem>,
    val sortType: TodoItemSortType,
    val hideDoneItems: Boolean,
    val tags: PersistentList<Tag>,
    val selectedTags: PersistentList<Tag>,
    val selectedPriorities: PersistentList<Priority>
)