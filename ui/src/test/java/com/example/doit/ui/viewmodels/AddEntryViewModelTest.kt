package com.example.doit.ui.viewmodels

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Subtask
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import com.example.doit.testing.CoroutineTestBase
import com.example.doit.testing.Subtasks
import com.example.doit.testing.Tags
import com.example.doit.testing.TodoItems
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class AddEntryViewModelTest : CoroutineTestBase() {

    private fun buildSavedStateHandle() = SavedStateHandle().apply {
        this["id"] = TodoItems.todoItemOne.id

        // Only used in UI
        this["edit"] = false
    }

    @Test
    fun `init ViewModel`() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        val item = TodoItems.todoItemOne

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns item

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            awaitItem()
            val state = awaitItem()

            val selectedTags = Tags.tagList.map {
                it.copy(selected = true)
            }

            Assert.assertEquals(selectedTags, state.tags)

            with(item) {
                Assert.assertEquals(title, state.title)
                Assert.assertEquals(description, state.description)
                Assert.assertEquals(subtasks, state.subtasks)
                Assert.assertEquals(priority, state.priority)
                Assert.assertEquals(dueDate, state.dueDate)
                Assert.assertEquals(notificationDateTime, state.notificationDateTime)
            }
        }
    }

    @Test
    fun onBackClicked() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        val item = TodoItems.todoItemOne

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns item

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        viewModel.events.test {
            viewModel.onBackClicked()

            val event = awaitItem()

            Assert.assertTrue(event is AddEntryEvent.PopBackStack)
        }
    }

    @Test
    fun `onDeleteClicked - existing`() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()
        val deleteTodoItemsUseCase = mockk<DeleteTodoItemsUseCase>()

        val item = TodoItems.todoItemOne

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns item
        coEvery { deleteTodoItemsUseCase.delete(any()) } returns Unit

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = deleteTodoItemsUseCase
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.onDeleteClicked()
            dispatcher.scheduler.advanceUntilIdle()

            coVerify { deleteTodoItemsUseCase.delete(listOf(item)) }

            val event = awaitItem()

            Assert.assertTrue(event is AddEntryEvent.PopBackStack)
        }
    }

    @Test
    fun `onDeleteClicked - new`() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()
        val deleteTodoItemsUseCase = mockk<DeleteTodoItemsUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null
        coEvery { deleteTodoItemsUseCase.delete(any()) } returns Unit

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = deleteTodoItemsUseCase
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.onDeleteClicked()
            dispatcher.scheduler.advanceUntilIdle()

            coVerify(inverse = true) { deleteTodoItemsUseCase.delete(any()) }

            val event = awaitItem()

            Assert.assertTrue(event is AddEntryEvent.PopBackStack)
        }
    }

    @Test
    fun `onSaveClicked - invalid`() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()
        val saveTodoItemUseCase = mockk<SaveTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null
        coEvery { saveTodoItemUseCase.save(any()) } returns Unit

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = saveTodoItemUseCase,
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.onSaveClicked()
            dispatcher.scheduler.advanceUntilIdle()

            coVerify(inverse = true) { saveTodoItemUseCase.save(any()) }

            this.expectNoEvents()
        }
    }

    @Test
    fun `onSaveClicked - valid`() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()
        val saveTodoItemUseCase = mockk<SaveTodoItemUseCase>()

        val item = TodoItems.todoItemOne

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns item
        coEvery { saveTodoItemUseCase.save(any()) } returns Unit

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = saveTodoItemUseCase,
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.events.test {
            viewModel.onSaveClicked()
            dispatcher.scheduler.advanceUntilIdle()

            val selectedTags = item.tags.map {
                it.copy(selected = true)
            }
            val selectedTagsItem = item.copy(tags = selectedTags)

            coVerify { saveTodoItemUseCase.save(selectedTagsItem) }

            val event = awaitItem()

            Assert.assertTrue(event is AddEntryEvent.PopBackStack)
        }
    }

    @Test
    fun onTitleChanged() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()

            Assert.assertTrue(state.title.isEmpty())

            val title = "Title"
            viewModel.onTitleChanged(title)

            state = awaitItem()

            Assert.assertEquals(title, state.title)
        }
    }

    @Test
    fun onDescriptionChanged() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()

            Assert.assertTrue(state.description.isEmpty())

            val description = "Desc"
            viewModel.onDescriptionChanged(description)

            state = awaitItem()

            Assert.assertEquals(description, state.description)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun `date handling`() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()

            Assert.assertEquals(null, state.dueDate)

            val dueDateInstant = Instant.now()
            val millis = dueDateInstant.toEpochMilli()

            viewModel.datePickerState.setSelection(millis)
            viewModel.onDateConfirmed()

            state = awaitItem()

            val dueDate = dueDateInstant.atZone(ZoneId.systemDefault()).toLocalDate()
            Assert.assertEquals(dueDate, state.dueDate)

            viewModel.onDateCleared()

            state = awaitItem()

            Assert.assertEquals(null, state.dueDate)
        }
    }

    @Test
    fun onNotificationDateTimePicked() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()

            Assert.assertEquals(null, state.notificationDateTime)

            val dateTime = LocalDateTime.now()

            viewModel.onNotificationDateTimePicked(dateTime)

            state = awaitItem()

            Assert.assertEquals(dateTime, state.notificationDateTime)
        }
    }

    @Test
    fun onTagClicked() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()

            val anyTagSelected = state.tags.any { it.selected }

            Assert.assertFalse(anyTagSelected)

            viewModel.onTagClicked(Tags.tagOne)

            state = awaitItem()

            val selectedTagCount = state.tags.count { it.selected }

            Assert.assertEquals(1, selectedTagCount)
        }
    }

    @Test
    fun onPriorityChanged() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()

            Assert.assertEquals(Priority.NONE, state.priority)

            viewModel.onPriorityChanged(Priority.HIGH)

            state = awaitItem()

            Assert.assertEquals(Priority.HIGH, state.priority)
        }
    }

    @Test
    fun onSubtaskAdded() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            var state = awaitItem()

            Assert.assertEquals(emptyList<Subtask>(), state.subtasks)

            val subtask = Subtasks.subtaskOne

            viewModel.onSubtaskAdded(subtask)

            state = awaitItem()

            Assert.assertEquals(listOf(subtask), state.subtasks)
        }
    }

    @Test
    fun onSubtaskTitleUpdated() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()
            val subtask = Subtasks.subtaskOne

            viewModel.onSubtaskAdded(subtask)

            var state = awaitItem()

            Assert.assertEquals(listOf(subtask), state.subtasks)

            val updatedSubtask = subtask.copy(title = "New title")

            viewModel.onSubtaskTitleUpdated(subtask, updatedSubtask.title)

            state = awaitItem()

            Assert.assertEquals(listOf(updatedSubtask), state.subtasks)
        }
    }

    @Test
    fun onSubtaskDoneChanged() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()
            val subtask = Subtasks.subtaskOne

            viewModel.onSubtaskAdded(subtask)

            var state = awaitItem()

            Assert.assertEquals(listOf(subtask), state.subtasks)

            val updatedSubtask = subtask.copy(done = true)

            viewModel.onSubtaskDoneChanged(subtask, updatedSubtask.done)

            state = awaitItem()

            Assert.assertEquals(listOf(updatedSubtask), state.subtasks)
        }
    }

    @Test
    fun onSubtaskRemoveClicked() = runTest {
        val getTagsUseCase = mockk<GetTagsUseCase>()
        val getTodoItemUseCase = mockk<GetTodoItemUseCase>()

        coEvery { getTagsUseCase() } returns Tags.tagList
        coEvery { getTodoItemUseCase(any()) } returns null

        val viewModel = AddEntryViewModel(
            savedStateHandle = buildSavedStateHandle(),
            saveTodoItemUseCase = mockk(),
            getTagsUseCase = getTagsUseCase,
            getTodoItemUseCase = getTodoItemUseCase,
            deleteTodoItemsUseCase = mockk()
        )

        dispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            awaitItem()
            val subtask = Subtasks.subtaskOne

            viewModel.onSubtaskAdded(subtask)

            var state = awaitItem()

            Assert.assertEquals(listOf(subtask), state.subtasks)

            viewModel.onSubtaskRemoveClicked(subtask)

            state = awaitItem()

            Assert.assertEquals(emptyList<Subtask>(), state.subtasks)
        }
    }

}