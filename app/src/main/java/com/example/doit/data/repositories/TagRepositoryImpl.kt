package com.example.doit.data.repositories

import com.example.doit.data.daos.TagDao
import com.example.doit.data.mappers.TagMapper
import com.example.doit.domain.models.Tag
import com.example.doit.domain.repositories.TagRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TagRepositoryImpl(
    private val dao: TagDao,
    private val mapper: TagMapper
) : TagRepository {
    override suspend fun saveTag(tag: Tag) {
        val mapped = mapper.mapBack(tag)

        dao.insert(mapped)
    }

    override fun getTagsFlow(): Flow<List<Tag>> {
        return dao.selectFlow().map { tags ->
            tags.map { tag ->
                mapper.map(tag)
            }
        }
    }

    override suspend fun getTags(): List<Tag> {
        return dao.select().map {
            mapper.map(it)
        }
    }

    override suspend fun getTagsByIds(ids: List<Long>): List<Tag> {
        return dao.selectByIds(ids).map {
            mapper.map(it)
        }
    }

    override suspend fun deleteTags(tags: List<Tag>) {
        val tagEntities = tags.map {
            mapper.mapBack(it)
        }

        tagEntities.forEach {
            dao.delete(it)
        }
    }
}