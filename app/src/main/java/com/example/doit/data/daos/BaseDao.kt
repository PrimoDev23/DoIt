package com.example.doit.data.daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg items: T)

    @Delete
    suspend fun delete(item: T)

    @Delete
    suspend fun delete(vararg items: T)
}