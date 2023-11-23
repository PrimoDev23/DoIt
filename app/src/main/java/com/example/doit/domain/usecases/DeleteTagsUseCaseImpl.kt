package com.example.doit.domain.usecases

import com.example.doit.domain.models.Tag
import com.example.doit.domain.repositories.TagRepository
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.DeleteTagsUseCase

class DeleteTagsUseCaseImpl(
    private val tagRepository: TagRepository,
    private val todoItemRepository: TodoItemRepository
) : DeleteTagsUseCase {
    override suspend operator fun invoke(tags: List<Tag>) {
        val ids = tags.map { it.id }
        val todoItems = todoItemRepository.getItemsWithTagIds(ids)

        val updatedTodoItems = todoItems.map {
            val newTags = it.tags.filter { tag ->
                !tags.contains(tag)
            }

            it.copy(tags = newTags)
        }

        todoItemRepository.saveTodoItems(updatedTodoItems)

        tagRepository.deleteTags(tags)
    }
}