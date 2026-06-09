package com.rpgmanager.repository

import androidx.lifecycle.LiveData
import com.rpgmanager.data.dao.GroupDao
import com.rpgmanager.data.model.Group

class GroupRepository(private val groupDao: GroupDao) {

    fun getAllGroups(): LiveData<List<Group>> = groupDao.getAllGroups()

    fun searchGroups(query: String): LiveData<List<Group>> = groupDao.searchGroups(query)

    suspend fun getGroupById(id: Long): Group? = groupDao.getGroupById(id)

    suspend fun insert(group: Group): Long = groupDao.insert(group)

    suspend fun update(group: Group) = groupDao.update(group)

    suspend fun delete(group: Group) = groupDao.delete(group)

    suspend fun setActiveStatus(id: Long, isActive: Boolean) =
        groupDao.setActiveStatus(id, isActive)
}
