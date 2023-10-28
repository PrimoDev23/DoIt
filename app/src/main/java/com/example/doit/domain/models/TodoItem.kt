package com.example.doit.domain.models

import java.time.LocalDate
import java.time.LocalDateTime

data class TodoItem(
    val id: String,
    val title: String,
    val description: String,
    val done: Boolean,
    val tags: List<Tag>,
    val priority: Priority,
    val dueDate: LocalDate?,
    val subtasks: List<Subtask>,
    val notificationDateTime: LocalDateTime?
)
