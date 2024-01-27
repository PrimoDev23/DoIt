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
    fun getTagMappingsFlow() {
        val dao = mockk<TagMappingDao>()

        every { dao.selectAllFlow() } returns flow { }

        val repo = TagMappingRepositoryImpl(dao = dao)

        repo.getTagMappingsFlow()

        coVerify { dao.selectAllFlow() }
    }
}