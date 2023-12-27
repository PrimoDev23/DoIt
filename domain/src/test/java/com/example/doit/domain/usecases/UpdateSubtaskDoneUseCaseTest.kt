package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.SubtaskRepository
import com.example.doit.testing.TestBase
import com.example.doit.testing.TodoItems
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class UpdateSubtaskDoneUseCaseTest : TestBase() {

    @Test
    operator fun invoke() = runTest {
        val repo = mockk<SubtaskRepository>()

        coEvery { repo.saveSubtaskForParent(any(), any()) } returns Unit

        val useCase = UpdateSubtaskDoneUseCaseImpl(
            repo = repo
        )

        val item = TodoItems.todoItemOne
        val subtask = item.subtasks[0]

        useCase.invoke(item.id, subtask, true)

        val doneSubtask = subtask.copy(done = true)

        coVerify { repo.saveSubtaskForParent(item.id, doneSubtask) }
    }
}