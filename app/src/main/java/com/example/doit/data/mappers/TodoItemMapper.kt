package com.example.doit.data.mappers

import com.example.doit.common.AppDatabase
import com.example.doit.data.models.local.TodoItemEntity
import com.example.doit.domain.models.TodoItem

class TodoItemMapper : BaseMapper<TodoItemEntity, TodoItem>() {
    override suspend fun mapBack(item: TodoItem): TodoItemEntity {
        return with(item) {
            val ids = tags.map { it.id }

            TodoItemEntity(
                id = id,
                title = title,
                description = description,
                done = done,
                tags = ids.joinToString(AppDatabase.LIST_SEPARATOR),
                priority = priority,
                dueDate = item.dueDate?.format(AppDatabase.DATE_FORMATTER),
                notificationDateTime = notificationDateTime?.format(AppDatabase.DATE_TIME_FORMATTER),
                creationDateTime = creationDateTime.format(AppDatabase.DATE_TIME_FORMATTER)
            )
        }
    }
}