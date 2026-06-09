package com.rpgmanager.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.rpgmanager.data.database.RPGDatabase
import com.rpgmanager.data.model.Group
import com.rpgmanager.repository.GroupRepository
import kotlinx.coroutines.launch

class GroupViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GroupRepository
    private val _searchQuery = MutableLiveData("")

    init {
        val db = RPGDatabase.getDatabase(application)
        repository = GroupRepository(db.groupDao())
    }

    val allGroups: LiveData<List<Group>> = repository.getAllGroups()

    val filteredGroups: LiveData<List<Group>> = _searchQuery.switchMap { query ->
        if (query.isNullOrBlank()) repository.getAllGroups()
        else repository.searchGroups(query)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insert(group: Group) = viewModelScope.launch {
        repository.insert(group)
    }

    fun update(group: Group) = viewModelScope.launch {
        repository.update(group)
    }

    fun delete(group: Group) = viewModelScope.launch {
        repository.delete(group)
    }

    fun setActiveStatus(id: Long, isActive: Boolean) = viewModelScope.launch {
        repository.setActiveStatus(id, isActive)
    }

    suspend fun getGroupById(id: Long): Group? = repository.getGroupById(id)
}
