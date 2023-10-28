package com.example.doit.domain.usecases

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.doit.common.worker.NotificationWorker
import com.example.doit.domain.models.TodoItem
import com.example.doit.domain.repositories.SubtaskRepository
import com.example.doit.domain.repositories.TodoItemRepository
import com.example.doit.domain.usecases.interfaces.SaveTodoItemUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

class SaveTodoItemUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val todoItemRepository: TodoItemRepository,
    private val subtaskRepository: SubtaskRepository
) : SaveTodoItemUseCase {

    override suspend fun save(item: TodoItem) {
        val workManager = WorkManager.getInstance(context)

        todoItemRepository.saveTodoItem(item)
        subtaskRepository.saveSubtasksForParent(item.id, item.subtasks)

        if (item.notificationDateTime != null) {
            val now = LocalDateTime.now()

            if (item.notificationDateTime.isAfter(now)) {
                val duration = Duration.between(now, item.notificationDateTime)

                val data = Data.Builder()
                    .putString(NotificationWorker.ITEM_ID_KEY, item.id)
                    .build()

                val request = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .addTag(item.id)
                    .setInitialDelay(duration)
                    .setInputData(data)
                    .build()

                workManager.cancelAllWorkByTag(item.id)
                workManager.enqueue(request)
            }
        } else {
            workManager.cancelAllWorkByTag(item.id)
        }
    }

}