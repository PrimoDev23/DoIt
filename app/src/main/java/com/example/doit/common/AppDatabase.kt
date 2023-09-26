package com.example.doit.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.doit.data.daos.TagDao
import com.example.doit.data.daos.TodoItemDao
import com.example.doit.data.models.local.TagEntity
import com.example.doit.data.models.local.TodoItemEntity

@Database(
    entities = [
        TodoItemEntity::class,
        TagEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoItemDao(): TodoItemDao
    abstract fun tagDao(): TagDao
}