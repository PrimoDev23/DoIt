package com.example.doit.ui.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.models.Tag
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.SaveTagUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagListViewModel @Inject constructor(
    getTagsFlowUseCase: GetTagsFlowUseCase,
    private val saveTagUseCase: SaveTagUseCase
) : ViewModel() {

    private val tagFlow = getTagsFlowUseCase.getFlow()
    val state = tagFlow
        .map {
            TagListState(
                items = it
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = TagListState()
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

}

data class TagListState(
    val items: List<Tag> = emptyList()
)