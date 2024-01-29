package com.example.doit.data.models

import com.example.doit.common.constants.DatabaseConstants
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.TodoItem
import com.example.doit.testing.TestBase
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class TodoItemEntityTest : TestBase() {

    @Test
    fun toEntity() {
        val dueDate = LocalDate.now()
        val notificationDateTime = LocalDateTime.now()
        val creationDateTime = LocalDateTime.now()

        val item = TodoItem(
            id = "ID",
            title = "Title",
            description = "Description",
            done = true,
            tags = persistentListOf(),
            priority = Priority.HIGH,
            dueDate = dueDate,
            subtasks = persistentListOf(),
            notificationDateTime = notificationDateTime,
            creationDateTime = creationDateTime
        )

        val expected = TodoItemEntity(
            id = "ID",
            title = "Title",
            description = "Description",
            done = true,
            priority = Priority.HIGH,
            dueDate = dueDate.format(DatabaseConstants.DATE_FORMATTER),
            notificationDateTime = notificationDateTime.format(DatabaseConstants.DATE_TIME_FORMATTER),
            creationDateTime = creationDateTime.format(DatabaseConstants.DATE_TIME_FORMATTER)
        )

        assertEquals(expected, item.toEntity())
    }

}