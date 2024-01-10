package com.example.doit.domain.repositories

import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TagMapping
import kotlinx.coroutines.flow.Flow

interface TagMappingRepository {
    suspend fun saveTagMappings(parent: String, tags: List<Tag>)
    fun getTagMappingsFlow(): Flow<List<TagMapping>>
}