package com.example.doit.domain.repositories

import com.example.doit.domain.models.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoItemRepository {
    suspend fun getItems(parent: String?): List<TodoItem>
    fun getItemsFlow(parent: String?): Flow<List<TodoItem>>
    fun getTodayItemsFlow(): Flow<List<TodoItem>>

    suspend fun getItemById(id: String): TodoItem?
    suspend fun saveTodoItem(item: TodoItem)
    suspend fun saveTodoItems(items: List<TodoItem>)
    suspend fun deleteTodoItems(items: List<TodoItem>)
    suspend fun deleteItemsByParent(parent: String)
    suspend fun getItemsWithTagIds(ids: List<Long>): List<TodoItem>
}