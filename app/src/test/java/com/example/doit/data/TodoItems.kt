package com.example.doit.data

import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.TodoItem
import java.time.LocalDate
import java.time.LocalDateTime

object TodoItems {

    val fullTodoItem = TodoItem(
        id = "12345",
        title = "Test1234",
        description = "Description",
        done = false,
        tags = Tags.tagList,
        priority = Priority.HIGH,
        dueDate = LocalDate.now(),
        subtasks = Subtasks.subtasks,
        notificationDateTime = LocalDateTime.now(),
        creationDateTime = LocalDateTime.now()
    )

}