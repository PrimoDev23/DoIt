package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItem
import kotlinx.coroutines.flow.Flow

interface GetTodoItemFlowUseCase {
    operator fun invoke(id: String): Flow<TodoItem?>
}