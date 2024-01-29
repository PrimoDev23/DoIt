package com.example.doit.data.models

import com.example.doit.domain.models.TagMapping
import com.example.doit.testing.TestBase
import org.junit.Assert.assertEquals
import org.junit.Test

class TagMappingEntityTest : TestBase() {

    @Test
    fun toDomainModel() {
        val entity = TagMappingEntity(
            itemId = "ID",
            tagId = 0
        )

        val expected = TagMapping(
            itemId = "ID",
            tagId = 0
        )

        assertEquals(expected, entity.toDomainModel())
    }
}