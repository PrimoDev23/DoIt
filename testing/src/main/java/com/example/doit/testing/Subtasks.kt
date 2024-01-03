package com.example.doit.testing

import com.example.doit.domain.models.Subtask
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDateTime

object Subtasks {

    val subtaskOne = Subtask(
        id = "Sub1",
        title = "Subtask1",
        done = false,
        creationDateTime = LocalDateTime.now()
    )

    private val subtaskTwo = Subtask(
        id = "Sub2",
        title = "Subtask2",
        done = true,
        creationDateTime = LocalDateTime.now()
    )

    val subtasks = persistentListOf(
        subtaskOne,
        subtaskTwo
    )

}