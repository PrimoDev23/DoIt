package com.example.doit.domain.repositories

import com.example.doit.domain.models.Tag

interface TagMappingRepository {
    suspend fun saveTagMappings(parent: String, tags: List<Tag>)
}