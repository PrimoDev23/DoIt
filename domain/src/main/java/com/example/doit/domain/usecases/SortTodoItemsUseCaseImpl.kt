package com.example.doit.domain.usecases

import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.models.TodoItemSortType
import com.example.doit.domain.usecases.interfaces.SortTodoItemsUseCase

class SortTodoItemsUseCaseImpl : SortTodoItemsUseCase {
    override fun invoke(items: List<TodoItem>, sortType: TodoItemSortType): List<TodoItem> {
        return when (sortType) {
            TodoItemSortType.ALPHABETICAL -> items.sortedBy {
                it.title.lowercase()
            }

            TodoItemSortType.PRIORITY -> items.sortedByDescending {
                it.priority
            }

            TodoItemSortType.DUE_DATE -> items.sortedByDescending {
                it.dueDate
            }

            TodoItemSortType.CREATION_DATE -> items.sortedByDescending {
                it.creationDateTime
            }
        }
    }
}