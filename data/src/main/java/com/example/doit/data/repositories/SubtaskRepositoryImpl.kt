package com.example.doit.data.repositories

import com.example.doit.data.daos.SubtaskDao
import com.example.doit.data.models.toEntity
import com.example.doit.domain.models.Subtask
import com.example.doit.domain.repositories.SubtaskRepository

class SubtaskRepositoryImpl(
    private val subtaskDao: SubtaskDao
) : SubtaskRepository {
    override suspend fun getSubtasksByParent(parent: String): List<Subtask> {
        return subtaskDao.selectByParent(parent).map {
            it.toDomainModel()
        }
    }

    override suspend fun saveSubtasksForParent(parent: String, items: List<Subtask>) {
        val mappedItems = items.map {
            it.toEntity(parent)
        }

        subtaskDao.insertOnSave(parent, mappedItems)
    }

    override suspend fun saveSubtaskForParent(parent: String, subtask: Subtask) {
        val mapped = subtask.toEntity(parent)

        subtaskDao.insert(mapped)
    }

    override suspend fun deleteByParent(parent: String) {
        subtaskDao.deleteByParent(parent)
    }
}