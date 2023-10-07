package com.example.doit.domain.models

import java.time.LocalDate

data class TodoItem(
    val id: Long,
    val title: String,
    val description: String,
    val done: Boolean,
    val tags: List<Tag>,
    val priority: Priority,
    val dueDate: LocalDate?
)
