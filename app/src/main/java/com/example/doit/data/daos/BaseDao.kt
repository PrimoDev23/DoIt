package com.example.doit.data.daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {
    @Update
    suspend fun update(item: T)

    @Update
    suspend fun update(vararg items: T)

    @Insert
    suspend fun insert(item: T)

    @Insert
    suspend fun insert(vararg items: T)

    @Delete
    suspend fun delete(item: T)

    @Delete
    suspend fun delete(vararg items: T)
}