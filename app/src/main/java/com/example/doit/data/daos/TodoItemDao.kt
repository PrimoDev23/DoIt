package com.example.doit.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.doit.data.models.local.TodoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoItemDao : BaseDao<TodoItemEntity> {

    @Query("SELECT * FROM TodoItemEntity WHERE parent = :parent")
    suspend fun select(parent: String): List<TodoItemEntity>

    @Query("SELECT * FROM TodoItemEntity WHERE parent IS NULL")
    suspend fun selectWithoutParent(): List<TodoItemEntity>

    @Query("SELECT * FROM TodoItemEntity WHERE parent = :parent")
    fun selectFlow(parent: String): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM TodoItemEntity WHERE parent IS NULL")
    fun selectWithoutParentFlow(): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM TodoItemEntity WHERE dueDate = :date AND parent = null")
    fun selectByDate(date: String): Flow<List<TodoItemEntity>>

    @Query("SELECT * FROM TodoItemEntity WHERE id = :id")
    suspend fun selectById(id: String): TodoItemEntity?

    @Query("SELECT * FROM TodoItemEntity WHERE tags LIKE '%' || :id || '%'")
    suspend fun selectContainsTagId(id: Long): List<TodoItemEntity>

    @Query("DELETE FROM TodoItemEntity WHERE parent = :parent")
    suspend fun deleteItemsByParent(parent: String)

}