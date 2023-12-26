package com.example.doit.domain.usecases

import com.example.doit.TestBase
import com.example.doit.data.models.local.TodoListPreferences
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.repositories.PreferencesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTodoListPreferencesUseCaseTest : TestBase() {

    @Test
    operator fun invoke() {
        val repo = mockk<PreferencesRepository>()

        val flow = flow {
            emit(
                TodoListPreferences(
                    sortType = TodoItemSortType.CREATION_DATE,
                    hideDoneItems = false
                )
            )
        }

        every { repo.getTodoListPreferencesFlow() } returns flow

        val useCase = GetTodoListPreferencesUseCaseImpl(
            repo = repo
        )

        val result = useCase.invoke()

        verify { repo.getTodoListPreferencesFlow() }

        assertEquals(flow, result)
    }
}