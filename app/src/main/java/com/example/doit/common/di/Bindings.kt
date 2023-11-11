package com.example.doit.common.di

import com.example.doit.data.repositories.PreferencesRepositoryImpl
import com.example.doit.data.repositories.SubtaskRepositoryImpl
import com.example.doit.data.repositories.TagRepositoryImpl
import com.example.doit.data.repositories.TodoItemRepositoryImpl
import com.example.doit.domain.repositories.PreferencesRepository
import com.example.doit.domain.repositories.SubtaskRepository
import com.example.doit.domain.repositories.TagRepository
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.DeleteTagsUseCaseImpl
import com.example.doit.domain.usecases.DeleteTodoItemUseCaseImpl
import com.example.doit.domain.usecases.GetTagsFlowUseCaseImpl
import com.example.doit.domain.usecases.GetTagsUseCaseImpl
import com.example.doit.domain.usecases.GetTodayTodoItemsFlowUseCaseImpl
import com.example.doit.domain.usecases.GetTodoItemUseCaseImpl
import com.example.doit.domain.usecases.GetTodoItemsFlowUseCaseImpl
import com.example.doit.domain.usecases.GetTodoListPreferencesUseCaseImpl
import com.example.doit.domain.usecases.SaveTagUseCaseImpl
import com.example.doit.domain.usecases.SaveTodoItemUseCaseImpl
import com.example.doit.domain.usecases.SetHideDoneItemsUseCaseImpl
import com.example.doit.domain.usecases.SetTodoItemSortTypeUseCaseImpl
import com.example.doit.domain.usecases.interfaces.DeleteTagsUseCase
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsUseCase
import com.example.doit.domain.usecases.interfaces.GetTodayTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoListPreferencesUseCase
import com.example.doit.domain.usecases.interfaces.SaveTagUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import com.example.doit.domain.usecases.interfaces.SetHideDoneItemsUseCase
import com.example.doit.domain.usecases.interfaces.SetTodoItemSortTypeUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("UNUSED")
@Module
@InstallIn(SingletonComponent::class)
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

    @Binds
    abstract fun bindDeleteTagsUseCase(
        impl: DeleteTagsUseCaseImpl
    ): DeleteTagsUseCase

    @Binds
    abstract fun bindGetTodoItemUseCase(
        impl: GetTodoItemUseCaseImpl
    ): GetTodoItemUseCase

    @Binds
    abstract fun bindGetTodayTodoItemsFlowUseCase(
        impl: GetTodayTodoItemsFlowUseCaseImpl
    ): GetTodayTodoItemsFlowUseCase

    @Binds
    abstract fun bindSubtaskRepository(
        impl: SubtaskRepositoryImpl
    ): SubtaskRepository

    @Binds
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository

    @Binds
    abstract fun bindGetTodoItemSortTypeFlowUseCase(
        impl: GetTodoListPreferencesUseCaseImpl
    ): GetTodoListPreferencesUseCase

    @Binds
    abstract fun bindSetTodoItemSortTypeUseCase(
        impl: SetTodoItemSortTypeUseCaseImpl
    ): SetTodoItemSortTypeUseCase

    @Binds
    abstract fun bindSetHideDoneItemsUseCase(
        impl: SetHideDoneItemsUseCaseImpl
    ): SetHideDoneItemsUseCase

}