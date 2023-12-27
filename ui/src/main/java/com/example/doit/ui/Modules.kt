package com.example.doit.ui

import com.example.doit.ui.viewmodels.AddEntryViewModel
import com.example.doit.ui.viewmodels.CalendarViewModel
import com.example.doit.ui.viewmodels.TagListViewModel
import com.example.doit.ui.viewmodels.TodoDetailViewModel
import com.example.doit.ui.viewmodels.TodoListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AddEntryViewModel)
    viewModelOf(::TagListViewModel)
    viewModelOf(::TodoListViewModel)
    viewModelOf(::CalendarViewModel)
    viewModelOf(::TodoDetailViewModel)
}