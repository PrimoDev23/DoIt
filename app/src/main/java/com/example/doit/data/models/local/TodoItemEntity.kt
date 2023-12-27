package com.example.doit.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.doit.common.constants.DatabaseConstants
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.TodoItem

@Entity
data class TodoItemEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val done: Boolean,
    val tags: String,
    val priority: Priority,
    val dueDate: String?,
    val notificationDateTime: String?,
    val creationDateTime: String
)

fun TodoItem.toEntity(): TodoItemEntity {
    val ids = tags.map { it.id }

    return TodoItemEntity(
        id = id,
        title = title,
        description = description,
        done = done,
        tags = ids.joinToString(DatabaseConstants.LIST_SEPARATOR),
        priority = priority,
        dueDate = dueDate?.format(DatabaseConstants.DATE_FORMATTER),
        notificationDateTime = notificationDateTime?.format(DatabaseConstants.DATE_TIME_FORMATTER),
        creationDateTime = creationDateTime.format(DatabaseConstants.DATE_TIME_FORMATTER)
    )
}