package com.example.doit.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.doit.domain.models.TagMapping

@Entity(
    primaryKeys = ["itemId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = TodoItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class TagMappingEntity(
    val itemId: String,
    val tagId: Long
) {
    fun toDomainModel() = TagMapping(
        itemId = itemId,
        tagId = tagId
    )
}
