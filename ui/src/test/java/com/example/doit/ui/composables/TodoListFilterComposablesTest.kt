package com.example.doit.ui.composables

import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.testing.Tags
import com.example.doit.testing.TodoItems
import org.junit.Assert.assertEquals
import org.junit.Test

class TodoListFilterComposablesTest {

    @Test
    fun `test sorting`() {
        val creationDate = TodoItems.todoList.sort(TodoItemSortType.CREATION_DATE)

        assertEquals(TodoItems.todoListCreationDate, creationDate)

        val alphabetical = TodoItems.todoList.sort(TodoItemSortType.ALPHABETICAL)

        assertEquals(TodoItems.todoListAlphabetical, alphabetical)

        val dueDate = TodoItems.todoList.sort(TodoItemSortType.DUE_DATE)

        assertEquals(TodoItems.todoListDueDate, dueDate)

        val priority = TodoItems.todoList.sort(TodoItemSortType.PRIORITY)

        assertEquals(TodoItems.todoListPriority, priority)
    }

    @Test
    fun `test filtering`() {
        val noFilter = TodoItems.todoList.applyFilter(emptyList(), emptyList(), false)

        assertEquals(TodoItems.todoList, noFilter)

        val tagTwoFilter = TodoItems.todoList.applyFilter(listOf(Tags.tagTwo), emptyList(), false)

        val resultTagTwoFilter = listOf(TodoItems.todoItemOne)
        assertEquals(resultTagTwoFilter, tagTwoFilter)

        val allTagsFilter = TodoItems.todoList.applyFilter(Tags.tagList, emptyList(), false)

        val resultAllTagsFilter = listOf(
            TodoItems.todoItemOne,
            TodoItems.todoItemThree
        )

        assertEquals(resultAllTagsFilter, allTagsFilter)

        val priorityMediumFilter =
            TodoItems.todoList.applyFilter(emptyList(), listOf(Priority.MEDIUM), false)

        val resultPriorityMediumFilter = listOf(TodoItems.todoItemOne)
        assertEquals(resultPriorityMediumFilter, priorityMediumFilter)

        val priorityMediumHighFilter =
            TodoItems.todoList.applyFilter(
                emptyList(),
                listOf(Priority.MEDIUM, Priority.HIGH),
                false
            )

        val resultPriorityMediumHighFilter = listOf(
            TodoItems.todoItemOne,
            TodoItems.todoItemFour
        )
        assertEquals(resultPriorityMediumHighFilter, priorityMediumHighFilter)

        val doneFilter = TodoItems.todoList.applyFilter(emptyList(), emptyList(), true)

        val resultDoneFilter = listOf(
            TodoItems.todoItemOne,
            TodoItems.todoItemFour
        )
        assertEquals(resultDoneFilter, doneFilter)
    }

}