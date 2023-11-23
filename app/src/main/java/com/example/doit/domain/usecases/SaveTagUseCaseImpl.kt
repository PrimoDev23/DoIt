package com.example.doit.domain.usecases

import androidx.compose.ui.graphics.Color
import com.example.doit.domain.models.Tag
import com.example.doit.domain.repositories.TagRepository
import com.example.doit.domain.usecases.interfaces.SaveTagUseCase

class SaveTagUseCaseImpl(
    private val repo: TagRepository
) : SaveTagUseCase {
    override suspend fun save(title: String, color: Color) {
        val tag = Tag(
            id = 0,
            title = title,
            color = color
        )

        repo.saveTag(tag)
    }
}