package com.example.doit.domain.usecases

import com.example.doit.domain.preferences.TodoListPrefs
import com.example.doit.testing.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SetHideDoneItemsUseCaseTest : TestBase() {

    @Test
    operator fun invoke() = runTest {
        val repo = mockk<TodoListPrefs>()

        coEvery { repo.setHideDoneItems(any()) } returns Unit

        val useCase = SetHideDoneItemsUseCaseImpl(
            repo = repo
        )

        useCase.invoke(true)

        coVerify { repo.setHideDoneItems(true) }

        useCase.invoke(false)

        coVerify { repo.setHideDoneItems(false) }
    }
}