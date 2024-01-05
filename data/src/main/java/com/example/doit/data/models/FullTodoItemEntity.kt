package com.example.doit.data.models

import androidx.room.Embedded
import androidx.room.Relation
import com.example.doit.common.constants.DatabaseConstants
import com.example.doit.domain.models.TodoItem
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate
import java.time.LocalDateTime

data class FullTodoItemEntity(
    @Embedded
    val item: TodoItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent"
    )
    val subtasks: List<SubtaskEntity>,
    @Relation(
        entity = TagMappingEntity::class,
        parentColumn = "id",
        entityColumn = "itemId"
    )
    val tags: List<TagMappingWithTagEntity>
) {
    fun toDomainModel(): TodoItem = with(item) {
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

        val tags = tags
            .map {
                it.tag.toDomainModel()
            }

        TodoItem(
            id = id,
            title = title,
            description = description,
            done = done,
            tags = tags.toPersistentList(),
            priority = priority,
            dueDate = date,
            subtasks = subtasks.toPersistentList(),
            notificationDateTime = notificationDateTime,
            creationDateTime = creationDateTime
        )
    }
}