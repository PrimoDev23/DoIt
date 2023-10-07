package com.example.doit.domain.models

import androidx.annotation.StringRes
import com.example.doit.R

enum class TodoItemSortType(@StringRes val title: Int) {
    ALPHABETICAL(R.string.sort_alphabetical),
    PRIORITY(R.string.sort_priority),
    DUE_DATE(R.string.sort_due_date)
}