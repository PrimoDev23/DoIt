package com.example.doit.domain.utils.interfaces

import java.time.LocalDateTime

interface WorkScheduler {

    fun enqueueNotification(
        id: String,
        time: LocalDateTime,
        data: Map<String, Any>
    )

    fun cancelById(id: String)

}