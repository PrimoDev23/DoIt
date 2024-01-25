package com.example.doit.domain.usecases.interfaces

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.models.TodoItemSortType

interface SortTodoItemsUseCase {
    operator fun invoke(items: List<TodoItem>, sortType: TodoItemSortType): List<TodoItem>
}