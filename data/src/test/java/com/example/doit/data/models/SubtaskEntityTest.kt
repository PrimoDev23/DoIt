package com.example.doit.data.models

import com.example.doit.common.constants.DatabaseConstants
import com.example.doit.domain.models.Subtask
import com.example.doit.testing.TestBase
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class SubtaskEntityTest : TestBase() {

    @Test
    fun toDomainModel() {
        val creationDateTime = LocalDateTime.now()

        val entity = SubtaskEntity(
            id = "ID",
            title = "Title",
            done = true,
            parent = "Parent",
            creationDateTime = creationDateTime.format(DatabaseConstants.DATE_TIME_FORMATTER)
        )

        val expected = Subtask(
            id = "ID",
            title = "Title",
            done = true,
            creationDateTime = creationDateTime
        )

        assertEquals(expected, entity.toDomainModel())
    }
}