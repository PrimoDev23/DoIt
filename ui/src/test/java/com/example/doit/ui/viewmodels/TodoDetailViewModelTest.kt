package com.example.doit.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemFlowUseCase
import com.example.doit.domain.usecases.interfaces.UpdateSubtaskDoneUseCase
import com.example.doit.testing.CoroutineTestBase
import com.example.doit.testing.Subtasks
import com.example.doit.testing.TodoItems
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class TodoDetailViewModelTest : CoroutineTestBase() {

    private fun buildSavedStateHandle() = SavedStateHandle().apply {
        this["id"] = TodoItems.todoItemOne.id
    }

    @Test
    fun `init ViewModel`() = runTest {
        val getTodoItemFlowUseCase = mockk<GetTodoItemFlowUseCase>()

        every { getTodoItemFlowUseCase(any()) } returns flow {
            emit(TodoItems.todoItemOne)
        }

        val viewModel = TodoDetailViewModel(
            savedStateHandle = buildSavedStateHandle(),
            getTodoItemFlowUseCase = getTodoItemFlowUseCase,
            updateSubtaskDoneUseCase = mockk(),
            deleteTodoItemsUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            val state = awaitItem()

            Assert.assertEquals(TodoItems.todoItemOne, state.item)
        }
    }

    @Test
    fun `update subtask state`() {
        val getTodoItemFlowUseCase = mockk<GetTodoItemFlowUseCase>()
        val updateSubtaskDoneUseCase = mockk<UpdateSubtaskDoneUseCase>()

        every { getTodoItemFlowUseCase(any()) } returns flow {
            emit(TodoItems.todoItemOne)
        }

        coEvery { updateSubtaskDoneUseCase(any(), any(), any()) } returns Unit

        val viewModel = TodoDetailViewModel(
            savedStateHandle = buildSavedStateHandle(),
            getTodoItemFlowUseCase = getTodoItemFlowUseCase,
            updateSubtaskDoneUseCase = updateSubtaskDoneUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        viewModel.onSubtaskDoneChanged(Subtasks.subtasks[0], true)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { updateSubtaskDoneUseCase(TodoItems.todoItemOne.id, Subtasks.subtasks[0], true) }
    }

    @Test
    fun onDeleteClicked() = runTest {
        val getTodoItemFlowUseCase = mockk<GetTodoItemFlowUseCase>()
        val deleteTodoItemsUseCase = mockk<DeleteTodoItemsUseCase>()

        val item = TodoItems.todoItemOne

        every { getTodoItemFlowUseCase(any()) } returns flow {
            emit(item)
        }

        coEvery { deleteTodoItemsUseCase.delete(any()) } returns Unit

        val viewModel = TodoDetailViewModel(
            savedStateHandle = buildSavedStateHandle(),
            getTodoItemFlowUseCase = getTodoItemFlowUseCase,
            updateSubtaskDoneUseCase = mockk(),
            deleteTodoItemsUseCase = deleteTodoItemsUseCase
        )

        viewModel.events.test {
            expectNoEvents()

            viewModel.onDeleteClicked()
            dispatcher.scheduler.advanceUntilIdle()

            coVerify { deleteTodoItemsUseCase.delete(listOf(item)) }

            val event = awaitItem()

            Assert.assertTrue(event is TodoDetailEvent.PopBackStack)
        }
    }

}