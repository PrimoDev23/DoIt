package com.example.doit.data.repositories

import com.example.doit.data.daos.TodoItemDao
import com.example.doit.data.mappers.TodoItemEntityMapper
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoItemRepositoryImpl @Inject constructor(
    private val dao: TodoItemDao,
    private val mapper: TodoItemEntityMapper
) : TodoItemRepository {

    override fun getItemsFlow(): Flow<List<TodoItem>> {
        return dao.select().map { items ->
            items.map {
                mapper.map(it)
            }
        }
    }

    override suspend fun saveTodoItem(item: TodoItem) {
        val mappedItem = mapper.mapBack(item)

        dao.insert(mappedItem)
    }

    override suspend fun deleteTodoItem(item: TodoItem) {
        val mappedItem = mapper.mapBack(item)

        dao.delete(mappedItem)
    }
}