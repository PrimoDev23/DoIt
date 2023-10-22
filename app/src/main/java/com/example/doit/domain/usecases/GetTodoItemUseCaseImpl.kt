package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.GetTodoItemUseCase
import javax.inject.Inject

class GetTodoItemUseCaseImpl @Inject constructor(
    private val todoItemRepository: TodoItemRepository
) : GetTodoItemUseCase {
    override suspend operator fun invoke(id: String): TodoItem? {
        return todoItemRepository.getItemById(id)
    }
}