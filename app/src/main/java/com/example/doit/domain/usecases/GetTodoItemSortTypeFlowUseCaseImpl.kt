package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.repositories.PreferencesRepository
import com.example.doit.domain.usecases.interfaces.GetTodoItemSortTypeFlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodoItemSortTypeFlowUseCaseImpl @Inject constructor(
    private val repo: PreferencesRepository
) : GetTodoItemSortTypeFlowUseCase {
    override fun invoke(): Flow<TodoItemSortType> {
        return repo.getTodoItemSortTypeFlow()
    }
}