package com.example.doit.data.models.local

import androidx.room.Embedded
import androidx.room.Relation

data class TodoItemWithSubtasksEntity(
    @Embedded
    val item: TodoItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parent"
    )
    val subtasks: List<SubtaskEntity>
)