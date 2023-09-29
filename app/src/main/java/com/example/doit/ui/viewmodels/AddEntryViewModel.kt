package com.example.doit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.GetTagsUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel @Inject constructor(
    private val saveTodoItemUseCase: SaveTodoItemUseCase,
    private val getTagsUseCase: GetTagsUseCase
) : ViewModel() {

    private val _events = Channel<AddEntryEvent>()
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(AddEntryState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadTags()
        }
    }

    private suspend fun loadTags() {
        val tags = getTagsUseCase()

        updateTags(tags)
    }

    fun onBackClicked() {
        viewModelScope.launch {
            if (!state.value.isDefault()) {
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

}

@Immutable
data class AddEntryState(
    val title: String = "",
    val description: String = "",
    val tags: List<Tag> = emptyList(),
    val priority: Priority = Priority.NONE
) {
    fun isDefault(): Boolean {
        return title.isBlank() &&
                description.isBlank() &&
                tags.none { it.selected } &&
                priority == Priority.NONE
    }

    fun toTodoItem(): TodoItem {
        return TodoItem(
            id = 0,
            title = title,
            description = description,
            done = false,
            tags = tags.filter { it.selected },
            priority = priority
        )
    }

    fun isValid(): Boolean {
        return this.title.isNotBlank()
    }
}

sealed interface AddEntryEvent {
    data object ShowDismissDialog : AddEntryEvent
    data object PopBackStack : AddEntryEvent
}