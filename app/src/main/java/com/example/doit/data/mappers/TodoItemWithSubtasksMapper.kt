package com.example.doit.data.mappers

import com.example.doit.common.AppDatabase
import com.example.doit.data.models.local.TodoItemEntity
import com.example.doit.data.models.local.TodoItemWithSubtasksEntity
import com.example.doit.data.models.local.toEntity
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TagRepository
import java.time.LocalDate
import java.time.LocalDateTime

class TodoItemWithSubtasksMapper(
    private val tagRepository: TagRepository
) : BaseMapper<TodoItemWithSubtasksEntity, TodoItem>() {
    override suspend fun map(item: TodoItemWithSubtasksEntity): TodoItem {
        return with(item.item) {
            val tagIds = if (tags.isEmpty()) {
                emptyList()
            } else {
                tags.split(AppDatabase.LIST_SEPARATOR).map { it.toLong() }
            }
            val tags = tagRepository.getTagsByIds(tagIds)
            val date = dueDate?.let {
                LocalDate.parse(it, AppDatabase.DATE_FORMATTER)
            }
            val notificationDateTime = notificationDateTime?.let {
                LocalDateTime.parse(it, AppDatabase.DATE_TIME_FORMATTER)
            }
            val creationDateTime =
                LocalDateTime.parse(creationDateTime, AppDatabase.DATE_TIME_FORMATTER)

            val subtasks = item.subtasks
                .map {
                    it.toDomainModel()
                }
                .sortedBy {
                    it.creationDateTime
                }

            TodoItem(
                id = id,
                title = title,
                description = description,
                done = done,
                tags = tags,
                priority = priority,
                dueDate = date,
                subtasks = subtasks,
                notificationDateTime = notificationDateTime,
                creationDateTime = creationDateTime
            )
        }
    }

    override suspend fun mapBack(item: TodoItem): TodoItemWithSubtasksEntity {
        return with(item) {
            val ids = tags.map { it.id }

            val todoItem = TodoItemEntity(
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

            val subtasks = item.subtasks.map {
                it.toEntity(id)
            }

            TodoItemWithSubtasksEntity(
                item = todoItem,
                subtasks = subtasks
            )
        }
    }
}