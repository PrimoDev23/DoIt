package com.example.doit.data.mappers

import com.example.doit.data.models.local.TodoItemEntity
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TagRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val FORMATTER = DateTimeFormatter.ISO_DATE

class TodoItemMapper @Inject constructor(
    private val tagRepository: TagRepository
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
                LocalDate.parse(it, FORMATTER)
            }

            TodoItem(
                id = id,
                title = title,
                description = description,
                done = done,
                tags = tags,
                priority = priority,
                dueDate = date
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
                dueDate = item.dueDate?.format(FORMATTER)
            )
        }
    }

    companion object {
        private const val SEPARATOR = "|"
    }
}