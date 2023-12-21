package com.example.doit.data

import com.example.doit.domain.models.Subtask
import java.time.LocalDateTime

object Subtasks {

    val subtasks = listOf(
        Subtask(
            id = "Sub1",
            title = "Subtask1",
            done = false,
            creationDateTime = LocalDateTime.now()
        ),
        Subtask(
            id = "Sub2",
            title = "Subtask2",
            done = true,
            creationDateTime = LocalDateTime.now()
        )
    )

}