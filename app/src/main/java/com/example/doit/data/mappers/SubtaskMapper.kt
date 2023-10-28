package com.example.doit.data.mappers

import com.example.doit.data.models.local.SubtaskEntity
import com.example.doit.domain.models.Subtask
import javax.inject.Inject

class SubtaskMapper @Inject constructor(

) : BaseMapper<SubtaskEntity, Subtask>() {

    override suspend fun map(item: SubtaskEntity): Subtask {
        return with(item) {
            Subtask(
                id = id,
                title = title,
                done = done
            )
        }
    }

}