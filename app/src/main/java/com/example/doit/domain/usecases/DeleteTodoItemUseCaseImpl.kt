package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.DeleteItemsByParentUseCase
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import javax.inject.Inject

class DeleteTodoItemUseCaseImpl @Inject constructor(
    private val todoItemRepository: TodoItemRepository,
    private val deleteItemsByParentUseCase: DeleteItemsByParentUseCase
) : DeleteTodoItemsUseCase {
    override suspend fun delete(items: List<TodoItem>) {
        todoItemRepository.deleteTodoItems(items)

        items.forEach {
            deleteItemsByParentUseCase(it.id)
        }
    }
}