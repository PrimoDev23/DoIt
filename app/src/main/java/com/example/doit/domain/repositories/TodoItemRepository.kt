package com.example.doit.domain.repositories

import com.example.doit.domain.models.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoItemRepository {
    fun getItemsFlow(): Flow<List<TodoItem>>
    suspend fun saveTodoItem(item: TodoItem)
    suspend fun saveTodoItems(items: List<TodoItem>)
    suspend fun deleteTodoItems(items: List<TodoItem>)
    suspend fun getItemsWithTagIds(ids: List<Long>): List<TodoItem>
}