package com.example.doit.common.di

import android.content.Context
import androidx.room.Room
import com.example.doit.common.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "doit-database"
    ).build()

    @Provides
    fun provideTodoItemDao(database: AppDatabase) = database.todoItemDao()

}