package com.rpgmanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(
            entity = Group::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId")]
)
data class Character(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val groupId: Long,
    val name: String,
    val playerName: String,
    val race: String,
    val characterClass: String,
    val level: Int = 1,
    val backstory: String = "",
    val imagePath: String? = null,
    val isAlive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
