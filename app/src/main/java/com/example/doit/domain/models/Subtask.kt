package com.example.doit.domain.models

import java.time.LocalDateTime

data class Subtask(
    val id: String,
    val title: String,
    val done: Boolean,
    val creationDateTime: LocalDateTime
)
