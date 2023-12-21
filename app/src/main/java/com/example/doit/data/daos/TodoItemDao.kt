package com.example.doit.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.doit.data.models.local.TodoItemEntity
import com.example.doit.data.models.local.TodoItemWithSubtasksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao : BaseDao<TodoItemEntity> {

    @Query("SELECT * FROM TodoItemEntity")
    fun selectFlow(): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM TodoItemEntity WHERE dueDate = :date")
    fun selectByDate(date: String): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM TodoItemEntity WHERE id = :id")
    suspend fun selectById(id: String): TodoItemEntity?

    @Transaction
    @Query("SELECT * FROM TodoItemEntity WHERE id = :id")
    fun selectByIdFlow(id: String): Flow<TodoItemWithSubtasksEntity?>

    @Query("SELECT * FROM TodoItemEntity WHERE tags LIKE '%' || :id || '%'")
    suspend fun selectContainsTagId(id: Long): List<TodoItemEntity>

    @Query("DELETE FROM TodoItemEntity WHERE id = :id")
    suspend fun deleteById(id: String)

}