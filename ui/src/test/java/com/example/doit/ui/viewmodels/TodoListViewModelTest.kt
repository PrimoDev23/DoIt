package com.example.doit.ui.viewmodels

import app.cash.turbine.test
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.models.TodoListPreferences
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodayTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoListPreferencesUseCase
import com.example.doit.domain.usecases.interfaces.SetHideDoneItemsUseCase
import com.example.doit.domain.usecases.interfaces.SetTodoItemSortTypeUseCase
import com.example.doit.domain.usecases.interfaces.UpdateDoneUseCase
import com.example.doit.testing.CoroutineTestBase
import com.example.doit.testing.Tags
import com.example.doit.testing.TodoItems
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate

class TodoListViewModelTest : CoroutineTestBase() {

    @Test
    fun `init ViewModel`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()

        every { getTodoListPreferencesUseCase() } returns flow {
            val preferences = TodoListPreferences(
                sortType = TodoItemSortType.CREATION_DATE,
                hideDoneItems = false
            )

            emit(preferences)
        }

        val todoItemList = TodoItems.todoList
        val todayItems = todoItemList.filter {
            it.dueDate == LocalDate.now()
        }

        every { getTodoItemsFlowUseCase.getItemFlow() } returns flow {
            emit(todoItemList)
        }

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        every { getTodayTodoItemsFlowUseCase() } returns flow {
            emit(todayItems)
        }

