package com.example.doit.data.mappers

import com.example.doit.data.models.local.TodoItemEntity
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TagRepository
import javax.inject.Inject

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

            TodoItem(
                id = id,
                title = title,
                description = description,
                done = done,
                tags = tags,
                priority = priority
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
                priority = priority
            )
        }
    }

    companion object {
        private const val SEPARATOR = "|"
    }
}