package com.example.doit.domain.usecases

import com.example.doit.domain.models.Priority
import com.example.doit.testing.Tags
import com.example.doit.testing.TestBase
import com.example.doit.testing.TodoItems
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Test

class FilterTodoItemsUseCaseImplTest : TestBase() {

    @Test
    operator fun invoke() {
        val useCase = FilterTodoItemsUseCaseImpl()

        val noFilter = useCase(TodoItems.todoList, persistentListOf(), persistentListOf(), false)

        assertEquals(TodoItems.todoList, noFilter)

        val tagTwoFilter =
            useCase(TodoItems.todoList, persistentListOf(Tags.tagTwo), persistentListOf(), false)

        val resultTagTwoFilter = listOf(TodoItems.todoItemOne)
        assertEquals(resultTagTwoFilter, tagTwoFilter)

        val allTagsFilter = useCase(TodoItems.todoList, Tags.tagList, persistentListOf(), false)

        val resultAllTagsFilter = listOf(
            TodoItems.todoItemOne,
            TodoItems.todoItemThree
        )

        assertEquals(resultAllTagsFilter, allTagsFilter)

        val priorityMediumFilter = useCase(
            TodoItems.todoList,
            persistentListOf(),
            persistentListOf(Priority.MEDIUM),
            false
        )

        val resultPriorityMediumFilter = listOf(TodoItems.todoItemOne)
        assertEquals(resultPriorityMediumFilter, priorityMediumFilter)

        val priorityMediumHighFilter = useCase(
            TodoItems.todoList,
            persistentListOf(),
            persistentListOf(Priority.MEDIUM, Priority.HIGH),
            false
        )

        val resultPriorityMediumHighFilter = listOf(
            TodoItems.todoItemOne,
            TodoItems.todoItemFour
        )
        assertEquals(resultPriorityMediumHighFilter, priorityMediumHighFilter)

        val doneFilter = useCase(TodoItems.todoList, persistentListOf(), persistentListOf(), true)

        val resultDoneFilter = listOf(
            TodoItems.todoItemOne,
            TodoItems.todoItemFour
        )
        assertEquals(resultDoneFilter, doneFilter)
    }
}