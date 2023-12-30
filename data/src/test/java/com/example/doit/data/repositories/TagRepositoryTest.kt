package com.example.doit.data.repositories

import com.example.doit.data.daos.TagDao
import com.example.doit.data.models.TodoItemWithSubtasksEntity
import com.example.doit.data.models.getTagsForItem
import com.example.doit.data.models.toEntity
import com.example.doit.domain.models.Tag
import com.example.doit.testing.Tags
import com.example.doit.testing.TestBase
import com.example.doit.testing.TodoItems
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class TagRepositoryTest : TestBase() {

    @Test
    fun saveTag() = runTest {
        val dao = mockk<TagDao>()

        coEvery { dao.insert(any()) } returns Unit

        val repo = TagRepositoryImpl(
            dao = dao
        )

        val tag = Tags.tagOne

        repo.saveTag(tag)

        val entity = tag.toEntity()

        coVerify { dao.insert(entity) }
    }

    @Test
    fun getTagsFlow() = runTest {
        val dao = mockk<TagDao>()

        val tags = Tags.tagList
        val entities = tags.map {
            it.toEntity()
        }
        val tagFlow = flow {
            emit(entities)
        }

        coEvery { dao.selectFlow() } returns tagFlow

        val repo = TagRepositoryImpl(
            dao = dao
        )

        val result = repo.getTagsFlow()

        coVerify { dao.selectFlow() }

        assertEquals(tags, result.first())
    }

    @Test
    fun getTags() = runTest {
        val dao = mockk<TagDao>()

        val tags = Tags.tagList
        val entities = tags.map {
            it.toEntity()
        }

        coEvery { dao.select() } returns entities

        val repo = TagRepositoryImpl(
            dao = dao
        )

        val result = repo.getTags()

        coVerify { dao.select() }

        assertEquals(tags, result)
    }

    @Test
    fun getTagsByIds() = runTest {
        val dao = mockk<TagDao>()

        val tags = Tags.tagList
        val ids = tags.map { it.id }
        val entities = tags.map {
            it.toEntity()
        }

        coEvery { dao.selectByIds(ids) } returns entities

        val repo = TagRepositoryImpl(
            dao = dao
        )

        val result = repo.getTagsByIds(ids)

        coVerify { dao.selectByIds(ids) }

        assertEquals(tags, result)
    }

    @Test
    fun deleteTags() = runTest {
        val dao = mockk<TagDao>()

        val tags = Tags.tagList
        val entities = tags.map {
            it.toEntity()
        }

        coEvery { dao.delete(any()) } returns Unit

        val repo = TagRepositoryImpl(
            dao = dao
        )

        repo.deleteTags(tags)

        coVerify {
            entities.forEach {
                dao.delete(it)
            }
        }
    }

    @Test
    fun getTagsForItem() = runTest {
        val dao = mockk<TagDao>()

        val item = TodoItems.todoItemOne
        val tags = item.tags
        val tagIds = tags.map {
            it.id
        }
        val entities = tags.map {
            it.toEntity()
        }

        val fullItemEntityNoTags = TodoItemWithSubtasksEntity(
            item = item.copy(tags = emptyList()).toEntity(),
            subtasks = item.subtasks.map {
                it.toEntity(item.id)
            }
        )
        val fullItemEntity = TodoItemWithSubtasksEntity(
            item = item.toEntity(),
            subtasks = item.subtasks.map {
                it.toEntity(item.id)
            }
        )

        coEvery { dao.selectByIds(tagIds) } returns entities

        val repo = TagRepositoryImpl(
            dao = dao
        )

        val noTagResult = repo.getTagsForItem(fullItemEntityNoTags)

        assertEquals(emptyList<Tag>(), noTagResult)

        val tagResult = repo.getTagsForItem(fullItemEntity)

        coVerify { dao.selectByIds(tagIds) }

        assertEquals(tags, tagResult)
    }
}