package com.example.doit.domain.repositories

import com.example.doit.domain.models.Subtask

interface SubtaskRepository {

    suspend fun getSubtasksByParent(parent: String): List<Subtask>
    suspend fun saveSubtasksForParent(parent: String, items: List<Subtask>)
    suspend fun saveSubtaskForParent(parent: String, subtask: Subtask)
    suspend fun deleteByParent(parent: String)

}