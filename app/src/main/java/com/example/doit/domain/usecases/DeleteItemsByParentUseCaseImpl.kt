package com.example.doit.domain.usecases

import android.content.Context
import androidx.work.WorkManager
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.DeleteItemsByParentUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DeleteItemsByParentUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: TodoItemRepository
) : DeleteItemsByParentUseCase {
    override suspend fun invoke(parent: String) {
        val workManager = WorkManager.getInstance(context)

        repo.getItems(parent).forEach { child ->
            invoke(child.id)

            repo.deleteItemById(child.id)
            workManager.cancelAllWorkByTag(child.id)
        }
    }
}