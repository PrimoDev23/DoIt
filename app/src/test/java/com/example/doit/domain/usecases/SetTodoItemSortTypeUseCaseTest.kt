package com.example.doit.domain.usecases

import com.example.doit.TestBase
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.repositories.PreferencesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetTodoItemSortTypeUseCaseTest : TestBase() {

    @Test
    operator fun invoke() = runTest {
        val repo = mockk<PreferencesRepository>()

        coEvery { repo.setTodoItemSortType(any()) } returns Unit

        val useCase = SetTodoItemSortTypeUseCaseImpl(
            repo = repo
        )

        useCase.invoke(TodoItemSortType.ALPHABETICAL)

        coVerify { repo.setTodoItemSortType(TodoItemSortType.ALPHABETICAL) }
    }
}