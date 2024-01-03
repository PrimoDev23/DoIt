package com.example.doit.testing

import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.TodoItem
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import java.time.LocalDateTime

object TodoItems {

    val todoItemOne = TodoItem(
        id = "1",
        title = "B",
        description = "Description",
        done = false,
        tags = Tags.tagList,
        priority = Priority.MEDIUM,
        dueDate = LocalDate.now(),
        subtasks = Subtasks.subtasks,
        notificationDateTime = LocalDateTime.now().plusDays(1),
        creationDateTime = LocalDateTime.of(2020, 12, 12, 0, 0)
    )

    private val todoItemTwo = TodoItem(
        id = "2",
        title = "A",
        description = "Description",
        done = true,
        tags = persistentListOf(),
        priority = Priority.NONE,
        dueDate = LocalDate.of(2020, 12, 10),
        subtasks = Subtasks.subtasks,
        notificationDateTime = LocalDateTime.now(),
        creationDateTime = LocalDateTime.of(2020, 12, 8, 0, 0)
    )

    val todoItemThree = TodoItem(
        id = "3",
        title = "D",
        description = "Description",
        done = true,
        tags = persistentListOf(Tags.tagOne),
        priority = Priority.LOW,
        dueDate = LocalDate.of(2020, 12, 8),
        subtasks = Subtasks.subtasks,
        notificationDateTime = LocalDateTime.now(),
        creationDateTime = LocalDateTime.now()
    )

    val todoItemFour = TodoItem(
        id = "4",
        title = "C",
        description = "Description",
        done = false,
        tags = persistentListOf(),
        priority = Priority.HIGH,
        dueDate = LocalDate.of(2020, 12, 12),
        subtasks = Subtasks.subtasks,
        notificationDateTime = null,
        creationDateTime = LocalDateTime.of(2020, 12, 10, 0, 0)
    )

    val todoList = persistentListOf(
        todoItemOne,
        todoItemTwo,
        todoItemThree,
        todoItemFour
    )

    val todoListCreationDate = persistentListOf(
        todoItemThree,
        todoItemOne,
        todoItemFour,
        todoItemTwo
    )

    val todoListAlphabetical = persistentListOf(
        todoItemTwo,
        todoItemOne,
        todoItemFour,
        todoItemThree
    )

    val todoListPriority = persistentListOf(
        todoItemFour,
        todoItemOne,
        todoItemThree,
        todoItemTwo
    )

    val todoListDueDate = listOf(
        todoItemOne,
        todoItemFour,
        todoItemTwo,
        todoItemThree
    )

}