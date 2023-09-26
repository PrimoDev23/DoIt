package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItem

interface DeleteTodoItemUseCase {
    suspend fun delete(item: TodoItem)
}