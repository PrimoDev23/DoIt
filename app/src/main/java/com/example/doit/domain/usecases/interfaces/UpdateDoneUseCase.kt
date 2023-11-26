package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItem

interface UpdateDoneUseCase {
    suspend operator fun invoke(item: TodoItem, done: Boolean)
}