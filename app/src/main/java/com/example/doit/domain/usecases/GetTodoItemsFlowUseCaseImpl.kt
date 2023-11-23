package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import kotlinx.coroutines.flow.Flow

class GetTodoItemsFlowUseCaseImpl(
    private val repo: TodoItemRepository
) : GetTodoItemsFlowUseCase {
    override fun getItemFlow(): Flow<List<TodoItem>> {
        return repo.getItemsFlow()
    }
}