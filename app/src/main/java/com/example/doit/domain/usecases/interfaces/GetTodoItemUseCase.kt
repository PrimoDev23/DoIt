package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItem

interface GetTodoItemUseCase {
    suspend operator fun invoke(id: Long): TodoItem?
}