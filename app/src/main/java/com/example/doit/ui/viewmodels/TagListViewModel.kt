package com.example.doit.ui.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doit.domain.usecases.interfaces.SaveTagUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagListViewModel @Inject constructor(
    private val saveTagUseCase: SaveTagUseCase
) : ViewModel() {

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