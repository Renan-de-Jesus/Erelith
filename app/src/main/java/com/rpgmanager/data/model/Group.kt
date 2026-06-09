package com.rpgmanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class Group(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val masterName: String,
    val imagePath: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
