package com.example.doit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.doit.common.R
import com.example.doit.common.constants.NotificationConstants
import com.example.doit.data.databaseModule
import com.example.doit.data.repoModule
import com.example.doit.domain.useCaseModule
import com.example.doit.domain.utilsModule
import com.example.doit.domain.workerModule
import com.example.doit.ui.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin

class DoItApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DoItApplication)
            workManagerFactory()

            modules(
                viewModelModule,
                databaseModule,
                repoModule,
                useCaseModule,
                workerModule,
                utilsModule
            )
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.registerItemNotificationChannel()
    }

    private fun NotificationManager.registerItemNotificationChannel() {
        val name = getString(R.string.notification_item_channel_name)
        val descriptionText = getString(R.string.notification_item_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(NotificationConstants.ITEM_CHANNEL_NAME, name, importance).apply {
                description = descriptionText
            }

        this.createNotificationChannel(channel)
    }

}