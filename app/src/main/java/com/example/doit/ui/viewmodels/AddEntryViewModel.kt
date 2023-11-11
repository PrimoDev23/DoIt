package com.example.doit.ui.viewmodels

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Subtask
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import com.example.doit.ui.composables.screens.navArgs
import com.example.doit.ui.navigation.arguments.AddEntryNavArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class AddEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveTodoItemUseCase: SaveTodoItemUseCase,
    private val getTagsUseCase: GetTagsUseCase,
    private val getTodoItemUseCase: GetTodoItemUseCase,
    private val deleteTodoItemsUseCase: DeleteTodoItemsUseCase
) : ViewModel() {

    private val navArgs: AddEntryNavArgs = savedStateHandle.navArgs()

    private val id: String = navArgs.id
    private var existingItem: TodoItem? = null

    private val _events = Channel<AddEntryEvent>()
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(
        AddEntryState(
            title = "",
            description = "",
            dueDate = null,
            notificationDateTime = null,
            tags = emptyList(),
            priority = Priority.NONE,
            subtasks = emptyList()
        )
    )
    val state = _state.asStateFlow()

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

            launch {
                loadTags()
            }
        }
    }

    private suspend fun initData() {
        getTodoItemUseCase(id)?.let { item ->
            existingItem = item

            val millis = item.dueDate?.let {
                Instant.from(it.atStartOfDay()).toEpochMilli()
            } ?: Instant.now().toEpochMilli()

            datePickerState.setSelection(millis)

            _state.update { state ->
                val newTags = state.tags.map { tag ->
                    val selected = item.tags.any { it.id == tag.id }

                    if (selected) {
                        tag.copy(selected = true)
                    } else {
                        tag
                    }
                }

                state.copy(
                    title = item.title,
                    description = item.description,
                    tags = newTags,
                    priority = item.priority,
                    dueDate = item.dueDate,
                    notificationDateTime = item.notificationDateTime,
                    subtasks = item.subtasks
                )
            }
        }
    }

    private suspend fun loadTags() {
        val tags = getTagsUseCase()

        updateTags(tags)
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

            state.copy(subtasks = subtasks)
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

            state.copy(
                subtasks = subtasks
            )
        }
    }

    fun onSubtaskRemoveClicked(subtask: Subtask) {
        _state.update { state ->
            val subtasks = state.subtasks.filter {
                it != subtask
            }

            state.copy(subtasks = subtasks)
        }
    }

    private fun toggleTagSelection(tag: Tag) {
        _state.update {
            val tags = it.tags.toMutableList()

            val index = tags.indexOf(tag)

            if (index == -1) {
                return@update it
            }

            val newTag = tag.copy(selected = !tag.selected)
            tags[index] = newTag

            it.copy(tags = tags)
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

    private fun updateTags(tags: List<Tag>) {
        _state.update {
            it.copy(tags = tags)
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
            tags = tags.filter { it.selected },
            priority = priority,
            dueDate = dueDate,
            notificationDateTime = notificationDateTime,
            subtasks = subtasks,
            creationDateTime = existingItem?.creationDateTime ?: LocalDateTime.now()
        )
    }
}

@Immutable
data class AddEntryState(
    val title: String,
    val description: String,
    val dueDate: LocalDate?,
    val notificationDateTime: LocalDateTime?,
    val tags: List<Tag>,
    val priority: Priority,
    val subtasks: List<Subtask>
) {
    fun isValid(): Boolean {
        return this.title.isNotBlank()
    }
}

sealed interface AddEntryEvent {
    data object PopBackStack : AddEntryEvent
}