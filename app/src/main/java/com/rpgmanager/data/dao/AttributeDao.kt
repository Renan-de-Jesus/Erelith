package com.rpgmanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rpgmanager.data.model.Attribute

@Dao
interface AttributeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attribute: Attribute): Long

    @Update
    suspend fun update(attribute: Attribute)

    @Delete
    suspend fun delete(attribute: Attribute)

    @Query("SELECT * FROM attributes WHERE characterId = :characterId ORDER BY name ASC")
    fun getAttributesByCharacter(characterId: Long): LiveData<List<Attribute>>

    @Query("SELECT * FROM attributes WHERE id = :id")
    suspend fun getAttributeById(id: Long): Attribute?

    @Query("SELECT * FROM attributes WHERE characterId = :characterId AND name LIKE '%' || :query || '%'")
    fun searchAttributes(characterId: Long, query: String): LiveData<List<Attribute>>

    @Query("DELETE FROM attributes WHERE characterId = :characterId")
    suspend fun deleteAllForCharacter(characterId: Long)
}
