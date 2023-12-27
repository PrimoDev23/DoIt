package com.example.doit.data

import androidx.room.Room
import com.example.doit.data.preferences.TodoListPrefsImpl
import com.example.doit.data.preferences.dataStore
import com.example.doit.data.repositories.SubtaskRepositoryImpl
import com.example.doit.data.repositories.TagRepositoryImpl
import com.example.doit.data.repositories.TodoItemRepositoryImpl
import com.example.doit.domain.preferences.TodoListPrefs
import com.example.doit.domain.repositories.SubtaskRepository
import com.example.doit.domain.repositories.TagRepository
import com.example.doit.domain.repositories.TodoItemRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "doit-database"
        ).build()
    }
    factory { get<AppDatabase>().todoItemDao() }
    factory { get<AppDatabase>().tagDao() }
    factory { get<AppDatabase>().subtaskDao() }
    factory { androidContext().dataStore }
}

val repoModule = module {
    factoryOf(::TodoListPrefsImpl) bind TodoListPrefs::class
    factoryOf(::SubtaskRepositoryImpl) bind SubtaskRepository::class
    factoryOf(::TagRepositoryImpl) bind TagRepository::class
    factoryOf(::TodoItemRepositoryImpl) bind TodoItemRepository::class
}