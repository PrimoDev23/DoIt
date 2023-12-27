package com.example.doit.data.models.local

import androidx.room.Embedded
import androidx.room.Relation
import com.example.doit.common.database.DatabaseConstants
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
            LocalDate.parse(it, DatabaseConstants.DATE_FORMATTER)
        }
        val notificationDateTime = notificationDateTime?.let {
            LocalDateTime.parse(it, DatabaseConstants.DATE_TIME_FORMATTER)
        }
        val creationDateTime =
            LocalDateTime.parse(creationDateTime, DatabaseConstants.DATE_TIME_FORMATTER)

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
    return if (item.item.tags.isEmpty()) {
        emptyList()
    } else {
        val tagIds = item.item.tags.split(DatabaseConstants.LIST_SEPARATOR).map { it.toLong() }
        this.getTagsByIds(tagIds)
    }
}