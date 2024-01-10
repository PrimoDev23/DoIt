package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TagMapping
import kotlinx.coroutines.flow.Flow

interface GetTagMappingsFlowUseCase {
    operator fun invoke(): Flow<List<TagMapping>>
}