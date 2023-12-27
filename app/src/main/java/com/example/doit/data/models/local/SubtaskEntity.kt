package com.example.doit.data.models.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.doit.common.AppDatabase
import com.example.doit.domain.models.Subtask
import java.time.LocalDateTime

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
    val done: Boolean,
    val creationDateTime: String
) {
    fun toDomainModel() = Subtask(
        id = id,
        title = title,
        done = done,
        creationDateTime = LocalDateTime.parse(
            creationDateTime,
            AppDatabase.DATE_TIME_FORMATTER
        )
    )
}

fun Subtask.toEntity(parent: String) = SubtaskEntity(
    id = id,
    parent = parent,
    title = title,
    done = done,
    creationDateTime = AppDatabase.DATE_TIME_FORMATTER.format(creationDateTime)
)