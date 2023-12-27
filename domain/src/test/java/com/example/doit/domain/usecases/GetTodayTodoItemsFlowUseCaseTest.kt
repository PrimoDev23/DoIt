package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.testing.TestBase
import com.example.doit.testing.TodoItems
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import org.junit.Assert
import org.junit.Test

class GetTodayTodoItemsFlowUseCaseTest : TestBase() {

    @Test
    fun invoke() {
        val repo = mockk<TodoItemRepository>()

        val todayItems = listOf(
            TodoItems.todoItemOne
        )
        val todoItemsFlow = flow {
            emit(todayItems)
        }

        every { repo.getTodayItemsFlow() } returns todoItemsFlow

        val useCase = GetTodayTodoItemsFlowUseCaseImpl(
            repo = repo
        )

        val flow = useCase.invoke()

        verify { repo.getTodayItemsFlow() }

        Assert.assertEquals(todoItemsFlow, flow)
    }

}