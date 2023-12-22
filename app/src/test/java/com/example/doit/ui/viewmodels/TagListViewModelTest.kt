package com.example.doit.ui.viewmodels

import app.cash.turbine.test
import com.example.doit.CoroutineTestBase
import com.example.doit.data.Tags
import com.example.doit.domain.usecases.interfaces.DeleteTagsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class TagListViewModelTest : CoroutineTestBase() {

    @Test
    fun `init ViewModel`() = runTest {
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        val viewModel = TagListViewModel(
            getTagsFlowUseCase = getTagsFlowUseCase,
            saveTagUseCase = mockk(),
            deleteTagsUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            val state = awaitItem()

            Assert.assertEquals(Tags.tagList, state.items)
        }
    }

    @Test
    fun `state update on tag change`() = runTest {
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()

        val tagFlow = MutableStateFlow(Tags.tagList)

        every { getTagsFlowUseCase.getFlow() } returns tagFlow

        val viewModel = TagListViewModel(
            getTagsFlowUseCase = getTagsFlowUseCase,
            saveTagUseCase = mockk(),
            deleteTagsUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertEquals(Tags.tagList, state.items)

            val newTags = Tags.tagList.subList(1, Tags.tagList.lastIndex)

            tagFlow.emit(newTags)

            state = awaitItem()

            Assert.assertEquals(newTags, state.items)

            tagFlow.emit(Tags.tagList)

            state = awaitItem()

            Assert.assertEquals(Tags.tagList, state.items)
        }
    }

    @Test
    fun `save tag calls use case`() = runTest {
        // This is not possible to check right now
        // Mockk does not support unsigned data types, but compose Color is an inlined ULong
    }

    @Test
    fun `select tag and clear selection`() = runTest {
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        val viewModel = TagListViewModel(
            getTagsFlowUseCase = getTagsFlowUseCase,
            saveTagUseCase = mockk(),
            deleteTagsUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertTrue(state.selectedTags.isEmpty())

            val selectedTag = Tags.tagList.first()

            viewModel.onTagSelected(selectedTag)

            state = awaitItem()

            Assert.assertEquals(listOf(selectedTag), state.selectedTags)

            viewModel.onClearSelectionClicked()

            state = awaitItem()

            Assert.assertTrue(state.selectedTags.isEmpty())
        }
    }

    @Test
    fun `delete tag calls use case`() = runTest {
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val deleteTagsUseCase = mockk<DeleteTagsUseCase>()

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        coEvery { deleteTagsUseCase(any()) } returns Unit

        val viewModel = TagListViewModel(
            getTagsFlowUseCase = getTagsFlowUseCase,
            saveTagUseCase = mockk(),
            deleteTagsUseCase = deleteTagsUseCase
        )

        viewModel.state.test {
            awaitItem()
            awaitItem()

            val tagToDelete = Tags.tagList.first()

            viewModel.onTagSelected(tagToDelete)
            var state = awaitItem()

            Assert.assertTrue(state.selectedTags.contains(tagToDelete))

            viewModel.onDeleteClicked()
            state = awaitItem()

            coVerify { deleteTagsUseCase(listOf(tagToDelete)) }

            Assert.assertTrue(state.selectedTags.isEmpty())
        }
    }

}