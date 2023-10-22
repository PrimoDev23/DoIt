package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItem
import kotlinx.coroutines.flow.Flow

interface GetTodoItemsFlowUseCase {
    fun getItemFlow(parent: String?): Flow<List<TodoItem>>
}