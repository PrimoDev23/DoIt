package com.example.doit.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.doit.data.daos.SubtaskDao
import com.example.doit.data.daos.TagDao
import com.example.doit.data.daos.TodoItemDao
import com.example.doit.data.models.local.SubtaskEntity
import com.example.doit.data.models.local.TagEntity
import com.example.doit.data.models.local.TodoItemEntity
import java.time.format.DateTimeFormatter

@Database(
    entities = [
        TodoItemEntity::class,
        TagEntity::class,
        SubtaskEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoItemDao(): TodoItemDao
    abstract fun tagDao(): TagDao
    abstract fun subtaskDao(): SubtaskDao

    companion object {
        const val LIST_SEPARATOR = "|"

        val DATE_FORMATTER = DateTimeFormatter.ISO_DATE
        val DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME
    }
}