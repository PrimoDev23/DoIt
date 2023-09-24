package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import javax.inject.Inject

class SaveTodoItemUseCaseImpl @Inject constructor(
    private val todoItemRepository: TodoItemRepository
) : SaveTodoItemUseCase {

    override suspend fun save(item: TodoItem) {
        todoItemRepository.saveTodoItem(item)
    }

}