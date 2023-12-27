package com.example.doit.data.repositories

import com.example.doit.TestBase
import com.example.doit.data.SubtaskEntity
import com.example.doit.data.Subtasks
import com.example.doit.data.daos.SubtaskDao
import com.example.doit.data.toEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SubtaskRepositoryTest : TestBase() {

    @Test
    fun getSubtasksByParent() = runTest {
        val dao = mockk<SubtaskDao>()

        val parent = "12345"
        val subtasks = Subtasks.subtasks
        val entities = subtasks.map {
            it.toEntity(parent)
        }

        coEvery { dao.selectByParent(parent) } returns entities

        val repo = SubtaskRepositoryImpl(
            subtaskDao = dao
        )

        val result = repo.getSubtasksByParent(parent)

        coVerify { dao.selectByParent(parent) }

        assertEquals(subtasks, result)
    }

    @Test
    fun saveSubtasksForParent() = runTest {
        val dao = mockk<SubtaskDao>()

        val parent = "12345"

        coEvery { dao.insertOnSave(parent, any()) } returns Unit

        val repo = SubtaskRepositoryImpl(
            subtaskDao = dao
        )

        val subtasks = Subtasks.subtasks

        repo.saveSubtasksForParent(parent, subtasks)

        val entities = subtasks.map {
            it.toEntity(parent)
        }

        coVerify { dao.insertOnSave(parent, entities) }
    }

    @Test
    fun saveSubtaskForParent() = runTest {
        val dao = mockk<SubtaskDao>()

        val parent = "12345"

        coEvery { dao.insert(any<com.example.doit.data.SubtaskEntity>()) } returns Unit

        val repo = SubtaskRepositoryImpl(
            subtaskDao = dao
        )

        val subtask = Subtasks.subtaskOne

        repo.saveSubtaskForParent(parent, subtask)

        val entity = subtask.toEntity(parent)

        coVerify { dao.insert(entity) }
    }

    @Test
    fun deleteByParent() = runTest {
        val dao = mockk<SubtaskDao>()

        val parent = "12345"

        coEvery { dao.deleteByParent(any()) } returns Unit

        val repo = SubtaskRepositoryImpl(
            subtaskDao = dao
        )

        repo.deleteByParent(parent)

        coVerify { dao.deleteByParent(parent) }
    }
}