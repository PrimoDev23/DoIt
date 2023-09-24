package com.example.doit.data.repositories

import com.example.doit.data.daos.TodoItemDao
import com.example.doit.data.mappers.TodoItemEntityMapper
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoItemRepositoryImpl @Inject constructor(
    private val todoItemDao: TodoItemDao,
    private val mapper: TodoItemEntityMapper
) : TodoItemRepository {

    override fun getItemsFlow(): Result<Flow<List<TodoItem>>> {
        return Result.success(
            todoItemDao.select().map { items ->
                items.map {
                    mapper.map(it)
                }
            }
        )
    }
}