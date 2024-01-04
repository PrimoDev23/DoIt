package com.example.doit.data.models

import androidx.room.Embedded
import androidx.room.Relation

data class TagMappingWithTagEntity(
    @Embedded
    val mapping: TagMappingEntity,
    @Relation(
        parentColumn = "tagId",
        entityColumn = "id"
    )
    val tag: TagEntity
)