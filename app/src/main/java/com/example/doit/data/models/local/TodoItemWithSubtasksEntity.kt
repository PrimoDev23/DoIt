package com.example.doit.data.models.local

import androidx.room.Embedded
import androidx.room.Relation
import com.example.doit.common.AppDatabase
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TagRepository
import java.time.LocalDate
import java.time.LocalDateTime

data class TodoItemWithSubtasksEntity(
    @Embedded
    val item: TodoItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent"
    )
    val subtasks: List<SubtaskEntity>
) {
    fun toDomainModel(tags: List<Tag>): TodoItem = with(item) {
        val date = dueDate?.let {
            LocalDate.parse(it, AppDatabase.DATE_FORMATTER)
        }
        val notificationDateTime = notificationDateTime?.let {
            LocalDateTime.parse(it, AppDatabase.DATE_TIME_FORMATTER)
        }
        val creationDateTime =
            LocalDateTime.parse(creationDateTime, AppDatabase.DATE_TIME_FORMATTER)

        val subtasks = subtasks
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

// This is more related to this specific entity, so it should belong here
suspend fun TagRepository.getTagsForItem(item: TodoItemWithSubtasksEntity): List<Tag> {
    val tagIds = if (item.item.tags.isEmpty()) {
        emptyList()
    } else {
        item.item.tags.split(AppDatabase.LIST_SEPARATOR).map { it.toLong() }
    }
    return this.getTagsByIds(tagIds)
}