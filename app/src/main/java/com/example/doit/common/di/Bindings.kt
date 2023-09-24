package com.example.doit.common.di

import com.example.doit.data.repositories.TodoItemRepositoryImpl
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.GetTodoItemsFlowUseCaseImpl
import com.example.doit.domain.usecases.SaveTodoItemUseCaseImpl
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Suppress("UNUSED")
@Module
@InstallIn(ViewModelComponent::class)
abstract class Bindings {

    @Binds
    abstract fun bindTodoItemRepo(
        repo: TodoItemRepositoryImpl
    ): TodoItemRepository

    @Binds
    abstract fun bindGetTodoItemsFlowUseCase(
        repo: GetTodoItemsFlowUseCaseImpl
    ): GetTodoItemsFlowUseCase

    @Binds
    abstract fun bindSaveTodoItemUseCase(
        repo: SaveTodoItemUseCaseImpl
    ): SaveTodoItemUseCase

}