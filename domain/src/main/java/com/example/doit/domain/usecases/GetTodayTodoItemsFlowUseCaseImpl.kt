package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.GetTodayTodoItemsFlowUseCase
import kotlinx.coroutines.flow.Flow

class GetTodayTodoItemsFlowUseCaseImpl(
    private val repo: TodoItemRepository
) : GetTodayTodoItemsFlowUseCase {
    override fun invoke(): Flow<List<TodoItem>> {
        return repo.getTodayItemsFlow()
    }
}