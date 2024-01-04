package com.example.doit.data.repositories

import com.example.doit.common.constants.DatabaseConstants
import com.example.doit.data.daos.TodoItemDao
import com.example.doit.data.models.TagMappingEntity
import com.example.doit.data.models.TagMappingWithTagEntity
import com.example.doit.data.models.TodoItemWithSubtasksEntity
import com.example.doit.data.models.toEntity
import com.example.doit.domain.repositories.TagRepository
import com.example.doit.testing.Tags
import com.example.doit.testing.TestBase
import com.example.doit.testing.TodoItems
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TodoItemRepositoryTest : TestBase() {

    @Test
    fun getItemsFlow() = runTest {
        val dao = mockk<TodoItemDao>()
        val tagRepo = mockk<TagRepository>()

        val items = TodoItems.todoList
        val entities = items.map { item ->
            val entity = item.toEntity()

            TodoItemWithSubtasksEntity(
                item = entity,
                subtasks = item.subtasks.map {
                    it.toEntity(item.id)
                },
                tags = item.tags.map {
                    TagMappingWithTagEntity(
                        mapping = TagMappingEntity(
                            itemId = item.id,
                            tagId = it.id
                        ),
                        tag = it.toEntity()
                    )
                }
            )
        }

        every { dao.selectFlow() } returns flow {
            emit(entities)
        }

        coEvery { tagRepo.getTagsByIds(any()) } answers {
            val ids = firstArg<List<Long>>()

            Tags.tagList.filter {
                ids.contains(it.id)
            }
        }

        val repo = TodoItemRepositoryImpl(
            dao = dao
        )

        val result = repo.getItemsFlow().first()

        verify { dao.selectFlow() }

        assertEquals(items, result)
    }

    @Test
    fun getTodayItemsFlow() = runTest {
        val dao = mockk<TodoItemDao>()
        val tagRepo = mockk<TagRepository>()

        val items = listOf(TodoItems.todoItemOne)
        val entities = items.map { item ->
            val entity = item.toEntity()

            TodoItemWithSubtasksEntity(
                item = entity,
                subtasks = item.subtasks.map {
                    it.toEntity(item.id)
                },
                tags = item.tags.map {
                    TagMappingWithTagEntity(
                        mapping = TagMappingEntity(
                            itemId = item.id,
                            tagId = it.id
                        ),
                        tag = it.toEntity()
                    )
                }
            )
        }

        val date = LocalDate.now().format(DatabaseConstants.DATE_FORMATTER)

        every { dao.selectByDate(date) } returns flow {
            emit(entities)
        }

        coEvery { tagRepo.getTagsByIds(any()) } answers {
            val ids = firstArg<List<Long>>()

            Tags.tagList.filter {
                ids.contains(it.id)
            }
        }

        val repo = TodoItemRepositoryImpl(
            dao = dao
        )

        val result = repo.getTodayItemsFlow().first()

        verify { dao.selectByDate(date) }

        assertEquals(items, result)
    }

    @Test
    fun getItemById() = runTest {
        val dao = mockk<TodoItemDao>()
        val tagRepo = mockk<TagRepository>()

        val item = TodoItems.todoItemOne
        val entity = item.toEntity()
        val fullEntity = TodoItemWithSubtasksEntity(
            item = entity,
            subtasks = item.subtasks.map {
                it.toEntity(item.id)
            },
            tags = item.tags.map {
                TagMappingWithTagEntity(
                    mapping = TagMappingEntity(
                        itemId = item.id,
                        tagId = it.id
                    ),
                    tag = it.toEntity()
                )
            }
        )

        coEvery { dao.selectById(item.id) } returns fullEntity

        coEvery { tagRepo.getTagsByIds(any()) } answers {
            val ids = firstArg<List<Long>>()

            Tags.tagList.filter {
                ids.contains(it.id)
            }
        }

        val repo = TodoItemRepositoryImpl(
            dao = dao
        )

        val result = repo.getItemById(item.id)

        coVerify { dao.selectById(item.id) }

        assertEquals(item, result)
    }

    @Test
    fun getItemFlowById() = runTest {
        val dao = mockk<TodoItemDao>()
        val tagRepo = mockk<TagRepository>()

        val item = TodoItems.todoItemOne
        val entity = item.toEntity()
        val fullEntity = TodoItemWithSubtasksEntity(
            item = entity,
            subtasks = item.subtasks.map {
                it.toEntity(item.id)
            },
            tags = item.tags.map {
                TagMappingWithTagEntity(
                    mapping = TagMappingEntity(
                        itemId = item.id,
                        tagId = it.id
                    ),
                    tag = it.toEntity()
                )
            }
        )

        coEvery { dao.selectByIdFlow(item.id) } returns flow {
            emit(fullEntity)
        }

        coEvery { tagRepo.getTagsByIds(any()) } answers {
            val ids = firstArg<List<Long>>()

            Tags.tagList.filter {
                ids.contains(it.id)
            }
        }

        val repo = TodoItemRepositoryImpl(
            dao = dao
        )

        val result = repo.getItemFlowById(item.id).first()

        coVerify { dao.selectByIdFlow(item.id) }

        assertEquals(item, result)
    }

    @Test
    fun saveTodoItem() = runTest {
        val dao = mockk<TodoItemDao>()

        val item = TodoItems.todoItemOne
        val entity = item.toEntity()

        coEvery { dao.insert(any()) } returns Unit

        val repo = TodoItemRepositoryImpl(
            dao = dao
        )

        repo.saveTodoItem(item)

        coVerify { dao.insert(entity) }
    }

    @Test
    fun saveTodoItems() = runTest {
        val dao = mockk<TodoItemDao>()

        val items = TodoItems.todoList
        val entities = items.map {
            it.toEntity()
        }

        coEvery { dao.insert(any()) } returns Unit

        val repo = TodoItemRepositoryImpl(
            dao = dao
        )

        repo.saveTodoItems(items)

        coVerify {
            entities.forEach {
                dao.insert(it)
            }
        }
    }

    @Test
    fun deleteTodoItems() = runTest {
        val dao = mockk<TodoItemDao>()

        val items = TodoItems.todoList
        val entities = items.map {
            it.toEntity()
        }

        coEvery { dao.delete(any()) } returns Unit

        val repo = TodoItemRepositoryImpl(
            dao = dao
        )

        repo.deleteTodoItems(items)

        coVerify {
            entities.forEach {
                dao.delete(it)
            }
        }
    }

    @Test
    fun deleteItemById() = runTest {
        val dao = mockk<TodoItemDao>()

        val item = TodoItems.todoItemOne

        coEvery { dao.deleteById(any()) } returns Unit

        val repo = TodoItemRepositoryImpl(
            dao = dao
        )

        repo.deleteItemById(item.id)

        coVerify { dao.deleteById(item.id) }
    }
}