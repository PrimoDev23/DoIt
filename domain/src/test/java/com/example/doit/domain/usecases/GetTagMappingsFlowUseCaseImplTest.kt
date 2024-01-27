package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.TagMappingRepository
import com.example.doit.testing.TestBase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import org.junit.Test

class GetTagMappingsFlowUseCaseImplTest : TestBase() {

    @Test
    operator fun invoke() {
        val repo = mockk<TagMappingRepository>()

        every { repo.getTagMappingsFlow() } returns flow { emit(listOf()) }

        val useCase = GetTagMappingsFlowUseCaseImpl(
            tagMappingRepository = repo
        )

        useCase()

        verify { repo.getTagMappingsFlow() }
    }
}