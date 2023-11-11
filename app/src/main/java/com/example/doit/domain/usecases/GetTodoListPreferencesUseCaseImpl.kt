package com.example.doit.domain.usecases

import com.example.doit.data.models.local.TodoListPreferences
import com.example.doit.domain.repositories.PreferencesRepository
import com.example.doit.domain.usecases.interfaces.GetTodoListPreferencesUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodoListPreferencesUseCaseImpl @Inject constructor(
    private val repo: PreferencesRepository
) : GetTodoListPreferencesUseCase {
    override fun invoke(): Flow<TodoListPreferences> {
        return repo.getTodoListPreferencesFlow()
    }
}