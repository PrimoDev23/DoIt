package com.example.doit.domain.models

import androidx.compose.runtime.Immutable
import java.time.LocalDate
import java.time.LocalDateTime

@Immutable
data class TodoItem(
    val id: String,
    val title: String,
    val description: String,
    val done: Boolean,
    val tags: List<Tag>,
    val priority: Priority,
    val dueDate: LocalDate?,
    val subtasks: List<Subtask>,
    val notificationDateTime: LocalDateTime?,
    val creationDateTime: LocalDateTime
)
