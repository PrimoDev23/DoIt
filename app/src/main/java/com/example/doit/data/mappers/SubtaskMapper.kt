package com.example.doit.data.mappers

import com.example.doit.data.models.local.SubtaskEntity
import com.example.doit.domain.models.Subtask
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SubtaskMapper : BaseMapper<SubtaskEntity, Subtask>() {

    override suspend fun map(item: SubtaskEntity): Subtask {
        return with(item) {
            Subtask(
                id = id,
                title = title,
                done = done,
                creationDateTime = LocalDateTime.parse(creationDateTime, DATE_TIME_FORMATTER)
            )
        }
    }

    companion object {
        val DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME
    }

}