package com.rpgmanager.repository

import androidx.lifecycle.LiveData
import com.rpgmanager.data.dao.CharacterDao
import com.rpgmanager.data.model.Character

class CharacterRepository(private val characterDao: CharacterDao) {

    fun getCharactersByGroup(groupId: Long): LiveData<List<Character>> =
        characterDao.getCharactersByGroup(groupId)

    fun searchCharacters(groupId: Long, query: String): LiveData<List<Character>> =
        characterDao.searchCharacters(groupId, query)

    suspend fun getCharacterById(id: Long): Character? = characterDao.getCharacterById(id)

    suspend fun insert(character: Character): Long = characterDao.insert(character)

    suspend fun update(character: Character) = characterDao.update(character)

    suspend fun delete(character: Character) = characterDao.delete(character)

    suspend fun levelUp(id: Long) = characterDao.levelUp(id)

    suspend fun setAliveStatus(id: Long, alive: Boolean) = characterDao.setAliveStatus(id, alive)
}
