package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.utils.interfaces.WorkScheduler

class DeleteTodoItemUseCaseImpl(
    private val workScheduler: WorkScheduler,
    private val todoItemRepository: TodoItemRepository
) : DeleteTodoItemsUseCase {
    override suspend fun delete(items: List<TodoItem>) {
        items.forEach {
            todoItemRepository.deleteItemById(it.id)
            workScheduler.cancelById(it.id)
        }
    }
}