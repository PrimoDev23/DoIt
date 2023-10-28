package com.example.doit.data.repositories

import com.example.doit.data.daos.SubtaskDao
import com.example.doit.data.mappers.SubtaskMapper
import com.example.doit.data.models.local.SubtaskEntity
import com.example.doit.domain.models.Subtask
import com.example.doit.domain.repositories.SubtaskRepository
import javax.inject.Inject

class SubtaskRepositoryImpl @Inject constructor(
    private val subtaskDao: SubtaskDao,
    private val mapper: SubtaskMapper
) : SubtaskRepository {
    override suspend fun getSubtasksByParent(parent: String): List<Subtask> {
        return subtaskDao.selectByParent(parent).map {
            mapper.map(it)
        }
    }

    override suspend fun saveSubtasksForParent(parent: String, items: List<Subtask>) {
        val mappedItems = items.map {
            SubtaskEntity(
                id = it.id,
                parent = parent,
                title = it.title,
                done = it.done
            )
        }

        subtaskDao.insertOnSave(parent, mappedItems)
    }

    override suspend fun deleteByParent(parent: String) {
        subtaskDao.deleteByParent(parent)
    }
}