package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.TagRepository
import com.example.doit.testing.Tags
import com.example.doit.testing.TestBase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteTagsUseCaseTest : TestBase() {

    @Test
    fun invoke() = runTest {
        val tagRepo = mockk<TagRepository>()

        val tags = listOf(Tags.tagOne)

        coEvery { tagRepo.deleteTags(any()) } returns Unit

        val useCase = DeleteTagsUseCaseImpl(
            tagRepository = tagRepo
        )

        useCase.invoke(tags)

        coVerify { tagRepo.deleteTags(tags) }
    }

}