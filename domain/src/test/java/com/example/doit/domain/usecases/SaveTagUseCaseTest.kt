package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.TagRepository
import com.example.doit.testing.Tags
import com.example.doit.testing.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveTagUseCaseTest : TestBase() {

    @Test
    fun save() = runTest {
        val repo = mockk<TagRepository>()

        coEvery { repo.saveTag(any()) } returns Unit

        val useCase = SaveTagUseCaseImpl(
            repo = repo
        )

        val tag = Tags.tagOne

        useCase.save(tag.title, tag.color)

        coVerify { repo.saveTag(tag) }
    }
}