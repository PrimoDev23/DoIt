package com.example.doit.common.di

import com.example.doit.common.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideTodoItemDao(database: AppDatabase) = database.todoItemDao()

}