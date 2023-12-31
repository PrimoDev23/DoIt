package com.example.doit.domain.models

import androidx.compose.runtime.Immutable
import java.time.LocalDateTime

@Immutable
data class Subtask(
    val id: String,
    val title: String,
    val done: Boolean,
    val creationDateTime: LocalDateTime
)
