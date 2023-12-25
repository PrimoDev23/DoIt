package com.example.doit.domain.usecases

import com.example.doit.TestBase
import com.example.doit.data.Tags
import com.example.doit.domain.repositories.TagRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class GetTagsUseCaseTest : TestBase() {

    @Test
    fun invoke() = runTest {
        val repo = mockk<TagRepository>()

        coEvery { repo.getTags() } returns Tags.tagList

        val useCase = GetTagsUseCaseImpl(
            repo = repo
        )

        val tags = useCase.invoke()

        coVerify { repo.getTags() }

        Assert.assertEquals(Tags.tagList, tags)
    }

}