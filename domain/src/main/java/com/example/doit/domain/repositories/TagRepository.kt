package com.example.doit.domain.repositories

import com.example.doit.domain.models.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun saveTag(tag: Tag)
    fun getTagsFlow(): Flow<List<Tag>>
    suspend fun getTags(): List<Tag>
    suspend fun getTagsByIds(ids: List<Long>): List<Tag>
    suspend fun deleteTags(tags: List<Tag>)
}