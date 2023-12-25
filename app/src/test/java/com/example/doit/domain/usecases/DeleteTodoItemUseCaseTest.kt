package com.example.doit.domain.usecases

import com.example.doit.TestBase
import com.example.doit.data.TodoItems
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.utils.interfaces.WorkScheduler
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteTodoItemUseCaseTest : TestBase() {

    @Test
    fun invoke() = runTest {
        val workScheduler = mockk<WorkScheduler>()
        val todoItemRepository = mockk<TodoItemRepository>()

        every { workScheduler.cancelById(any()) } returns Unit
        coEvery { todoItemRepository.deleteItemById(any()) } returns Unit

        val useCase = DeleteTodoItemUseCaseImpl(
            workScheduler = workScheduler,
            todoItemRepository = todoItemRepository
        )

        val items = listOf(
            TodoItems.todoItemOne,
            TodoItems.todoItemThree
        )

        useCase.delete(items)

        coVerifyOrder {
            items.forEach { item ->
                val id = item.id

                todoItemRepository.deleteItemById(id)
                workScheduler.cancelById(id)
            }
        }
    }

}