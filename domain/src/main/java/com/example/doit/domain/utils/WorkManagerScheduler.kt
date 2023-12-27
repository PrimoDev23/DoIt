package com.example.doit.domain.utils

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.doit.domain.utils.interfaces.WorkScheduler
import com.example.doit.domain.worker.NotificationWorker
import java.time.Duration
import java.time.LocalDateTime

class WorkManagerScheduler(
    context: Context
) : WorkScheduler {

    private val workManager = WorkManager.getInstance(context)

    override fun enqueueNotification(
        id: String,
        time: LocalDateTime,
        data: Map<String, Any>
    ) {
        val duration = Duration.between(LocalDateTime.now(), time)

        val workerData = Data.Builder()
            .putAll(data)
            .build()

        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .addTag(id)
            .setInitialDelay(duration)
            .setInputData(workerData)
            .build()

        workManager.enqueue(request)
    }

    override fun cancelById(id: String) {
        workManager.cancelAllWorkByTag(id)
    }
}