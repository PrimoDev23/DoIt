package com.example.doit.domain.models

data class TodoItem(
    val id: Long,
    val title: String,
    val description: String,
    val done: Boolean,
    val tags: List<Tag>
)
