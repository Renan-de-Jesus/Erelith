package com.rpgmanager.repository

import androidx.lifecycle.LiveData
import com.rpgmanager.data.dao.AttributeDao
import com.rpgmanager.data.model.Attribute

class AttributeRepository(private val attributeDao: AttributeDao) {

    fun getAttributesByCharacter(characterId: Long): LiveData<List<Attribute>> =
        attributeDao.getAttributesByCharacter(characterId)

    fun searchAttributes(characterId: Long, query: String): LiveData<List<Attribute>> =
        attributeDao.searchAttributes(characterId, query)

    suspend fun getAttributeById(id: Long): Attribute? = attributeDao.getAttributeById(id)

    suspend fun insert(attribute: Attribute): Long = attributeDao.insert(attribute)

    suspend fun update(attribute: Attribute) = attributeDao.update(attribute)

    suspend fun delete(attribute: Attribute) = attributeDao.delete(attribute)

    suspend fun deleteAllForCharacter(characterId: Long) =
        attributeDao.deleteAllForCharacter(characterId)
}
