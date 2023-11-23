package com.example.doit.domain.usecases

import android.content.Context
import androidx.work.WorkManager
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.DeleteTodoItemsUseCase

class DeleteTodoItemUseCaseImpl(
    private val context: Context,
    private val todoItemRepository: TodoItemRepository
) : DeleteTodoItemsUseCase {
    override suspend fun delete(items: List<TodoItem>) {
        val workManager = WorkManager.getInstance(context)

        items.forEach {
            todoItemRepository.deleteItemById(it.id)
            workManager.cancelAllWorkByTag(it.id)
        }
    }
}