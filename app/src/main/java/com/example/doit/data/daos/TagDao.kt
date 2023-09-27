package com.example.doit.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.doit.data.models.local.TagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao : BaseDao<TagEntity> {
    @Query("SELECT * FROM TagEntity")
    fun selectFlow(): Flow<List<TagEntity>>

    @Query("SELECT * FROM TagEntity")
    suspend fun select(): List<TagEntity>

    @Query("SELECT * FROM TagEntity WHERE id IN (:ids)")
    suspend fun selectByIds(ids: List<Long>): List<TagEntity>
}