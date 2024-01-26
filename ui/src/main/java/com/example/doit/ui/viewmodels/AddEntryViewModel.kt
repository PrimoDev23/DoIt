package com.example.doit.ui.viewmodels

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Subtask
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import com.example.doit.ui.arguments.AddEntryNavArgs
import com.example.doit.ui.composables.screens.navArgs
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
class AddEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val saveTodoItemUseCase: SaveTodoItemUseCase,
    getTagsFlowUseCase: GetTagsFlowUseCase,
    private val getTodoItemUseCase: GetTodoItemUseCase,
    private val deleteTodoItemsUseCase: DeleteTodoItemsUseCase
) : ViewModel() {

    private val navArgs: AddEntryNavArgs = savedStateHandle.navArgs()

    private val id: String = navArgs.id
    private var existingItem: TodoItem? = null

    private val _events = Channel<AddEntryEvent>()
    val events = _events.receiveAsFlow()

    private val tags = getTagsFlowUseCase.getFlow()
    private val tagSearchTerm = MutableStateFlow("")
    private val searchedTags = combine(tags, tagSearchTerm) { tags, term ->
        tags.filter {
            it.title.contains(
                other = term,
                ignoreCase = true
            )
        }
    }

    private val _state = MutableStateFlow(
        AddEntryViewModelState(
            title = "",
            description = "",
            dueDate = null,
            notificationDateTime = null,
            selectedTags = persistentListOf(),
            priority = Priority.NONE,
            subtasks = persistentListOf()
        )
    )

    val state = combine(_state, searchedTags, tagSearchTerm) { state, tags, tagSearchTerm ->
        with(state) {
            AddEntryState(
                title = title,
                titleHasError = title.isBlank(),
                description = description,
                dueDate = dueDate,
                notificationDateTime = notificationDateTime,
                tagSearchTerm = tagSearchTerm,
                tags = tags.toPersistentList(),
                selectedTags = selectedTags,
                priority = priority,
                subtasks = subtasks
            )
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = AddEntryState(
                title = "",
                titleHasError = false,
                description = "",
                dueDate = null,
                notificationDateTime = null,
                tagSearchTerm = "",
                tags = persistentListOf(),
                selectedTags = persistentListOf(),
                priority = Priority.NONE,
                subtasks = persistentListOf()
            )
        )

    val datePickerState = DatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli(),
        initialDisplayedMonthMillis = null,
        yearRange = DatePickerDefaults.YearRange,
        initialDisplayMode = DisplayMode.Picker
    )

    init {
        viewModelScope.launch {
            launch {
                initData()
            }
        }
    }

    private suspend fun initData() {
        getTodoItemUseCase(id)?.let { item ->
            existingItem = item

            val millis = item.dueDate?.let {
                val startOfDay = it.atStartOfDay().atZone(ZoneId.systemDefault())
                startOfDay.toInstant().toEpochMilli()
            } ?: Instant.now().toEpochMilli()

            datePickerState.setSelection(millis)

            _state.update { state ->
                with(item) {
                    state.copy(
                        title = title,
                        description = description,
                        selectedTags = tags,
                        priority = priority,
                        dueDate = dueDate,
                        notificationDateTime = notificationDateTime,
                        subtasks = subtasks.toPersistentList()
                    )
                }
            }
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            sendPopBackStack()
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            val existingItem = this@AddEntryViewModel.existingItem

            if (existingItem != null) {
                deleteTodoItemsUseCase.delete(listOf(existingItem))
            }

            sendPopBackStack()
        }
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            if (state.value.isValid()) {
                val todoItem = state.value.toTodoItem()

                saveTodoItemUseCase.save(todoItem)

                sendPopBackStack()
            }
        }
    }

    fun onTitleChanged(title: String) {
        updateTitle(title)
    }

    fun onDescriptionChanged(description: String) {
        updateDescription(description)
    }

    fun onDateCleared() {
        datePickerState.setSelection(Instant.now().toEpochMilli())

        _state.update {
            it.copy(dueDate = null)
        }
    }

    fun onDateConfirmed() {
        datePickerState.selectedDateMillis?.let { millis ->
            _state.update {
                val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()

                it.copy(dueDate = date)
            }
        }
    }

    fun onNotificationDateTimePicked(localDateTime: LocalDateTime?) {
        _state.update {
            it.copy(notificationDateTime = localDateTime)
        }
    }

    fun onTagSearchTermChanged(term: String) {
        viewModelScope.launch {
            tagSearchTerm.emit(term)
        }
    }

    fun onTagClicked(tag: Tag) {
        toggleTagSelection(tag)
    }

    fun onPriorityChanged(priority: Priority) {
        updatePriority(priority)
    }

    fun onSubtaskAdded(subtask: Subtask) {
        _state.update { state ->
            val subtasks = state.subtasks + subtask

            state.copy(subtasks = subtasks)
        }
    }

    fun onSubtaskTitleUpdated(subtask: Subtask, title: String) {
        _state.update { state ->
            val subtasks = state.subtasks.toMutableList()
            val index = subtasks.indexOf(subtask)

            if (index == -1) {
                return@update state
            }

            val newItem = subtask.copy(title = title)
            subtasks[index] = newItem

            state.copy(subtasks = subtasks.toPersistentList())
        }
    }

    fun onSubtaskDoneChanged(subtask: Subtask, done: Boolean) {
        _state.update { state ->
            val subtasks = state.subtasks.toMutableList()
            val index = state.subtasks.indexOf(subtask)

            if (index == -1) {
                return@update state
            }

            val newItem = subtask.copy(done = done)
            subtasks[index] = newItem

            state.copy(subtasks = subtasks.toPersistentList())
        }
    }

    fun onSubtaskRemoveClicked(subtask: Subtask) {
        _state.update { state ->
            val subtasks = state.subtasks.filter {
                it != subtask
            }

            state.copy(subtasks = subtasks.toPersistentList())
        }
    }

    private fun toggleTagSelection(tag: Tag) {
        _state.update { state ->
            val selectedTags = state.selectedTags

            val newTags = if (selectedTags.contains(tag)) {
                selectedTags - tag
            } else {
                selectedTags + tag
            }

            state.copy(selectedTags = newTags.toPersistentList())
        }
    }

    private suspend fun sendPopBackStack() {
        _events.send(AddEntryEvent.PopBackStack)
    }

    private fun updateTitle(title: String) {
        _state.update {
            it.copy(title = title)
        }
    }

    private fun updateDescription(description: String) {
        _state.update {
            it.copy(description = description)
        }
    }

    private fun updatePriority(priority: Priority) {
        _state.update {
            it.copy(priority = priority)
        }
    }

    private fun AddEntryState.toTodoItem(): TodoItem {
        return TodoItem(
            id = id,
            title = title,
            description = description,
            done = existingItem?.done ?: false,
            tags = selectedTags,
            priority = priority,
            dueDate = dueDate,
            notificationDateTime = notificationDateTime,
            subtasks = subtasks,
            creationDateTime = existingItem?.creationDateTime ?: LocalDateTime.now()
        )
    }
}

data class AddEntryViewModelState(
    val title: String,
    val description: String,
    val dueDate: LocalDate?,
    val notificationDateTime: LocalDateTime?,
    val selectedTags: PersistentList<Tag>,
    val priority: Priority,
    val subtasks: PersistentList<Subtask>
)

@Immutable
data class AddEntryState(
    val title: String,
    val titleHasError: Boolean,
    val description: String,
    val dueDate: LocalDate?,
    val notificationDateTime: LocalDateTime?,
    val tagSearchTerm: String,
    val tags: PersistentList<Tag>,
    val selectedTags: PersistentList<Tag>,
    val priority: Priority,
    val subtasks: PersistentList<Subtask>
) {
    fun isValid(): Boolean {
        return this.title.isNotBlank()
    }
}

sealed interface AddEntryEvent {
    data object PopBackStack : AddEntryEvent
}