package com.example.doit.domain.usecases

import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.usecases.interfaces.FilterTodoItemsUseCase

class FilterTodoItemsUseCaseImpl : FilterTodoItemsUseCase {
    override fun invoke(
        items: List<TodoItem>,
        tags: List<Tag>,
        priorities: List<Priority>,
        hideDoneItems: Boolean
    ): List<TodoItem> {
        return items.filter {
            if (hideDoneItems && it.done) {
                return@filter false
            }

            if (tags.isNotEmpty() && !it.tags.any(tags::contains)) {
                return@filter false
            }

            return@filter priorities.isEmpty() || priorities.contains(it.priority)
        }
    }
}