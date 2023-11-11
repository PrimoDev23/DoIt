package com.example.doit.domain.repositories

import com.example.doit.domain.models.TodoItemSortType
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getTodoItemSortTypeFlow(): Flow<TodoItemSortType>
    suspend fun setTodoItemSortType(sortType: TodoItemSortType)

}