package com.example.doit.data.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.doit.data.models.FullTodoItemEntity
import com.example.doit.data.models.TodoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao : BaseDao<TodoItemEntity> {

    @Transaction
    @Query("SELECT * FROM TodoItemEntity")
    fun selectFlow(): Flow<List<FullTodoItemEntity>>

    @Transaction
    @Query("SELECT * FROM TodoItemEntity WHERE dueDate = :date")
    fun selectByDate(date: String): Flow<List<FullTodoItemEntity>>

    @Transaction
    @Query("SELECT * FROM TodoItemEntity WHERE id = :id")
    suspend fun selectById(id: String): FullTodoItemEntity?

    @Transaction
    @Query("SELECT * FROM TodoItemEntity WHERE id = :id")
    fun selectByIdFlow(id: String): Flow<FullTodoItemEntity?>

    @Query("DELETE FROM TodoItemEntity WHERE id = :id")
    suspend fun deleteById(id: String)

}