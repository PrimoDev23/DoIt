package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItem

interface DeleteTodoItemsUseCase {
    suspend fun delete(items: List<TodoItem>)
}