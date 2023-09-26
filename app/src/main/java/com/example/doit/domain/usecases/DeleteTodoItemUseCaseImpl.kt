package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemUseCase
import javax.inject.Inject

class DeleteTodoItemUseCaseImpl @Inject constructor(
    private val todoItemRepository: TodoItemRepository
) : DeleteTodoItemUseCase {
    override suspend fun delete(item: TodoItem) {
        todoItemRepository.deleteTodoItem(item)
    }
}