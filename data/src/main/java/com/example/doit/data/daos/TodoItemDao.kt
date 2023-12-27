package com.example.doit.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.doit.data.models.TodoItemEntity
import com.example.doit.data.models.TodoItemWithSubtasksEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao : BaseDao<TodoItemEntity> {

    @Transaction
    @Query("SELECT * FROM TodoItemEntity")
    fun selectFlow(): Flow<List<TodoItemWithSubtasksEntity>>

    @Transaction
    @Query("SELECT * FROM TodoItemEntity WHERE dueDate = :date")
    fun selectByDate(date: String): Flow<List<TodoItemWithSubtasksEntity>>

    @Transaction
    @Query("SELECT * FROM TodoItemEntity WHERE id = :id")
    suspend fun selectById(id: String): TodoItemWithSubtasksEntity?

    @Transaction
    @Query("SELECT * FROM TodoItemEntity WHERE id = :id")
    fun selectByIdFlow(id: String): Flow<TodoItemWithSubtasksEntity?>

    @Transaction
    @Query("SELECT * FROM TodoItemEntity WHERE tags LIKE '%' || :id || '%'")
    suspend fun selectContainsTagId(id: Long): List<TodoItemWithSubtasksEntity>

    @Query("DELETE FROM TodoItemEntity WHERE id = :id")
    suspend fun deleteById(id: String)

}