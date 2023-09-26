package com.example.doit.data.models.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val color: String
)