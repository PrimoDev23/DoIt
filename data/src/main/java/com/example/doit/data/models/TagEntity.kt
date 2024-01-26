package com.example.doit.data.models

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.doit.domain.models.Tag

@Entity
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val color: String
) {
    fun toDomainModel() = Tag(
        id = id,
        title = title,
        color = Color(color.toULong())
    )
}

fun Tag.toEntity() = TagEntity(
    id = id,
    title = title,
    color = color.value.toString()
)