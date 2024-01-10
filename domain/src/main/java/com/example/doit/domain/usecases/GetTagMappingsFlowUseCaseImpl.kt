package com.example.doit.domain.usecases

import com.example.doit.domain.models.TagMapping
import com.example.doit.domain.repositories.TagMappingRepository
import com.example.doit.domain.usecases.interfaces.GetTagMappingsFlowUseCase
import kotlinx.coroutines.flow.Flow

class GetTagMappingsFlowUseCaseImpl(
    private val tagMappingRepository: TagMappingRepository
) : GetTagMappingsFlowUseCase {
    override fun invoke(): Flow<List<TagMapping>> {
        return tagMappingRepository.getTagMappingsFlow()
    }
}