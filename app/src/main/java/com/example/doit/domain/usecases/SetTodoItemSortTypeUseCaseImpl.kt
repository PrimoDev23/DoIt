package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.repositories.PreferencesRepository
import com.example.doit.domain.usecases.interfaces.SetTodoItemSortTypeUseCase

class SetTodoItemSortTypeUseCaseImpl(
    private val repo: PreferencesRepository
) : SetTodoItemSortTypeUseCase {
    override suspend fun invoke(sortType: TodoItemSortType) {
        repo.setTodoItemSortType(sortType)
    }
}