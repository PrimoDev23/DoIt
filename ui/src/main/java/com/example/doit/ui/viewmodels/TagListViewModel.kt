package com.example.doit.ui.viewmodels

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Tag
import com.example.doit.domain.usecases.interfaces.DeleteTagsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagMappingsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.SaveTagUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.plus
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TagListViewModel(
    getTagsFlowUseCase: GetTagsFlowUseCase,
    getTagMappingsFlowUseCase: GetTagMappingsFlowUseCase,
    private val saveTagUseCase: SaveTagUseCase,
    private val deleteTagsUseCase: DeleteTagsUseCase
) : ViewModel() {

    private val tagFlow = getTagsFlowUseCase.getFlow()
    private val mappingFlow = getTagMappingsFlowUseCase()

    private val mappedTags = combine(tagFlow, mappingFlow) { tags, mappings ->
        tags.associateWith { tag -> mappings.count { it.tagId == tag.id } }
    }

    private val _state = MutableStateFlow(TagListViewModelState())
    val state = combine(_state, mappedTags) { state, mappedTags ->
        TagListState(
            items = mappedTags.toPersistentMap(),
            selectedTags = state.selectedTags
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TagListState(
                items = persistentMapOf(),
                selectedTags = persistentListOf()
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
            val oldList = it.selectedTags
            val newList = if (oldList.contains(tag)) {
                oldList - tag
            } else {
                oldList + tag
            }

            it.copy(selectedTags = newList)
        }
    }

    fun onClearSelectionClicked() {
        _state.update {
            it.copy(selectedTags = persistentListOf())
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            deleteTagsUseCase(state.value.selectedTags)

            _state.update {
                it.copy(selectedTags = persistentListOf())
            }
        }
    }

}

data class TagListViewModelState(
    val selectedTags: PersistentList<Tag> = persistentListOf()
)

@Immutable
data class TagListState(
    val items: PersistentMap<Tag, Int>,
    val selectedTags: PersistentList<Tag>
)