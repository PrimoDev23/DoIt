package com.example.doit.data.repositories

import com.example.doit.data.daos.SubtaskDao
import com.example.doit.data.mappers.SubtaskMapper
import com.example.doit.data.models.local.SubtaskEntity
import com.example.doit.domain.models.Subtask
import com.example.doit.domain.repositories.SubtaskRepository

class SubtaskRepositoryImpl(
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
                done = it.done,
                creationDateTime = SubtaskMapper.DATE_TIME_FORMATTER.format(it.creationDateTime)
            )
        }

        subtaskDao.insertOnSave(parent, mappedItems)
    }

    override suspend fun saveSubtaskForParent(parent: String, subtask: Subtask) {
        val mapped = with(subtask) {
            SubtaskEntity(
                id = id,
                parent = parent,
                title = title,
                done = done,
                creationDateTime = SubtaskMapper.DATE_TIME_FORMATTER.format(creationDateTime)
            )
        }

        subtaskDao.insert(mapped)
    }

    override suspend fun deleteByParent(parent: String) {
        subtaskDao.deleteByParent(parent)
    }
}