package com.example.doit.domain.usecases.interfaces

import com.example.doit.data.models.local.TodoListPreferences
import kotlinx.coroutines.flow.Flow

interface GetTodoListPreferencesUseCase {
    operator fun invoke(): Flow<TodoListPreferences>
}