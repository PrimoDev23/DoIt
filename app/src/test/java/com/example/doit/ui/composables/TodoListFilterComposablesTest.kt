package com.example.doit.ui.composables

import com.example.doit.data.TodoItems
import com.example.doit.domain.models.TodoItemSortType
import org.junit.Assert
import org.junit.Test

class TodoListFilterComposablesTest {

    @Test
    fun `test sorting`() {
        val creationDate = TodoItems.todoList.sort(TodoItemSortType.CREATION_DATE)

        Assert.assertEquals(TodoItems.todoListCreationDate, creationDate)

        val alphabetical = TodoItems.todoList.sort(TodoItemSortType.ALPHABETICAL)

        Assert.assertEquals(TodoItems.todoListAlphabetical, alphabetical)

        val dueDate = TodoItems.todoList.sort(TodoItemSortType.DUE_DATE)

        Assert.assertEquals(TodoItems.todoListDueDate, dueDate)

        val priority = TodoItems.todoList.sort(TodoItemSortType.PRIORITY)

        Assert.assertEquals(TodoItems.todoListPriority, priority)
    }

}