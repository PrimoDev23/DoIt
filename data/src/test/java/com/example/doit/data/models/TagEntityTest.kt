package com.example.doit.data.models

import androidx.compose.ui.graphics.Color
import com.example.doit.domain.models.Tag
import com.example.doit.testing.TestBase
import org.junit.Assert.assertEquals
import org.junit.Test

class TagEntityTest : TestBase() {

    @Test
    fun toDomainModel() {
        val entity = TagEntity(
            id = 0,
            title = "Title",
            color = Color.Green.value.toString()
        )

        val expected = Tag(
            id = 0,
            title = "Title",
            color = Color.Green
        )

        assertEquals(expected, entity.toDomainModel())
    }

    @Test
    fun toEntity() {
        val tag = Tag(
            id = 0,
            title = "Title",
            color = Color.Green
        )

        val expected = TagEntity(
            id = 0,
            title = "Title",
            color = Color.Green.value.toString()
        )

        assertEquals(expected, tag.toEntity())
    }
}