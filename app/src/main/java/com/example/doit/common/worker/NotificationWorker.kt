package com.example.doit.common.worker

import android.Manifest
import android.app.Notification
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.doit.DoItApplication
import com.example.doit.R
import com.example.doit.domain.usecases.interfaces.GetTodoItemUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    val getTodoItemUseCase: GetTodoItemUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val id = inputData.getString(ITEM_ID_KEY) ?: return Result.failure()

        val item = getTodoItemUseCase(id) ?: return Result.failure()

        val notification = Notification.Builder(context, DoItApplication.ITEM_CHANNEL_NAME)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(item.title)
            .apply {
                if (item.description.isNotBlank()) {
                    setContentText(item.description)
                }
            }
            .build()

        with(NotificationManagerCompat.from(context)) {
            val notificationId = System.currentTimeMillis().toInt()
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                PackageManager.PERMISSION_GRANTED
            }

            if (permission == PackageManager.PERMISSION_GRANTED) {
                notify(notificationId, notification)

                return Result.success()
            }
        }

        return Result.retry()
    }

    companion object {
        const val ITEM_ID_KEY = "ITEM_ID"
    }
}