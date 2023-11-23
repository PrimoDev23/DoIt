package com.example.doit.data.mappers

import com.example.doit.data.models.local.TodoItemEntity
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.SubtaskRepository
import com.example.doit.domain.repositories.TagRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TodoItemMapper(
    private val tagRepository: TagRepository,
    private val subtaskRepository: SubtaskRepository,
) : BaseMapper<TodoItemEntity, TodoItem>() {
    override suspend fun map(item: TodoItemEntity): TodoItem {
        return with(item) {
            val tagIds = if (tags.isEmpty()) {
                emptyList()
            } else {
                tags.split(SEPARATOR).map { it.toLong() }
            }
            val tags = tagRepository.getTagsByIds(tagIds)
            val date = item.dueDate?.let {
                LocalDate.parse(it, DATE_FORMATTER)
            }
            val notificationDateTime = item.notificationDateTime?.let {
                LocalDateTime.parse(it, DATE_TIME_FORMATTER)
            }
            val creationDateTime = LocalDateTime.parse(item.creationDateTime, DATE_TIME_FORMATTER)

            val subtasks = subtaskRepository.getSubtasksByParent(id)

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

    override suspend fun mapBack(item: TodoItem): TodoItemEntity {
        return with(item) {
            val ids = tags.map { it.id }

            TodoItemEntity(
                id = id,
                title = title,
                description = description,
                done = done,
                tags = ids.joinToString(SEPARATOR),
                priority = priority,
                dueDate = item.dueDate?.format(DATE_FORMATTER),
                notificationDateTime = notificationDateTime?.format(DATE_TIME_FORMATTER),
                creationDateTime = creationDateTime.format(DATE_TIME_FORMATTER)
            )
        }
    }

    companion object {
        private const val SEPARATOR = "|"

        val DATE_FORMATTER = DateTimeFormatter.ISO_DATE
        val DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME
    }
}