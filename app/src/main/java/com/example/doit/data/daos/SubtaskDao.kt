package com.example.doit.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.doit.data.models.local.SubtaskEntity

@Dao
interface SubtaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<SubtaskEntity>)

    @Query("DELETE FROM SubtaskEntity WHERE parent = :parent")
    suspend fun deleteByParent(parent: String)

    @Query("SELECT * FROM SubtaskEntity WHERE parent = :parent")
    suspend fun selectByParent(parent: String): List<SubtaskEntity>

    @Transaction
    suspend fun insertOnSave(parent: String, tasks: List<SubtaskEntity>) {
        deleteByParent(parent)
        insert(tasks)
    }

}