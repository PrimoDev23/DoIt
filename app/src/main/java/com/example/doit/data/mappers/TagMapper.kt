package com.example.doit.data.mappers

import androidx.compose.ui.graphics.Color
import com.example.doit.data.models.local.TagEntity
import com.example.doit.domain.models.Tag

class TagMapper : BaseMapper<TagEntity, Tag>() {

    override suspend fun map(item: TagEntity): Tag {
        return with(item) {
            Tag(
                id = id,
                title = title,
                color = Color(color.toULong()),
                selected = false
            )
        }
    }

    override suspend fun mapBack(item: Tag): TagEntity {
        return with(item) {
            TagEntity(
                id = id,
                title = title,
                color = color.value.toString()
            )
        }
    }

}