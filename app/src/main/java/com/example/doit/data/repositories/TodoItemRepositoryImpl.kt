package com.example.doit.data.repositories

import com.example.doit.data.daos.SubtaskDao
import com.example.doit.data.daos.TodoItemDao
import com.example.doit.data.mappers.TodoItemMapper
import com.example.doit.data.mappers.TodoItemWithSubtasksMapper
import com.example.doit.data.models.local.TodoItemWithSubtasksEntity
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TodoItemRepositoryImpl(
    private val dao: TodoItemDao,
    private val mapper: TodoItemMapper,
    private val subtaskDao: SubtaskDao,
    private val fullMapper: TodoItemWithSubtasksMapper
) : TodoItemRepository {

    override fun getItemsFlow(): Flow<List<TodoItem>> {
        return dao.selectFlow().map { items ->
            items.map {
                mapper.map(it)
            }
        }
    }

    override fun getTodayItemsFlow(): Flow<List<TodoItem>> {
        val today = LocalDate.now().format(TodoItemMapper.DATE_FORMATTER)

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

    override fun getItemFlowById(id: String): Flow<TodoItem?> {
        val itemFlow = dao.selectByIdFlow(id)
        val subtasksFlow = subtaskDao.selectByParentFlow(id)

        return combine(itemFlow, subtasksFlow) { item, subtasks ->
            if (item == null) {
                return@combine null
            }

            val combined = TodoItemWithSubtasksEntity(
                item = item,
                subtasks = subtasks
            )

            fullMapper.map(combined)
        }
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

    override suspend fun deleteItemById(id: String) {
        dao.deleteById(id)
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