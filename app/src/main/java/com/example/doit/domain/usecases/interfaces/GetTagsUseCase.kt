package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.Tag

interface GetTagsUseCase {
    suspend operator fun invoke(): List<Tag>
}