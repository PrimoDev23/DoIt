package com.example.doit.domain.usecases

import com.example.doit.domain.models.Tag
import com.example.doit.domain.repositories.TagRepository
import com.example.doit.domain.usecases.interfaces.GetTagsUseCase

class GetTagsUseCaseImpl(
    private val repo: TagRepository
) : GetTagsUseCase {
    override suspend operator fun invoke(): List<Tag> {
        return repo.getTags()
    }
}