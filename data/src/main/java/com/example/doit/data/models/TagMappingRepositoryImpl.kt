package com.example.doit.data.models

import com.example.doit.data.daos.TagMappingDao
import com.example.doit.domain.models.Tag
import com.example.doit.domain.repositories.TagMappingRepository

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
}