package com.example.doit.data

import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.TodoItem
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
        tags = emptyList(),
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
        tags = listOf(Tags.tagOne),
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
        tags = emptyList(),
        priority = Priority.HIGH,
        dueDate = LocalDate.of(2020, 12, 12),
        subtasks = Subtasks.subtasks,
        notificationDateTime = null,
        creationDateTime = LocalDateTime.of(2020, 12, 10, 0, 0)
    )

    val todoList = listOf(
        todoItemOne,
        todoItemTwo,
        todoItemThree,
        todoItemFour
    )

    val todoListCreationDate = listOf(
        todoItemThree,
        todoItemOne,
        todoItemFour,
        todoItemTwo
    )

    val todoListAlphabetical = listOf(
        todoItemTwo,
        todoItemOne,
        todoItemFour,
        todoItemThree
    )

    val todoListPriority = listOf(
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