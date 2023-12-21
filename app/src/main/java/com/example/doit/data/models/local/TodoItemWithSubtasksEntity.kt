package com.example.doit.data.models.local

data class TodoItemWithSubtasksEntity(
    val item: TodoItemEntity,
    val subtasks: List<SubtaskEntity>
)