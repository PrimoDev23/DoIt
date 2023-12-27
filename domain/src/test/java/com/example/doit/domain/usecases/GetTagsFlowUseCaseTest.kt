package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.TagRepository
import com.example.doit.testing.Tags
import com.example.doit.testing.TestBase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import org.junit.Assert
import org.junit.Test

class GetTagsFlowUseCaseTest : TestBase() {

    @Test
    fun invoke() {
        val repo = mockk<TagRepository>()

        val tagFlow = flow {
            emit(Tags.tagList)
        }

        every { repo.getTagsFlow() } returns tagFlow

        val useCase = GetTagsFlowUseCaseImpl(
            repo = repo
        )

        val flow = useCase.getFlow()

        Assert.assertEquals(tagFlow, flow)

        verify { repo.getTagsFlow() }
    }

}