package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.Priority
import com.example.doit.domain.models.Tag
import com.example.doit.domain.models.TodoItem

interface FilterTodoItemsUseCase {
    operator fun invoke(
        items: List<TodoItem>,
        tags: List<Tag>,
        priorities: List<Priority>,
        hideDoneItems: Boolean
    ): List<TodoItem>
}