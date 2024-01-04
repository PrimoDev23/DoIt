package com.example.doit.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.doit.data.daos.SubtaskDao
import com.example.doit.data.daos.TagDao
import com.example.doit.data.daos.TagMappingDao
import com.example.doit.data.daos.TodoItemDao
import com.example.doit.data.models.SubtaskEntity
import com.example.doit.data.models.TagEntity
import com.example.doit.data.models.TagMappingEntity
import com.example.doit.data.models.TodoItemEntity

@Database(
    entities = [
        TodoItemEntity::class,
        TagEntity::class,
        SubtaskEntity::class,
        TagMappingEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoItemDao(): TodoItemDao
    abstract fun tagDao(): TagDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun tagMappingDao(): TagMappingDao
}