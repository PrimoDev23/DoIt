package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItemSortType
import kotlinx.coroutines.flow.Flow

interface GetTodoItemSortTypeFlowUseCase {
    operator fun invoke(): Flow<TodoItemSortType>
}