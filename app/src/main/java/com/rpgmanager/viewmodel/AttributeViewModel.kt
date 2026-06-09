package com.rpgmanager.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.rpgmanager.data.database.RPGDatabase
import com.rpgmanager.data.model.Attribute
import com.rpgmanager.repository.AttributeRepository
import kotlinx.coroutines.launch

class AttributeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AttributeRepository
    private val _characterId = MutableLiveData<Long>()
    private val _searchQuery = MutableLiveData("")

    init {
        val db = RPGDatabase.getDatabase(application)
        repository = AttributeRepository(db.attributeDao())
    }

    fun setCharacterId(characterId: Long) {
        _characterId.value = characterId
        _searchQuery.value = ""
    }

    val attributes: LiveData<List<Attribute>> = _characterId.switchMap { cId ->
        _searchQuery.switchMap { query ->
            if (query.isNullOrBlank()) repository.getAttributesByCharacter(cId)
            else repository.searchAttributes(cId, query)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insert(attribute: Attribute) = viewModelScope.launch {
        repository.insert(attribute)
    }

    fun update(attribute: Attribute) = viewModelScope.launch {
        repository.update(attribute)
    }

    fun delete(attribute: Attribute) = viewModelScope.launch {
        repository.delete(attribute)
    }

    suspend fun getAttributeById(id: Long): Attribute? = repository.getAttributeById(id)
}
