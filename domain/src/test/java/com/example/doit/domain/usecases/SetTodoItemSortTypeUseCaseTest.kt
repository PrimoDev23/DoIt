package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.preferences.TodoListPrefs
import com.example.doit.testing.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetTodoItemSortTypeUseCaseTest : TestBase() {

    @Test
    operator fun invoke() = runTest {
        val repo = mockk<TodoListPrefs>()

        coEvery { repo.setSortType(any()) } returns Unit

        val useCase = SetTodoItemSortTypeUseCaseImpl(
            repo = repo
        )

        useCase.invoke(TodoItemSortType.ALPHABETICAL)

        coVerify { repo.setSortType(TodoItemSortType.ALPHABETICAL) }
    }
}