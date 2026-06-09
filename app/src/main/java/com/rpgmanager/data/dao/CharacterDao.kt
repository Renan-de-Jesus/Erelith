package com.rpgmanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rpgmanager.data.model.Character

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: Character): Long

    @Update
    suspend fun update(character: Character)

    @Delete
    suspend fun delete(character: Character)

    @Query("SELECT * FROM characters WHERE groupId = :groupId ORDER BY name ASC")
    fun getCharactersByGroup(groupId: Long): LiveData<List<Character>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Long): Character?

    @Query("""
        SELECT * FROM characters 
        WHERE groupId = :groupId 
        AND (name LIKE '%' || :query || '%' OR playerName LIKE '%' || :query || '%')
        ORDER BY name ASC
    """)
    fun searchCharacters(groupId: Long, query: String): LiveData<List<Character>>

    @Query("UPDATE characters SET level = level + 1 WHERE id = :id")
    suspend fun levelUp(id: Long)

    @Query("UPDATE characters SET isAlive = :alive WHERE id = :id")
    suspend fun setAliveStatus(id: Long, alive: Boolean)
}
