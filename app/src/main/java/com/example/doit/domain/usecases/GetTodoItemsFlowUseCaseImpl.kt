package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodoItemsFlowUseCaseImpl @Inject constructor(
    private val repo: TodoItemRepository
) : GetTodoItemsFlowUseCase {
    override fun getItemFlow(parent: String?): Flow<List<TodoItem>> {
        return repo.getItemsFlow(parent)
    }
}