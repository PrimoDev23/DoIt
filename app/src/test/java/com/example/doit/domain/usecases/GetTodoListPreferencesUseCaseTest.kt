package com.example.doit.domain.usecases

import com.example.doit.TestBase
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.preferences.TodoListPrefs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTodoListPreferencesUseCaseTest : TestBase() {

    @Test
    operator fun invoke() {
        val repo = mockk<TodoListPrefs>()

        val flow = flow {
            emit(
                com.example.doit.domain.models.TodoListPreferences(
                    sortType = TodoItemSortType.CREATION_DATE,
                    hideDoneItems = false
                )
            )
        }

        every { repo.getFlow() } returns flow

        val useCase = GetTodoListPreferencesUseCaseImpl(
            repo = repo
        )

        val result = useCase.invoke()

        verify { repo.getFlow() }

        assertEquals(flow, result)
    }
}