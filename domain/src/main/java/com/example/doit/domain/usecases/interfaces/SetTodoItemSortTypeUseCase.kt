package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItemSortType

interface SetTodoItemSortTypeUseCase {
    suspend operator fun invoke(sortType: TodoItemSortType)
}