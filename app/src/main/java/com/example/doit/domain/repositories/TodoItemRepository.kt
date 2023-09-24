package com.example.doit.domain.repositories

import com.example.doit.domain.models.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoItemRepository {
    fun getItemsFlow(): Result<Flow<List<TodoItem>>>
}