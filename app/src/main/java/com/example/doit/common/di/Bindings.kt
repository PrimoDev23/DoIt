package com.example.doit.common.di

import com.example.doit.data.repositories.TagRepositoryImpl
import com.example.doit.data.repositories.TodoItemRepositoryImpl
import com.example.doit.domain.repositories.TagRepository
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.DeleteTodoItemUseCaseImpl
import com.example.doit.domain.usecases.GetTagsFlowUseCaseImpl
import com.example.doit.domain.usecases.GetTagsUseCaseImpl
import com.example.doit.domain.usecases.GetTodoItemsFlowUseCaseImpl
import com.example.doit.domain.usecases.SaveTagUseCaseImpl
import com.example.doit.domain.usecases.SaveTodoItemUseCaseImpl
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.SaveTagUseCase
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
        impl: TodoItemRepositoryImpl
    ): TodoItemRepository

    @Binds
    abstract fun bindGetTodoItemsFlowUseCase(
        impl: GetTodoItemsFlowUseCaseImpl
    ): GetTodoItemsFlowUseCase

    @Binds
    abstract fun bindSaveTodoItemUseCase(
        impl: SaveTodoItemUseCaseImpl
    ): SaveTodoItemUseCase

    @Binds
    abstract fun bindDeleteTodoItemUseCase(
        impl: DeleteTodoItemUseCaseImpl
    ): DeleteTodoItemsUseCase

    @Binds
    abstract fun bindTagRepository(
        impl: TagRepositoryImpl
    ): TagRepository

    @Binds
    abstract fun bindSaveTagUseCase(
        impl: SaveTagUseCaseImpl
    ): SaveTagUseCase

    @Binds
    abstract fun bindGetTagsFlowUseCase(
        impl: GetTagsFlowUseCaseImpl
    ): GetTagsFlowUseCase

    @Binds
    abstract fun bindGetTagsUseCase(
        impl: GetTagsUseCaseImpl
    ): GetTagsUseCase

}