package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.GetTodoItemFlowUseCase
import kotlinx.coroutines.flow.Flow

class GetTodoItemFlowUseCaseImpl(
    private val repo: TodoItemRepository
) : GetTodoItemFlowUseCase {
    override fun invoke(id: String): Flow<TodoItem?> {
        return repo.getItemFlowById(id)
    }
}