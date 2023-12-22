package com.example.doit.ui.viewmodels

import app.cash.turbine.test
import com.example.doit.CoroutineTestBase
import com.example.doit.data.TodoItems
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.UpdateDoneUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class CalendarViewModelTest : CoroutineTestBase() {

    @Test
    fun `init ViewModel`() = runTest {
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()

        every { getTodoItemsFlowUseCase.getItemFlow() } returns flow {
            emit(TodoItems.todoList)
        }

        val viewModel = CalendarViewModel(
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            updateDoneUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            val state = awaitItem()

            Assert.assertEquals(TodoItems.todoList, state.items)
        }
    }

    @Test
    fun `update done state`() = runTest {
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val updateDoneUseCase = mockk<UpdateDoneUseCase>()

        every { getTodoItemsFlowUseCase.getItemFlow() } returns flow {
            emit(TodoItems.todoList)
        }

        coEvery { updateDoneUseCase(any(), any()) } returns Unit

        val viewModel = CalendarViewModel(
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            updateDoneUseCase = updateDoneUseCase
        )

        viewModel.onDoneChanged(TodoItems.fullTodoItem, true)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDoneUseCase(any(), true) }
    }

}