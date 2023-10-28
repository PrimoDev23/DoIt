package com.example.doit.domain.usecases

import android.content.Context
import androidx.work.WorkManager
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.SubtaskRepository
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DeleteTodoItemUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val todoItemRepository: TodoItemRepository,
    private val subtaskRepository: SubtaskRepository
) : DeleteTodoItemsUseCase {
    override suspend fun delete(items: List<TodoItem>) {
        val workManager = WorkManager.getInstance(context)

        items.forEach {
            todoItemRepository.deleteItemById(it.id)
            workManager.cancelAllWorkByTag(it.id)

            subtaskRepository.deleteByParent(it.id)
        }
    }
}