package com.example.doit.data.repositories

import com.example.doit.common.constants.DatabaseConstants
import com.example.doit.data.daos.TodoItemDao
import com.example.doit.data.models.getTagsForItem
import com.example.doit.data.models.toEntity
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TagRepository
import com.example.doit.domain.repositories.TodoItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TodoItemRepositoryImpl(
    private val dao: TodoItemDao,
    private val tagRepository: TagRepository
) : TodoItemRepository {

    override fun getItemsFlow(): Flow<List<TodoItem>> {
        return dao.selectFlow().map { items ->
            items.map {
                val tags = tagRepository.getTagsForItem(it)
                it.toDomainModel(tags)
            }
        }
    }

    override fun getTodayItemsFlow(): Flow<List<TodoItem>> {
        val today = LocalDate.now().format(DatabaseConstants.DATE_FORMATTER)

        return dao.selectByDate(today).map { items ->
            items.map {
                val tags = tagRepository.getTagsForItem(it)
                it.toDomainModel(tags)
            }
        }
    }

    override suspend fun getItemById(id: String): TodoItem? {
        val entity = dao.selectById(id) ?: return null

        val tags = tagRepository.getTagsForItem(entity)
        return entity.toDomainModel(tags)
    }

    override fun getItemFlowById(id: String): Flow<TodoItem?> {
        return dao.selectByIdFlow(id).map { entity ->
            entity?.let {
                val tags = tagRepository.getTagsForItem(it)
                it.toDomainModel(tags)
            }
        }
    }

    override suspend fun saveTodoItem(item: TodoItem) {
        val mappedItem = item.toEntity()

        dao.insert(mappedItem)
    }

    override suspend fun saveTodoItems(items: List<TodoItem>) {
        val mappedItems = items.map {
            it.toEntity()
        }

        mappedItems.forEach {
            dao.insert(it)
        }
    }

    override suspend fun deleteTodoItems(items: List<TodoItem>) {
        val mappedItems = items.map {
            it.toEntity()
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
            val tags = tagRepository.getTagsForItem(it)
            it.toDomainModel(tags)
        }
    }
}