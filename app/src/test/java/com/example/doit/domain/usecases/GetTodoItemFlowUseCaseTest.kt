package com.example.doit.domain.usecases

import com.example.doit.TestBase
import com.example.doit.data.TodoItems
import com.example.doit.domain.repositories.TodoItemRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import org.junit.Assert
import org.junit.Test

class GetTodoItemFlowUseCaseTest : TestBase() {
    @Test
    fun invoke() {
        val repo = mockk<TodoItemRepository>()

        val item = TodoItems.todoItemOne
        val itemFlow = flow {
            emit(item)
        }

        every { repo.getItemFlowById(item.id) } returns itemFlow

        val useCase = GetTodoItemFlowUseCaseImpl(
            repo = repo
        )

        val flow = useCase.invoke(item.id)

        verify { repo.getItemFlowById(item.id) }

        Assert.assertEquals(itemFlow, flow)
    }
}