package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.SubtaskRepository
import com.example.doit.domain.repositories.TagMappingRepository
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.utils.interfaces.WorkScheduler
import com.example.doit.domain.worker.NotificationWorker
import com.example.doit.testing.TestBase
import com.example.doit.testing.TodoItems
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveTodoItemUseCaseTest : TestBase() {

    @Test
    fun `save - notification in future`() = runTest {
        val workScheduler = mockk<WorkScheduler>()
        val todoItemRepo = mockk<TodoItemRepository>()
        val subtaskRepo = mockk<SubtaskRepository>()
        val tagMappingRepository = mockk<TagMappingRepository>()

        coEvery { todoItemRepo.saveTodoItem(any()) } returns Unit
        coEvery { subtaskRepo.saveSubtasksForParent(any(), any()) } returns Unit
        coEvery { tagMappingRepository.saveTagMappings(any(), any()) } returns Unit

        every { workScheduler.cancelById(any()) } returns Unit
        every { workScheduler.enqueueNotification(any(), any(), any()) } returns Unit

        val useCase = SaveTodoItemUseCaseImpl(
            workScheduler = workScheduler,
            todoItemRepository = todoItemRepo,
            subtaskRepository = subtaskRepo,
            tagMappingRepository = tagMappingRepository
        )

        val item = TodoItems.todoItemOne

        useCase.save(item)

        coVerify { todoItemRepo.saveTodoItem(item) }
        coVerify { subtaskRepo.saveSubtasksForParent(item.id, item.subtasks) }
        coVerify { tagMappingRepository.saveTagMappings(item.id, item.tags) }

        verify { workScheduler.cancelById(item.id) }
        verify {
            workScheduler.enqueueNotification(
                item.id,
                item.notificationDateTime!!,
                mapOf(NotificationWorker.ITEM_ID_KEY to item.id)
            )
        }
    }

    @Test
    fun `save - notification in past`() = runTest {
        val workScheduler = mockk<WorkScheduler>()
        val todoItemRepo = mockk<TodoItemRepository>()
        val subtaskRepo = mockk<SubtaskRepository>()
        val tagMappingRepository = mockk<TagMappingRepository>()

        coEvery { todoItemRepo.saveTodoItem(any()) } returns Unit
        coEvery { subtaskRepo.saveSubtasksForParent(any(), any()) } returns Unit
        coEvery { tagMappingRepository.saveTagMappings(any(), any()) } returns Unit

        every { workScheduler.cancelById(any()) } returns Unit
        every { workScheduler.enqueueNotification(any(), any(), any()) } returns Unit

        val useCase = SaveTodoItemUseCaseImpl(
            workScheduler = workScheduler,
            todoItemRepository = todoItemRepo,
            subtaskRepository = subtaskRepo,
            tagMappingRepository = tagMappingRepository
        )

        val item = TodoItems.todoItemThree

        useCase.save(item)

        coVerify { todoItemRepo.saveTodoItem(item) }
        coVerify { subtaskRepo.saveSubtasksForParent(item.id, item.subtasks) }
        coVerify { tagMappingRepository.saveTagMappings(item.id, item.tags) }

        verify { workScheduler.cancelById(item.id) }
    }

    @Test
    fun `save - no notification`() = runTest {
        val workScheduler = mockk<WorkScheduler>()
        val todoItemRepo = mockk<TodoItemRepository>()
        val subtaskRepo = mockk<SubtaskRepository>()
        val tagMappingRepository = mockk<TagMappingRepository>()

        coEvery { todoItemRepo.saveTodoItem(any()) } returns Unit
        coEvery { subtaskRepo.saveSubtasksForParent(any(), any()) } returns Unit
        coEvery { tagMappingRepository.saveTagMappings(any(), any()) } returns Unit

        every { workScheduler.cancelById(any()) } returns Unit
        every { workScheduler.enqueueNotification(any(), any(), any()) } returns Unit

        val useCase = SaveTodoItemUseCaseImpl(
            workScheduler = workScheduler,
            todoItemRepository = todoItemRepo,
            subtaskRepository = subtaskRepo,
            tagMappingRepository = tagMappingRepository
        )

        val item = TodoItems.todoItemFour

        useCase.save(item)

        coVerify { todoItemRepo.saveTodoItem(item) }
        coVerify { subtaskRepo.saveSubtasksForParent(item.id, item.subtasks) }
        coVerify { tagMappingRepository.saveTagMappings(item.id, item.tags) }

        verify { workScheduler.cancelById(item.id) }
    }

}