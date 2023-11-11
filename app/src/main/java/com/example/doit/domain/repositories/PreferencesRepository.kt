package com.example.doit.domain.repositories

import com.example.doit.data.models.local.TodoListPreferences
import com.example.doit.domain.models.TodoItemSortType
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getTodoListPreferencesFlow(): Flow<TodoListPreferences>

    suspend fun setTodoItemSortType(sortType: TodoItemSortType)
    suspend fun setHideDoneItems(hide: Boolean)
}