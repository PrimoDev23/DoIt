package com.example.doit.domain.usecases

import com.example.doit.TestBase
import com.example.doit.data.TodoItems
import com.example.doit.domain.repositories.TodoItemRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateDoneUseCaseTest : TestBase() {

    @Test
    operator fun invoke() = runTest {
        val repo = mockk<TodoItemRepository>()

        coEvery { repo.saveTodoItem(any()) } returns Unit

        val useCase = UpdateDoneUseCaseImpl(
            repo = repo
        )

        val item = TodoItems.todoItemOne

        useCase.invoke(item, true)

        val doneItem = item.copy(done = true)

        coVerify { repo.saveTodoItem(doneItem) }
    }
}