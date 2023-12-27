package com.example.doit.ui.composables

import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.testing.Tags
import com.example.doit.testing.TodoItems
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

    @Test
    fun `test filtering`() {
        val tagFilter = TodoItems.todoList.applyFilter(Tags.tagOne, null, false)

        val resultTagFilter = listOf(
            TodoItems.todoItemOne,
            TodoItems.todoItemThree
        )
        Assert.assertEquals(resultTagFilter, tagFilter)

        val priorityFilter = TodoItems.todoList.applyFilter(null, Priority.MEDIUM, false)

        val resultPriorityFilter = listOf(TodoItems.todoItemOne)
        Assert.assertEquals(resultPriorityFilter, priorityFilter)

        val doneFilter = TodoItems.todoList.applyFilter(null, null, true)

        val resultDoneFilter = listOf(
            TodoItems.todoItemOne,
            TodoItems.todoItemFour
        )
        Assert.assertEquals(resultDoneFilter, doneFilter)
    }

}