        val viewModel = TodoListViewModel(
            getTodoListPreferencesUseCase = getTodoListPreferencesUseCase,
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            getTagsFlowUseCase = getTagsFlowUseCase,
            getTodayTodoItemsFlowUseCase = getTodayTodoItemsFlowUseCase,
            deleteTodoItemsUseCase = mockk(),
            setTodoItemSortTypeUseCase = mockk(),
            setHideDoneItemsUseCase = mockk(),
            updateDoneUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            val state = awaitItem()

            Assert.assertEquals(todoItemList, state.items)
            Assert.assertEquals(Tags.tagList, state.tags)
        }
    }

    @Test
    fun `today filtering`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()

        every { getTodoListPreferencesUseCase() } returns flow {
            val preferences = TodoListPreferences(
                sortType = TodoItemSortType.CREATION_DATE,
                hideDoneItems = false
            )

            emit(preferences)
        }

        val todoItemList = TodoItems.todoList
        val todayItems = todoItemList.filter {
            it.dueDate == LocalDate.now()
        }

        every { getTodoItemsFlowUseCase.getItemFlow() } returns flow {
            emit(todoItemList)
        }

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        every { getTodayTodoItemsFlowUseCase() } returns flow {
            emit(todayItems)
        }

        val viewModel = TodoListViewModel(
            getTodoListPreferencesUseCase = getTodoListPreferencesUseCase,
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            getTagsFlowUseCase = getTagsFlowUseCase,
            getTodayTodoItemsFlowUseCase = getTodayTodoItemsFlowUseCase,
            deleteTodoItemsUseCase = mockk(),
            setTodoItemSortTypeUseCase = mockk(),
            setHideDoneItemsUseCase = mockk(),
            updateDoneUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertFalse(state.todayFilterActive)

            viewModel.onTodayInfoCardClicked(true)

            state = awaitItem()

            Assert.assertEquals(todayItems, state.items)
            Assert.assertTrue(state.todayFilterActive)
        }
    }

    @Test
    fun `today item count updates`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()

        every { getTodoListPreferencesUseCase() } returns flow {
            val preferences = TodoListPreferences(
                sortType = TodoItemSortType.CREATION_DATE,
                hideDoneItems = false
            )

            emit(preferences)
        }

        val todoItemList = TodoItems.todoList
        val todoItemFlow = MutableStateFlow(todoItemList)
        val todayItemFlow = todoItemFlow.map { items ->
            items.filter {
                it.dueDate == LocalDate.now()
            }
        }

        every { getTodoItemsFlowUseCase.getItemFlow() } returns todoItemFlow

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        every { getTodayTodoItemsFlowUseCase() } returns todayItemFlow

        val viewModel = TodoListViewModel(
            getTodoListPreferencesUseCase = getTodoListPreferencesUseCase,
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            getTagsFlowUseCase = getTagsFlowUseCase,
            getTodayTodoItemsFlowUseCase = getTodayTodoItemsFlowUseCase,
            deleteTodoItemsUseCase = mockk(),
            setTodoItemSortTypeUseCase = mockk(),
            setHideDoneItemsUseCase = mockk(),
            updateDoneUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertEquals(1, state.todayUndone)

            todoItemFlow.update { items ->
                items.map {
                    it.copy(done = true)
                }
            }

            state = awaitItem()

            Assert.assertEquals(1, state.todayDone)
        }
    }

    @Test
    fun `onDoneChanged calls usecase`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()
        val updateDoneUseCase = mockk<UpdateDoneUseCase>()

        every { getTodoListPreferencesUseCase() } returns flow {
            val preferences = TodoListPreferences(
                sortType = TodoItemSortType.CREATION_DATE,
                hideDoneItems = false
            )

            emit(preferences)
        }

        val todoItemList = TodoItems.todoList
        val todoItemFlow = MutableStateFlow(todoItemList)
        val todayItemFlow = todoItemFlow.map { items ->
            items.filter {
                it.dueDate == LocalDate.now()
            }
        }

        every { getTodoItemsFlowUseCase.getItemFlow() } returns todoItemFlow

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        every { getTodayTodoItemsFlowUseCase() } returns todayItemFlow

        coEvery { updateDoneUseCase(any(), any()) } returns Unit

        val viewModel = TodoListViewModel(
            getTodoListPreferencesUseCase = getTodoListPreferencesUseCase,
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            getTagsFlowUseCase = getTagsFlowUseCase,
            getTodayTodoItemsFlowUseCase = getTodayTodoItemsFlowUseCase,
            deleteTodoItemsUseCase = mockk(),
            setTodoItemSortTypeUseCase = mockk(),
            setHideDoneItemsUseCase = mockk(),
            updateDoneUseCase = updateDoneUseCase
        )

        viewModel.onDoneChanged(TodoItems.todoItemOne, true)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDoneUseCase(TodoItems.todoItemOne, true) }

        viewModel.onDoneChanged(TodoItems.todoItemOne, false)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { updateDoneUseCase(TodoItems.todoItemOne, false) }
    }

    @Test
    fun `delete item`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()
        val deleteTodoItemsUseCase = mockk<DeleteTodoItemsUseCase>()

        every { getTodoListPreferencesUseCase() } returns flow {
            val preferences = TodoListPreferences(
                sortType = TodoItemSortType.CREATION_DATE,
                hideDoneItems = false
            )

            emit(preferences)
        }

        val todoItemList = TodoItems.todoList
        val todoItemFlow = MutableStateFlow(todoItemList)
        val todayItemFlow = todoItemFlow.map { items ->
            items.filter {
                it.dueDate == LocalDate.now()
            }
        }

        every { getTodoItemsFlowUseCase.getItemFlow() } returns todoItemFlow

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        every { getTodayTodoItemsFlowUseCase() } returns todayItemFlow

        coEvery { deleteTodoItemsUseCase.delete(any()) } returns Unit

        val viewModel = TodoListViewModel(
            getTodoListPreferencesUseCase = getTodoListPreferencesUseCase,
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            getTagsFlowUseCase = getTagsFlowUseCase,
            getTodayTodoItemsFlowUseCase = getTodayTodoItemsFlowUseCase,
            deleteTodoItemsUseCase = deleteTodoItemsUseCase,
            setTodoItemSortTypeUseCase = mockk(),
            setHideDoneItemsUseCase = mockk(),
            updateDoneUseCase = mockk()
        )

        viewModel.onDeleteClicked()
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { deleteTodoItemsUseCase.delete(emptyList()) }

        viewModel.onItemSelected(TodoItems.todoItemOne)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.onDeleteClicked()
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { deleteTodoItemsUseCase.delete(listOf(TodoItems.todoItemOne)) }
    }

    @Test
    fun `select item`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()

        every { getTodoListPreferencesUseCase() } returns flow {
            val preferences = TodoListPreferences(
                sortType = TodoItemSortType.CREATION_DATE,
                hideDoneItems = false
            )

            emit(preferences)
        }

        val todoItemList = TodoItems.todoList
        val todayItems = todoItemList.filter {
            it.dueDate == LocalDate.now()
        }

        every { getTodoItemsFlowUseCase.getItemFlow() } returns flow {
            emit(todoItemList)
        }

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        every { getTodayTodoItemsFlowUseCase() } returns flow {
            emit(todayItems)
        }

        val viewModel = TodoListViewModel(
            getTodoListPreferencesUseCase = getTodoListPreferencesUseCase,
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            getTagsFlowUseCase = getTagsFlowUseCase,
            getTodayTodoItemsFlowUseCase = getTodayTodoItemsFlowUseCase,
            deleteTodoItemsUseCase = mockk(),
            setTodoItemSortTypeUseCase = mockk(),
            setHideDoneItemsUseCase = mockk(),
            updateDoneUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertEquals(emptyList<TodoItem>(), state.selectedItems)

            viewModel.onItemSelected(TodoItems.todoItemOne)

            state = awaitItem()

            Assert.assertTrue(state.selectedItems.contains(TodoItems.todoItemOne))

            viewModel.onClearSelectionClicked()

            state = awaitItem()

            Assert.assertFalse(state.selectedItems.contains(TodoItems.todoItemOne))
        }
    }

    @Test
    fun `select and reset tag filter`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()

        every { getTodoListPreferencesUseCase() } returns flow {
            val preferences = TodoListPreferences(
                sortType = TodoItemSortType.CREATION_DATE,
                hideDoneItems = false
            )

            emit(preferences)
        }

        val todoItemList = TodoItems.todoList
        val todayItems = todoItemList.filter {
            it.dueDate == LocalDate.now()
        }

        every { getTodoItemsFlowUseCase.getItemFlow() } returns flow {
            emit(todoItemList)
        }

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        every { getTodayTodoItemsFlowUseCase() } returns flow {
            emit(todayItems)
        }

        val viewModel = TodoListViewModel(
            getTodoListPreferencesUseCase = getTodoListPreferencesUseCase,
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            getTagsFlowUseCase = getTagsFlowUseCase,
            getTodayTodoItemsFlowUseCase = getTodayTodoItemsFlowUseCase,
            deleteTodoItemsUseCase = mockk(),
            setTodoItemSortTypeUseCase = mockk(),
            setHideDoneItemsUseCase = mockk(),
            updateDoneUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertEquals(emptyList<Tag>(), state.selectedTags)

            val tag = Tags.tagOne

            viewModel.onTagClicked(Tags.tagOne)

            state = awaitItem()

            Assert.assertEquals(listOf(tag), state.selectedTags)

            viewModel.onResetTagsClicked()

            state = awaitItem()

            Assert.assertEquals(emptyList<Tag>(), state.selectedTags)
        }
    }

    @Test
    fun `select and reset priority filter`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()

        every { getTodoListPreferencesUseCase() } returns flow {
            val preferences = TodoListPreferences(
                sortType = TodoItemSortType.CREATION_DATE,
                hideDoneItems = false
            )

            emit(preferences)
        }

        val todoItemList = TodoItems.todoList
        val todayItems = todoItemList.filter {
            it.dueDate == LocalDate.now()
        }

        every { getTodoItemsFlowUseCase.getItemFlow() } returns flow {
            emit(todoItemList)
        }

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        every { getTodayTodoItemsFlowUseCase() } returns flow {
            emit(todayItems)
        }

        val viewModel = TodoListViewModel(
            getTodoListPreferencesUseCase = getTodoListPreferencesUseCase,
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            getTagsFlowUseCase = getTagsFlowUseCase,
            getTodayTodoItemsFlowUseCase = getTodayTodoItemsFlowUseCase,
            deleteTodoItemsUseCase = mockk(),
            setTodoItemSortTypeUseCase = mockk(),
            setHideDoneItemsUseCase = mockk(),
            updateDoneUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertEquals(emptyList<Priority>(), state.selectedPriorities)

            val priority = Priority.HIGH

            viewModel.onPriorityClicked(priority)

            state = awaitItem()

            Assert.assertEquals(listOf(priority), state.selectedPriorities)

            viewModel.onResetPrioritiesClicked()

            state = awaitItem()

            Assert.assertEquals(emptyList<Priority>(), state.selectedPriorities)
        }
    }

    @Test
    fun `update settings calls usecases`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()
        val setTodoItemSortTypeUseCase = mockk<SetTodoItemSortTypeUseCase>()
        val setHideDoneItemsUseCase = mockk<SetHideDoneItemsUseCase>()

        every { getTodoListPreferencesUseCase() } returns flow {
            emit(
                TodoListPreferences(
                    sortType = TodoItemSortType.CREATION_DATE,
                    hideDoneItems = false
                )
            )
        }

        val todoItemList = TodoItems.todoList
        val todayItems = todoItemList.filter {
            it.dueDate == LocalDate.now()
        }

        every { getTodoItemsFlowUseCase.getItemFlow() } returns flow {
            emit(todoItemList)
        }

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        every { getTodayTodoItemsFlowUseCase() } returns flow {
            emit(todayItems)
        }

        coEvery { setTodoItemSortTypeUseCase(any()) } returns Unit

        coEvery { setHideDoneItemsUseCase(any()) } returns Unit

        val viewModel = TodoListViewModel(
            getTodoListPreferencesUseCase = getTodoListPreferencesUseCase,
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            getTagsFlowUseCase = getTagsFlowUseCase,
            getTodayTodoItemsFlowUseCase = getTodayTodoItemsFlowUseCase,
            deleteTodoItemsUseCase = mockk(),
            setTodoItemSortTypeUseCase = setTodoItemSortTypeUseCase,
            setHideDoneItemsUseCase = setHideDoneItemsUseCase,
            updateDoneUseCase = mockk()
        )

        viewModel.onSortTypeChanged(TodoItemSortType.ALPHABETICAL)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { setTodoItemSortTypeUseCase(TodoItemSortType.ALPHABETICAL) }

        viewModel.onHideDoneItemsChanged(true)
        dispatcher.scheduler.advanceUntilIdle()

        coVerify { setHideDoneItemsUseCase(true) }
    }

    @Test
    fun `update settings rebuilds state`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()

        val preferencesFlow = MutableStateFlow(
            TodoListPreferences(
                sortType = TodoItemSortType.CREATION_DATE,
                hideDoneItems = false
            )
        )

        every { getTodoListPreferencesUseCase() } returns preferencesFlow

        val todoItemList = TodoItems.todoList
        val todayItems = todoItemList.filter {
            it.dueDate == LocalDate.now()
        }

        every { getTodoItemsFlowUseCase.getItemFlow() } returns flow {
            emit(todoItemList)
        }

        every { getTagsFlowUseCase.getFlow() } returns flow {
            emit(Tags.tagList)
        }

        every { getTodayTodoItemsFlowUseCase() } returns flow {
            emit(todayItems)
        }

        val viewModel = TodoListViewModel(
            getTodoListPreferencesUseCase = getTodoListPreferencesUseCase,
            getTodoItemsFlowUseCase = getTodoItemsFlowUseCase,
            getTagsFlowUseCase = getTagsFlowUseCase,
            getTodayTodoItemsFlowUseCase = getTodayTodoItemsFlowUseCase,
            deleteTodoItemsUseCase = mockk(),
            setTodoItemSortTypeUseCase = mockk(),
            setHideDoneItemsUseCase = mockk(),
            updateDoneUseCase = mockk()
        )

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertEquals(TodoItemSortType.CREATION_DATE, state.sortType)
            Assert.assertFalse(state.hideDoneItems)

            preferencesFlow.update {
                it.copy(
                    sortType = TodoItemSortType.ALPHABETICAL,
                    hideDoneItems = true
                )
            }

            state = awaitItem()

            Assert.assertEquals(TodoItemSortType.ALPHABETICAL, state.sortType)
            Assert.assertTrue(state.hideDoneItems)
        }
    }

}