package com.example.doit.data.repositories

import com.example.doit.data.daos.TodoItemDao
import com.example.doit.data.mappers.TodoItemMapper
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TodoItemRepositoryImpl @Inject constructor(
    private val dao: TodoItemDao,
    private val mapper: TodoItemMapper
) : TodoItemRepository {

    override fun getItemsFlow(): Flow<List<TodoItem>> {
        return dao.select().map { items ->
            items.map {
                mapper.map(it)
            }
        }
    }

    override fun getTodayItemsFlow(): Flow<List<TodoItem>> {
        val today = LocalDate.now().format(TodoItemMapper.FORMATTER)

        return dao.selectByDate(today).map { items ->
            items.map {
                mapper.map(it)
            }
        }
    }

    override suspend fun getItemById(id: String): TodoItem? {
        val entity = dao.selectById(id) ?: return null

        return mapper.map(entity)
    }

    override suspend fun saveTodoItem(item: TodoItem) {
        val mappedItem = mapper.mapBack(item)

        dao.insert(mappedItem)
    }

    override suspend fun saveTodoItems(items: List<TodoItem>) {
        val mappedItems = items.map {
            mapper.mapBack(it)
        }

        mappedItems.forEach {
            dao.insert(it)
        }
    }

    override suspend fun deleteTodoItems(items: List<TodoItem>) {
        val mappedItems = items.map {
            mapper.mapBack(it)
        }

        mappedItems.forEach {
            dao.delete(it)
        }
    }

    override suspend fun getItemsWithTagIds(ids: List<Long>): List<TodoItem> {
        val entities = ids.flatMap {
            dao.selectContainsTagId(it)
        }.distinct()

        return entities.map {
            mapper.map(it)
        }
    }
}