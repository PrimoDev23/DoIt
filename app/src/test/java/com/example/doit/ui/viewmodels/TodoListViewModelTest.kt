package com.example.doit.ui.viewmodels

import app.cash.turbine.test
import com.example.doit.CoroutineTestBase
import com.example.doit.data.Tags
import com.example.doit.data.TodoItems
import com.example.doit.data.models.local.TodoListPreferences
import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodayTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoListPreferencesUseCase
import com.example.doit.domain.usecases.interfaces.SetHideDoneItemsUseCase
import com.example.doit.domain.usecases.interfaces.SetTodoItemSortTypeUseCase
import com.example.doit.domain.usecases.interfaces.UpdateDoneUseCase
import io.mockk.coEvery
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
    fun `today item count`() = runTest {
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

        coEvery { updateDoneUseCase(any(), any()) } answers {
            val item = firstArg<TodoItem>()
            val newState = secondArg<Boolean>()

            todoItemFlow.update {
                val items = it.toMutableList()

                val index = items.indexOf(item)

                if (index == -1) {
                    return@update it
                }

                val newItem = item.copy(done = newState)
                items[index] = newItem

                items
            }
        }

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

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertEquals(1, state.todayUndone)

            viewModel.onDoneChanged(TodoItems.fullTodoItem, true)

            state = awaitItem()

            Assert.assertEquals(1, state.todayDone)
        }
    }

    @Test
    fun `update done status`() = runTest {
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

        coEvery { updateDoneUseCase(any(), any()) } answers {
            val item = firstArg<TodoItem>()
            val newState = secondArg<Boolean>()

            todoItemFlow.update {
                val items = it.toMutableList()

                val index = items.indexOf(item)

                if (index == -1) {
                    return@update it
                }

                val newItem = item.copy(done = newState)
                items[index] = newItem

                items
            }
        }

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

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertTrue(state.items.none { it.done })

            viewModel.onDoneChanged(TodoItems.fullTodoItem, true)

            state = awaitItem()

            Assert.assertEquals(1, state.items.count { it.done })
        }
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

        coEvery { deleteTodoItemsUseCase.delete(any()) } answers {
            val items = firstArg<List<TodoItem>>()

            todoItemFlow.update {
                it.filterNot { item ->
                    items.contains(item)
                }
            }
        }

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

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertEquals(TodoItems.todoList, state.items)

            viewModel.onItemSelected(TodoItems.fullTodoItem)

            state = awaitItem()

            Assert.assertEquals(1, state.selectedItems.size)

            viewModel.onDeleteClicked()

            state = awaitItem()

            Assert.assertEquals(listOf(TodoItems.todoList[1]), state.items)
        }
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

            viewModel.onItemSelected(TodoItems.fullTodoItem)

            state = awaitItem()

            Assert.assertTrue(state.selectedItems.contains(TodoItems.fullTodoItem))

            viewModel.onClearSelectionClicked()

            state = awaitItem()

            Assert.assertFalse(state.selectedItems.contains(TodoItems.fullTodoItem))
        }
    }

    @Test
    fun `select tag`() = runTest {
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

            Assert.assertEquals(TodoItems.todoList, state.items)

            val tag = Tags.tagList[0]

            viewModel.onTagFilterClicked(tag)

            state = awaitItem()

            Assert.assertEquals(tag, state.selectedTag)
        }
    }

    @Test
    fun `select priority`() = runTest {
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

            Assert.assertEquals(TodoItems.todoList, state.items)

            viewModel.onPrioritySelected(Priority.HIGH)

            state = awaitItem()

            Assert.assertEquals(Priority.HIGH, state.selectedPriority)
        }
    }

    @Test
    fun `update settings`() = runTest {
        val getTodoListPreferencesUseCase = mockk<GetTodoListPreferencesUseCase>()
        val getTodoItemsFlowUseCase = mockk<GetTodoItemsFlowUseCase>()
        val getTagsFlowUseCase = mockk<GetTagsFlowUseCase>()
        val getTodayTodoItemsFlowUseCase = mockk<GetTodayTodoItemsFlowUseCase>()
        val setTodoItemSortTypeUseCase = mockk<SetTodoItemSortTypeUseCase>()
        val setHideDoneItemsUseCase = mockk<SetHideDoneItemsUseCase>()

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

        coEvery { setTodoItemSortTypeUseCase(any()) } answers {
            val sortType = firstArg<TodoItemSortType>()

            preferencesFlow.update {
                it.copy(sortType = sortType)
            }
        }

        coEvery { setHideDoneItemsUseCase(any()) } answers {
            val hideDoneItems = firstArg<Boolean>()

            preferencesFlow.update {
                it.copy(hideDoneItems = hideDoneItems)
            }
        }

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

        viewModel.state.test {
            awaitItem()
            var state = awaitItem()

            Assert.assertEquals(TodoItemSortType.CREATION_DATE, state.sortType)
            Assert.assertFalse(state.hideDoneItems)

            viewModel.onSortTypeChanged(TodoItemSortType.ALPHABETICAL)

            state = awaitItem()

            Assert.assertEquals(TodoItemSortType.ALPHABETICAL, state.sortType)

            viewModel.onHideDoneItemsChanged(true)

            state = awaitItem()

            Assert.assertTrue(state.hideDoneItems)
        }
    }

}