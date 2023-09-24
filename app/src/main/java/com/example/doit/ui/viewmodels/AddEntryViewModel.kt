package com.example.doit.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel @Inject constructor(
    private val saveTodoItemUseCase: SaveTodoItemUseCase
) : ViewModel() {

    private val _events = Channel<AddEntryEvent>()
    val events = _events.receiveAsFlow()

    private val _state = MutableStateFlow(AddEntryState())
    val state = _state.asStateFlow()

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

}

data class AddEntryState(
    val title: String = ""
) {
    fun isDefault(): Boolean {
        return this == AddEntryState()
    }

    fun toTodoItem(): TodoItem {
        return TodoItem(
            id = 0,
            title = title
        )
    }
}

sealed interface AddEntryEvent {
    data object ShowDismissDialog : AddEntryEvent
    data object PopBackStack : AddEntryEvent
}