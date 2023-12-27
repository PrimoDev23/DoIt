package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItem

interface SaveTodoItemUseCase {
    suspend fun save(item: TodoItem)
}