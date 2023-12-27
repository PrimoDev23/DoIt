package com.example.doit.domain.models

data class TodoListPreferences(
    val sortType: TodoItemSortType,
    val hideDoneItems: Boolean
)