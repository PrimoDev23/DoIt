package com.example.doit.data.repositories

import com.example.doit.data.daos.TagDao
import com.example.doit.data.models.local.toEntity
import com.example.doit.domain.models.Tag
import com.example.doit.domain.repositories.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagRepositoryImpl(
    private val dao: TagDao
) : TagRepository {
    override suspend fun saveTag(tag: Tag) {
        val mapped = tag.toEntity()

        dao.insert(mapped)
    }

    override fun getTagsFlow(): Flow<List<Tag>> {
        return dao.selectFlow().map { tags ->
            tags.map { tag ->
                tag.toDomainModel()
            }
        }
    }

    override suspend fun getTags(): List<Tag> {
        return dao.select().map {
            it.toDomainModel()
        }
    }

    override suspend fun getTagsByIds(ids: List<Long>): List<Tag> {
        return dao.selectByIds(ids).map {
            it.toDomainModel()
        }
    }

    override suspend fun deleteTags(tags: List<Tag>) {
        val tagEntities = tags.map {
            it.toEntity()
        }

        tagEntities.forEach {
            dao.delete(it)
        }
    }
}