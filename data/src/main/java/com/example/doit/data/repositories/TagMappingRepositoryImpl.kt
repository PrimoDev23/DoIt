package com.example.doit.data.repositories

import com.example.doit.data.daos.TagMappingDao
import com.example.doit.data.models.TagMappingEntity
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TagMapping
import com.example.doit.domain.repositories.TagMappingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagMappingRepositoryImpl(
    private val dao: TagMappingDao
) : TagMappingRepository {
    override suspend fun saveTagMappings(parent: String, tags: List<Tag>) {
        val mappings = tags.map {
            TagMappingEntity(
                itemId = parent,
                tagId = it.id
            )
        }

        dao.deleteAndInsertTagMappings(mappings)
    }

    override fun getTagMappingsFlow(): Flow<List<TagMapping>> {
        return dao.selectAllFlow().map { items ->
            items.map {
                it.toDomainModel()
            }
        }
    }
}