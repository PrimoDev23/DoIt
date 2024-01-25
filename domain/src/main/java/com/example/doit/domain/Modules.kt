package com.example.doit.domain

import com.example.doit.domain.usecases.DeleteTagsUseCaseImpl
import com.example.doit.domain.usecases.DeleteTodoItemUseCaseImpl
import com.example.doit.domain.usecases.FilterTodoItemsUseCaseImpl
import com.example.doit.domain.usecases.GetTagMappingsFlowUseCaseImpl
import com.example.doit.domain.usecases.GetTagsFlowUseCaseImpl
import com.example.doit.domain.usecases.GetTagsUseCaseImpl
import com.example.doit.domain.usecases.GetTodayTodoItemsFlowUseCaseImpl
import com.example.doit.domain.usecases.GetTodoItemFlowUseCaseImpl
import com.example.doit.domain.usecases.GetTodoItemUseCaseImpl
import com.example.doit.domain.usecases.GetTodoItemsFlowUseCaseImpl
import com.example.doit.domain.usecases.GetTodoListPreferencesUseCaseImpl
import com.example.doit.domain.usecases.SaveTagUseCaseImpl
import com.example.doit.domain.usecases.SaveTodoItemUseCaseImpl
import com.example.doit.domain.usecases.SetHideDoneItemsUseCaseImpl
import com.example.doit.domain.usecases.SetTodoItemSortTypeUseCaseImpl
import com.example.doit.domain.usecases.SortTodoItemsUseCaseImpl
import com.example.doit.domain.usecases.UpdateDoneUseCaseImpl
import com.example.doit.domain.usecases.UpdateSubtaskDoneUseCaseImpl
import com.example.doit.domain.usecases.interfaces.DeleteTagsUseCase
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.FilterTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.GetTagMappingsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTagsUseCase
import com.example.doit.domain.usecases.interfaces.GetTodayTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoItemsFlowUseCase
import com.example.doit.domain.usecases.interfaces.GetTodoListPreferencesUseCase
import com.example.doit.domain.usecases.interfaces.SaveTagUseCase
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import com.example.doit.domain.usecases.interfaces.SetHideDoneItemsUseCase
import com.example.doit.domain.usecases.interfaces.SetTodoItemSortTypeUseCase
import com.example.doit.domain.usecases.interfaces.SortTodoItemsUseCase
import com.example.doit.domain.usecases.interfaces.UpdateDoneUseCase
import com.example.doit.domain.usecases.interfaces.UpdateSubtaskDoneUseCase
import com.example.doit.domain.utils.WorkManagerScheduler
import com.example.doit.domain.utils.interfaces.WorkScheduler
import com.example.doit.domain.worker.NotificationWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::DeleteTagsUseCaseImpl) bind DeleteTagsUseCase::class
    factoryOf(::DeleteTodoItemUseCaseImpl) bind DeleteTodoItemsUseCase::class
    factoryOf(::GetTagsFlowUseCaseImpl) bind GetTagsFlowUseCase::class
    factoryOf(::GetTagsUseCaseImpl) bind GetTagsUseCase::class
    factoryOf(::GetTodayTodoItemsFlowUseCaseImpl) bind GetTodayTodoItemsFlowUseCase::class
    factoryOf(::GetTodoItemsFlowUseCaseImpl) bind GetTodoItemsFlowUseCase::class
    factoryOf(::GetTodoItemUseCaseImpl) bind GetTodoItemUseCase::class
    factoryOf(::GetTodoListPreferencesUseCaseImpl) bind GetTodoListPreferencesUseCase::class
    factoryOf(::SaveTagUseCaseImpl) bind SaveTagUseCase::class
    factoryOf(::SaveTodoItemUseCaseImpl) bind SaveTodoItemUseCase::class
    factoryOf(::SetHideDoneItemsUseCaseImpl) bind SetHideDoneItemsUseCase::class
    factoryOf(::SetTodoItemSortTypeUseCaseImpl) bind SetTodoItemSortTypeUseCase::class
    factoryOf(::UpdateDoneUseCaseImpl) bind UpdateDoneUseCase::class
    factoryOf(::UpdateSubtaskDoneUseCaseImpl) bind UpdateSubtaskDoneUseCase::class
    factoryOf(::GetTodoItemFlowUseCaseImpl) bind GetTodoItemFlowUseCase::class
    factoryOf(::GetTagMappingsFlowUseCaseImpl) bind GetTagMappingsFlowUseCase::class
    factoryOf(::FilterTodoItemsUseCaseImpl) bind FilterTodoItemsUseCase::class
    factoryOf(::SortTodoItemsUseCaseImpl) bind SortTodoItemsUseCase::class
}

val workerModule = module {
    workerOf(::NotificationWorker)
}

val utilsModule = module {
    factoryOf(::WorkManagerScheduler) bind WorkScheduler::class
}