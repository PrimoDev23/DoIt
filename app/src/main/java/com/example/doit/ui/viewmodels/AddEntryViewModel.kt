package com.example.doit.ui.viewmodels

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
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

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
                        priority = item.priority
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
            priority = priority
        )
    }

    fun AddEntryState.hasChanges(): Boolean {
        val existingItem = this@AddEntryViewModel.existingItem

        return if (existingItem != null) {
            val selectedTags = tags.filter {
                it.selected
            }

            title != existingItem.title ||
                    description != existingItem.description ||
                    selectedTags != existingItem.tags ||
                    priority != existingItem.priority
        } else {
            title.isNotBlank() ||
                    description.isNotBlank() ||
                    tags.any { it.selected } ||
                    priority != Priority.NONE
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
    fun isValid(): Boolean {
        return this.title.isNotBlank()
    }
}

sealed interface AddEntryEvent {
    data object ShowDismissDialog : AddEntryEvent
    data object PopBackStack : AddEntryEvent
}