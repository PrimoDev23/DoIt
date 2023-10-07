package com.example.doit.ui.viewmodels

import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.GetTagsUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import com.example.doit.ui.composables.navArgs
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
import java.time.ZoneId
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class AddEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val saveTodoItemUseCase: SaveTodoItemUseCase,
    private val getTagsUseCase: GetTagsUseCase,
    private val getTodoItemUseCase: GetTodoItemUseCase
) : ViewModel() {

    private val navArgs: AddEntryNavArgs = savedStateHandle.navArgs()

    private val id: Long = navArgs.id
    private var existingItem: TodoItem? = null

    private val _events = Channel<AddEntryEvent>()
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(AddEntryState())
    val state = _state.asStateFlow()

    val datePickerState = DatePickerState(
        initialSelectedDateMillis = null,
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
        if (id != 0L) {
            getTodoItemUseCase(id)?.let { item ->
                existingItem = item

                val millis =
                    item.dueDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
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
                        dueDate = item.dueDate
                    )
                }
            }
        }
    }

    private suspend fun loadTags() {
        val tags = getTagsUseCase()

        updateTags(tags)
    }

    fun onBackClicked() {
        viewModelScope.launch {
            if (state.value.hasChanges()) {
                sendShowDismissDialog()
            } else {
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
        datePickerState.setSelection(null)

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

    fun onTagClicked(tag: Tag) {
        toggleTagSelection(tag)
    }

    fun onPriorityChanged(priority: Priority) {
        updatePriority(priority)
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

    fun onSaveClicked() {
        viewModelScope.launch {
            val todoItem = state.value.toTodoItem()

            saveTodoItemUseCase.save(todoItem)
            sendPopBackStack()
        }
    }

    private suspend fun sendShowDismissDialog() {
        _events.send(AddEntryEvent.ShowDismissDialog)
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
            dueDate = dueDate
        )
    }

    private fun AddEntryState.hasChanges(): Boolean {
        val existingItem = this@AddEntryViewModel.existingItem

        return if (existingItem != null) {
            val selectedTags = tags.filter {
                it.selected
            }

            title != existingItem.title ||
                    description != existingItem.description ||
                    selectedTags != existingItem.tags ||
                    priority != existingItem.priority ||
                    dueDate != existingItem.dueDate
        } else {
            title.isNotBlank() ||
                    description.isNotBlank() ||
                    tags.any { it.selected } ||
                    priority != Priority.NONE ||
                    dueDate != null
        }
    }
}

@Immutable
data class AddEntryState(
    val title: String = "",
    val description: String = "",
    val dueDate: LocalDate? = null,
    val tags: List<Tag> = emptyList(),
    val priority: Priority = Priority.NONE
) {
    fun isValid(): Boolean {
        return this.title.isNotBlank()
    }
}

sealed interface AddEntryEvent {
    data object ShowDismissDialog : AddEntryEvent
    data object PopBackStack : AddEntryEvent
}