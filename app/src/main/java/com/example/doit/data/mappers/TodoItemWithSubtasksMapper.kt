package com.example.doit.data.mappers

import com.example.doit.data.models.local.TodoItemEntity
import com.example.doit.data.models.local.TodoItemWithSubtasksEntity
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TagRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TodoItemWithSubtasksMapper(
    private val tagRepository: TagRepository,
    private val subtaskMapper: SubtaskMapper
) : BaseMapper<TodoItemWithSubtasksEntity, TodoItem>() {
    override suspend fun map(item: TodoItemWithSubtasksEntity): TodoItem {
        return with(item.item) {
            val tagIds = if (tags.isEmpty()) {
                emptyList()
            } else {
                tags.split(SEPARATOR).map { it.toLong() }
            }
            val tags = tagRepository.getTagsByIds(tagIds)
            val date = dueDate?.let {
                LocalDate.parse(it, DATE_FORMATTER)
            }
            val notificationDateTime = notificationDateTime?.let {
                LocalDateTime.parse(it, DATE_TIME_FORMATTER)
            }
            val creationDateTime = LocalDateTime.parse(creationDateTime, DATE_TIME_FORMATTER)

            val subtasks = item.subtasks.map {
                subtaskMapper.map(it)
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
                tags = ids.joinToString(SEPARATOR),
                priority = priority,
                dueDate = item.dueDate?.format(TodoItemMapper.DATE_FORMATTER),
                notificationDateTime = notificationDateTime?.format(TodoItemMapper.DATE_TIME_FORMATTER),
                creationDateTime = creationDateTime.format(TodoItemMapper.DATE_TIME_FORMATTER)
            )

            val subtasks = item.subtasks.map {
                subtaskMapper.mapBack(it)
            }

            TodoItemWithSubtasksEntity(
                item = todoItem,
                subtasks = subtasks
            )
        }
    }

    companion object {
        private const val SEPARATOR = "|"

        val DATE_FORMATTER = DateTimeFormatter.ISO_DATE
        val DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME
    }
}