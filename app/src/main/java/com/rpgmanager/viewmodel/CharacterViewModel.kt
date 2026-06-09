package com.rpgmanager.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.rpgmanager.data.database.RPGDatabase
import com.rpgmanager.data.model.Character
import com.rpgmanager.repository.CharacterRepository
import kotlinx.coroutines.launch

class CharacterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CharacterRepository
    private val _groupId = MutableLiveData<Long>()
    private val _searchQuery = MutableLiveData("")

    init {
        val db = RPGDatabase.getDatabase(application)
        repository = CharacterRepository(db.characterDao())
    }

    fun setGroupId(groupId: Long) {
        _groupId.value = groupId
        _searchQuery.value = ""
    }

    val characters: LiveData<List<Character>> = _groupId.switchMap { gId ->
        _searchQuery.switchMap { query ->
            if (query.isNullOrBlank()) repository.getCharactersByGroup(gId)
            else repository.searchCharacters(gId, query)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun insert(character: Character) = viewModelScope.launch {
        repository.insert(character)
    }

    fun update(character: Character) = viewModelScope.launch {
        repository.update(character)
    }

    fun delete(character: Character) = viewModelScope.launch {
        repository.delete(character)
    }

    fun levelUp(id: Long) = viewModelScope.launch {
        repository.levelUp(id)
    }

    fun setAliveStatus(id: Long, alive: Boolean) = viewModelScope.launch {
        repository.setAliveStatus(id, alive)
    }

    suspend fun getCharacterById(id: Long): Character? = repository.getCharacterById(id)
}
