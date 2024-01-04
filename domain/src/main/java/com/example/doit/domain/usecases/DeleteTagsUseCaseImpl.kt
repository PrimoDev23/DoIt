package com.example.doit.domain.usecases

import com.example.doit.domain.models.Tag
import com.example.doit.domain.repositories.TagRepository
import com.example.doit.domain.usecases.interfaces.DeleteTagsUseCase

class DeleteTagsUseCaseImpl(
    private val tagRepository: TagRepository
) : DeleteTagsUseCase {
    override suspend operator fun invoke(tags: List<Tag>) {
        tagRepository.deleteTags(tags)
    }
}