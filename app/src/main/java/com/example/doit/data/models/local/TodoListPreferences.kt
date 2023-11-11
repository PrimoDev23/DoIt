package com.example.doit.data.models.local

import com.example.doit.domain.models.TodoItemSortType

class TodoListPreferences(
    val sortType: TodoItemSortType,
    val hideDoneItems: Boolean
)