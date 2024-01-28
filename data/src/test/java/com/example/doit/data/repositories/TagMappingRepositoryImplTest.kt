package com.example.doit.data.repositories

import com.example.doit.data.daos.TagMappingDao
import com.example.doit.data.models.TagMappingEntity
import com.example.doit.testing.Tags
import com.example.doit.testing.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class TagMappingRepositoryImplTest : TestBase() {

    @Test
    fun saveTagMappings() = runTest {
        val dao = mockk<TagMappingDao>()

        coEvery { dao.insertTagMapping(any()) } returns Unit
        coEvery { dao.deleteAndInsertTagMappings(any()) } returns Unit
        coEvery { dao.deleteTagMappingByItemId(any()) } returns Unit

        val repo = TagMappingRepositoryImpl(dao = dao)

        val parent = "ID"

        val mappingsEntity = TagMappingEntity(
            itemId = parent,
            tagId = 0L
        )

        repo.saveTagMappings(parent, listOf(Tags.tagOne))

        coVerify { dao.deleteAndInsertTagMappings(listOf(mappingsEntity)) }
    }

    @Test
    fun getTagMappingsFlow() = runTest {
        val dao = mockk<TagMappingDao>()

        val tagMappingEntity = TagMappingEntity(
            itemId = "Test",
            tagId = 0
        )

        every { dao.selectAllFlow() } returns flow {
            emit(listOf(tagMappingEntity))
        }

        val repo = TagMappingRepositoryImpl(dao = dao)

        val tagMapping = tagMappingEntity.toDomainModel()

        repo.getTagMappingsFlow().collect {
            Assert.assertEquals(listOf(tagMapping), it)
        }

        coVerify { dao.selectAllFlow() }
    }
}