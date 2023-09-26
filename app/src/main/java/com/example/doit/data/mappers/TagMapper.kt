package com.example.doit.data.mappers

import androidx.compose.ui.graphics.Color
import com.example.doit.data.models.local.TagEntity
import com.example.doit.domain.models.Tag
import javax.inject.Inject

class TagMapper @Inject constructor(

) : BaseMapper<TagEntity, Tag>() {

    override fun map(item: TagEntity): Tag {
        return with(item) {
            Tag(
                id = id,
                title = title,
                color = Color(color.toULong())
            )
        }
    }

    override fun mapBack(item: Tag): TagEntity {
        return with(item) {
            TagEntity(
                id = id,
                title = title,
                color = color.value.toString()
            )
        }
    }

}