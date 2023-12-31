package com.example.doit.domain.usecases

import com.example.doit.domain.models.Tag
import com.example.doit.domain.repositories.TagRepository
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import kotlinx.coroutines.flow.Flow

class GetTagsFlowUseCaseImpl(
    private val repo: TagRepository
) : GetTagsFlowUseCase {
    override fun getFlow(): Flow<List<Tag>> {
        return repo.getTagsFlow()
    }
}