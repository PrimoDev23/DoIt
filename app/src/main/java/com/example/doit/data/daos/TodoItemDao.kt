package com.example.doit.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.doit.data.models.local.TodoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao : BaseDao<TodoItemEntity> {

    @Query("SELECT * FROM TodoItemEntity")
    fun select(): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM TodoItemEntity WHERE tags LIKE '%' || :id || '%'")
    suspend fun selectContainsTagId(id: Long): List<TodoItemEntity>

}