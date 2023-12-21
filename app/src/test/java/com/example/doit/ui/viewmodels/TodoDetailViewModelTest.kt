package com.example.doit.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.doit.data.Subtasks
import com.example.doit.data.TodoItems
import com.example.doit.domain.usecases.interfaces.GetTodoItemFlowUseCase
import com.example.doit.domain.usecases.interfaces.UpdateSubtaskDoneUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun before() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun after() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun buildSavedStateHandle() = SavedStateHandle().apply {
        this["id"] = TodoItems.fullTodoItem.id
    }

    @Test
    fun `init ViewModel`() = runTest {
        val getTodoItemFlowUseCase = mockk<GetTodoItemFlowUseCase>()

        every { getTodoItemFlowUseCase(any()) } returns flow {
            emit(TodoItems.fullTodoItem)
        }

        val viewModel = TodoDetailViewModel(
            savedStateHandle = buildSavedStateHandle(),
            getTodoItemFlowUseCase = getTodoItemFlowUseCase,
            updateSubtaskDoneUseCase = mockk()
        )

        viewModel.state.test {
            val state = awaitItem()

            Assert.assertEquals(TodoItems.fullTodoItem, state.item)
        }
    }

    @Test
    fun `update subtask state`() {
        val getTodoItemFlowUseCase = mockk<GetTodoItemFlowUseCase>()
        val updateSubtaskDoneUseCase = mockk<UpdateSubtaskDoneUseCase>()

        every { getTodoItemFlowUseCase(any()) } returns flow {
            emit(TodoItems.fullTodoItem)
        }

        coEvery { updateSubtaskDoneUseCase(any(), any(), any()) } returns Unit

        val viewModel = TodoDetailViewModel(
            savedStateHandle = buildSavedStateHandle(),
            getTodoItemFlowUseCase = getTodoItemFlowUseCase,
            updateSubtaskDoneUseCase = updateSubtaskDoneUseCase
        )

        viewModel.onSubtaskDoneChanged(Subtasks.subtasks[0], true)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { updateSubtaskDoneUseCase(TodoItems.fullTodoItem.id, Subtasks.subtasks[0], true) }
    }

}