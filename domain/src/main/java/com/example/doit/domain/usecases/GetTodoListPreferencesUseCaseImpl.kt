package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoListPreferences
import com.example.doit.domain.preferences.TodoListPrefs
import com.example.doit.domain.usecases.interfaces.GetTodoListPreferencesUseCase
import kotlinx.coroutines.flow.Flow

class GetTodoListPreferencesUseCaseImpl(
    private val repo: TodoListPrefs
) : GetTodoListPreferencesUseCase {
    override fun invoke(): Flow<TodoListPreferences> {
        return repo.getFlow()
    }
}