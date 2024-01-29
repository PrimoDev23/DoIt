package com.example.doit.data.models

import androidx.compose.ui.graphics.Color
import com.example.doit.common.constants.DatabaseConstants
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.TodoItem
import com.example.doit.testing.TestBase
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class FullTodoItemEntityTest : TestBase() {

    @Test
    fun toDomainModel() {
        val dueDate = LocalDate.now()
        val notificationDateTime = LocalDateTime.now()
        val creationDateTime = LocalDateTime.now()

        val todoEntity = TodoItemEntity(
            id = "ID",
            title = "Title",
            description = "Description",
            done = true,
            priority = Priority.HIGH,
            dueDate = dueDate.format(DatabaseConstants.DATE_FORMATTER),
            notificationDateTime = notificationDateTime.format(DatabaseConstants.DATE_TIME_FORMATTER),
            creationDateTime = creationDateTime.format(DatabaseConstants.DATE_TIME_FORMATTER)
        )

        val subtask = SubtaskEntity(
            id = "ID2",
            parent = "ID",
            title = "Subtask",
            done = true,
            creationDateTime = LocalDateTime.now().format(DatabaseConstants.DATE_TIME_FORMATTER)
        )
        val subtaskEntities = listOf(subtask)

        val tag = TagEntity(
            id = 0,
            title = "Tag",
            color = Color.Green.value.toString()
        )

        val tagWithMappingEntities = listOf(
            TagMappingWithTagEntity(
                mapping = TagMappingEntity(
                    itemId = todoEntity.id,
                    tagId = tag.id
                ),
                tag = tag
            )
        )

        val entity = FullTodoItemEntity(
            item = todoEntity,
            subtasks = subtaskEntities,
            tags = tagWithMappingEntities
        )

        val mappedItem = entity.toDomainModel()
        val expected = TodoItem(
            id = "ID",
            title = "Title",
            description = "Description",
            done = true,
            tags = persistentListOf(tag.toDomainModel()),
            priority = Priority.HIGH,
            dueDate = dueDate,
            subtasks = persistentListOf(subtask.toDomainModel()),
            notificationDateTime = notificationDateTime,
            creationDateTime = creationDateTime
        )

        assertEquals(expected, mappedItem)
    }
}