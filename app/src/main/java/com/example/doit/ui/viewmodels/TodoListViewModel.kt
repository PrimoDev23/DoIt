package com.example.doit.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    getTodoItemsFlowUseCase: GetTodoItemsFlowUseCase
) : ViewModel() {

    private val todoItems = getTodoItemsFlowUseCase.getItemFlow()

}