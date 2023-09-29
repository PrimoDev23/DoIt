package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.Tag

interface DeleteTagsUseCase {
    suspend operator fun invoke(tags: List<Tag>)
}