package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItem
import kotlinx.coroutines.flow.Flow

interface GetTodayTodoItemsFlowUseCase {
    operator fun invoke(): Flow<List<TodoItem>>
}