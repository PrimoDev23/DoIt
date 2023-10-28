package com.example.doit.data.models.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TodoItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["parent"])
    ]
)
data class SubtaskEntity(
    @PrimaryKey
    val id: String,
    val parent: String,
    val title: String,
    val done: Boolean
)