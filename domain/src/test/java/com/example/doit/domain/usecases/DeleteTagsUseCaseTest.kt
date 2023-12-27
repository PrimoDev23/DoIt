package com.example.doit.domain.usecases

import com.example.doit.domain.repositories.TagRepository
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.testing.Tags
import com.example.doit.testing.TestBase
import com.example.doit.testing.TodoItems
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteTagsUseCaseTest : TestBase() {

    private val todoListTagOne = listOf(
        TodoItems.todoItemOne,
        TodoItems.todoItemThree
    )

    private val todoListTagOneWithout = listOf(
        TodoItems.todoItemOne.copy(tags = TodoItems.todoItemOne.tags - Tags.tagOne),
        TodoItems.todoItemThree.copy(tags = TodoItems.todoItemThree.tags - Tags.tagOne)
    )

    @Test
    fun invoke() = runTest {
        val tagRepo = mockk<TagRepository>()
        val todoRepo = mockk<TodoItemRepository>()

        val tags = listOf(Tags.tagOne)
        val ids = tags.map { it.id }

        coEvery { todoRepo.getItemsWithTagIds(ids) } returns todoListTagOne
        coEvery { todoRepo.saveTodoItems(any()) } returns Unit
        coEvery { tagRepo.deleteTags(any()) } returns Unit

        val useCase = DeleteTagsUseCaseImpl(
            tagRepository = tagRepo,
            todoItemRepository = todoRepo
        )

        useCase.invoke(tags)

        coVerify { todoRepo.getItemsWithTagIds(ids) }

        coVerify { todoRepo.saveTodoItems(todoListTagOneWithout) }

        coVerify { tagRepo.deleteTags(tags) }
    }

}