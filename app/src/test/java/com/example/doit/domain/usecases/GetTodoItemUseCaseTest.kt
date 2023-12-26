package com.example.doit.domain.usecases

import com.example.doit.TestBase
import com.example.doit.data.TodoItems
import com.example.doit.domain.repositories.TodoItemRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GetTodoItemUseCaseTest : TestBase() {

    @Test
    operator fun invoke() = runTest {
        val repo = mockk<TodoItemRepository>()

        val item = TodoItems.todoItemOne

        coEvery { repo.getItemById(item.id) } returns item

        val useCase = GetTodoItemUseCaseImpl(
            todoItemRepository = repo
        )

        val result = useCase.invoke(item.id)

        coVerify { repo.getItemById(item.id) }

        Assert.assertEquals(item, result)
    }
}