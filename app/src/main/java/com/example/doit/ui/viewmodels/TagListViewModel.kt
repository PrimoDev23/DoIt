package com.example.doit.ui.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Tag
import com.example.doit.domain.usecases.interfaces.DeleteTagsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.SaveTagUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagListViewModel @Inject constructor(
    getTagsFlowUseCase: GetTagsFlowUseCase,
    private val saveTagUseCase: SaveTagUseCase,
    private val deleteTagsUseCase: DeleteTagsUseCase
) : ViewModel() {

    private val tagFlow = getTagsFlowUseCase.getFlow()

    private val _state = MutableStateFlow(TagListViewModelState())
    val state = combine(_state, tagFlow) { state, tags ->
        TagListState(
            items = tags,
            selectedTags = state.selectedTags
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TagListState(
                items = emptyList(),
                selectedTags = emptyList()
            )
        )

    fun onTagSaved(
        title: String,
        color: Color
    ) {
        viewModelScope.launch {
            saveTagUseCase.save(
                title = title,
                color = color
            )
        }
    }

    fun onTagSelected(tag: Tag) {
        _state.update {
            val newList = it.selectedTags.toMutableList()

            if (newList.contains(tag)) {
                newList.remove(tag)
            } else {
                newList.add(tag)
            }

            it.copy(selectedTags = newList)
        }
    }

    fun onClearSelectionClicked() {
        _state.update {
            it.copy(selectedTags = emptyList())
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            deleteTagsUseCase(state.value.selectedTags)

            _state.update {
                it.copy(selectedTags = emptyList())
            }
        }
    }

}

data class TagListViewModelState(
    val selectedTags: List<Tag> = emptyList()
)

data class TagListState(
    val items: List<Tag>,
    val selectedTags: List<Tag>
)