package com.example.doit.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.doit.domain.models.Priority

@Entity
data class TodoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val description: String,
    val done: Boolean,
    val tags: String,
    val priority: Priority,
    val dueDate: String?
)
