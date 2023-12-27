package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.preferences.TodoListPrefs
import com.example.doit.domain.usecases.interfaces.SetTodoItemSortTypeUseCase

class SetTodoItemSortTypeUseCaseImpl(
    private val repo: TodoListPrefs
) : SetTodoItemSortTypeUseCase {
    override suspend fun invoke(sortType: TodoItemSortType) {
        repo.setSortType(sortType)
    }
}