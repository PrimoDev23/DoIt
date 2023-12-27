package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.UpdateDoneUseCase

class UpdateDoneUseCaseImpl(
    private val repo: TodoItemRepository
) : UpdateDoneUseCase {
    override suspend fun invoke(item: TodoItem, done: Boolean) {
        val newItem = item.copy(done = done)
        repo.saveTodoItem(newItem)
    }
}