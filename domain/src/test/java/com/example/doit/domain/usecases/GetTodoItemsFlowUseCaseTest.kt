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

class GetTodoItemsFlowUseCaseTest : TestBase() {

    @Test
    fun getItemFlow() {
        val repo = mockk<TodoItemRepository>()

        val itemFlow = flow {
            emit(TodoItems.todoList)
        }

        every { repo.getItemsFlow() } returns itemFlow

        val useCase = GetTodoItemsFlowUseCaseImpl(
            repo = repo
        )

        val flow = useCase.getItemFlow()

        verify { repo.getItemsFlow() }

        Assert.assertEquals(itemFlow, flow)
    }
}