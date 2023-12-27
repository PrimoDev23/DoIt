package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.Subtask

interface UpdateSubtaskDoneUseCase {
    suspend operator fun invoke(parent: String, subtask: Subtask, done: Boolean)
}