package com.example.doit.domain.preferences

import com.example.doit.data.models.local.TodoListPreferences
import com.example.doit.domain.models.TodoItemSortType
import kotlinx.coroutines.flow.Flow

interface TodoListPrefs {
    fun getFlow(): Flow<TodoListPreferences>

    suspend fun setSortType(sortType: TodoItemSortType)
    suspend fun setHideDoneItems(hide: Boolean)
}