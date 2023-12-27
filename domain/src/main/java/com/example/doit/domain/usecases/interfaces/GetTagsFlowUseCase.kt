package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.Tag
import kotlinx.coroutines.flow.Flow

interface GetTagsFlowUseCase {
    fun getFlow(): Flow<List<Tag>>
}