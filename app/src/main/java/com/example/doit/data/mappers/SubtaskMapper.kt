package com.example.doit.data.mappers

import com.example.doit.common.AppDatabase
import com.example.doit.data.models.local.SubtaskEntity
import com.example.doit.domain.models.Subtask
import java.time.LocalDateTime

class SubtaskMapper : BaseMapper<SubtaskEntity, Subtask>() {

    override suspend fun map(item: SubtaskEntity): Subtask {
        return with(item) {
            Subtask(
                id = id,
                title = title,
                done = done,
                creationDateTime = LocalDateTime.parse(
                    creationDateTime,
                    AppDatabase.DATE_TIME_FORMATTER
                )
            )
        }
    }
}