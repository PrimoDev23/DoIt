package com.example.doit.domain.usecases

import com.example.doit.domain.models.Subtask
import com.example.doit.domain.repositories.SubtaskRepository
import com.example.doit.domain.usecases.interfaces.UpdateSubtaskDoneUseCase

class UpdateSubtaskDoneUseCaseImpl(
    private val repo: SubtaskRepository
) : UpdateSubtaskDoneUseCase {
    override suspend fun invoke(parent: String, subtask: Subtask, done: Boolean) {
        val newSubtask = subtask.copy(done = done)

        repo.saveSubtaskForParent(parent, newSubtask)
    }
}