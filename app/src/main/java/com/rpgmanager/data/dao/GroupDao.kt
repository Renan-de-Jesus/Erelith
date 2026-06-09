package com.rpgmanager.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rpgmanager.data.model.Group

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: Group): Long

    @Update
    suspend fun update(group: Group)

    @Delete
    suspend fun delete(group: Group)

    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    fun getAllGroups(): LiveData<List<Group>>

    @Query("SELECT * FROM groups WHERE id = :id")
    suspend fun getGroupById(id: Long): Group?

    @Query("SELECT * FROM groups WHERE name LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchGroups(query: String): LiveData<List<Group>>

    @Query("UPDATE groups SET isActive = :isActive WHERE id = :id")
    suspend fun setActiveStatus(id: Long, isActive: Boolean)
}
