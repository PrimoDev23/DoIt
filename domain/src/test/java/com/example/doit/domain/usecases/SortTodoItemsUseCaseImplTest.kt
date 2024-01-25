package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.testing.TestBase
import com.example.doit.testing.TodoItems
import org.junit.Assert.assertEquals
import org.junit.Test

class SortTodoItemsUseCaseImplTest : TestBase() {

    @Test
    operator fun invoke() {
        val useCase = SortTodoItemsUseCaseImpl()

        val creationDate = useCase(TodoItems.todoList, TodoItemSortType.CREATION_DATE)

        assertEquals(TodoItems.todoListCreationDate, creationDate)

        val alphabetical = useCase(TodoItems.todoList, TodoItemSortType.ALPHABETICAL)

        assertEquals(TodoItems.todoListAlphabetical, alphabetical)

        val dueDate = useCase(TodoItems.todoList, TodoItemSortType.DUE_DATE)

        assertEquals(TodoItems.todoListDueDate, dueDate)

        val priority = useCase(TodoItems.todoList, TodoItemSortType.PRIORITY)

        assertEquals(TodoItems.todoListPriority, priority)
    }
}