package com.example.doit.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.doit.data.daos.TodoItemDao
import com.example.doit.data.models.local.TodoItemEntity

@Database(
    entities = [
        TodoItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoItemDao(): TodoItemDao
}