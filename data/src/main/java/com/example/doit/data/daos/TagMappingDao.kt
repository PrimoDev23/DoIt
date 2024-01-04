package com.example.doit.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.doit.data.models.TagMappingEntity

@Dao
interface TagMappingDao {
    @Insert
    suspend fun insertTagMapping(entities: List<TagMappingEntity>)

    @Query("DELETE FROM TagMappingEntity WHERE itemId IN (:id)")
    suspend fun deleteTagMappingByItemId(id: List<String>)

    @Transaction
    suspend fun deleteAndInsertTagMappings(entities: List<TagMappingEntity>) {
        val ids = entities.map { it.itemId }

        deleteTagMappingByItemId(ids)
        insertTagMapping(entities)
    }
}