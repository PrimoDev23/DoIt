package com.example.doit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DoItApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.registerItemNotificationChannel()
    }

    private fun NotificationManager.registerItemNotificationChannel() {
        val name = getString(R.string.notification_item_channel_name)
        val descriptionText = getString(R.string.notification_item_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(ITEM_CHANNEL_NAME, name, importance).apply {
            description = descriptionText
        }

        this.createNotificationChannel(channel)
    }

    companion object {
        const val ITEM_CHANNEL_NAME = "TodoItemNotifications"
    }

}