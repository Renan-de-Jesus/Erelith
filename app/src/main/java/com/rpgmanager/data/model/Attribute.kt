package com.rpgmanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a single attribute on a character.
 * The Master defines the attribute name and type; values are stored as strings
 * and interpreted by [attributeType].
 */
@Entity(
    tableName = "attributes",
    foreignKeys = [
        ForeignKey(
            entity = Character::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("characterId")]
)
data class Attribute(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val characterId: Long,
    /** e.g. "Força", "Inteligência", "HP Máximo" */
    val name: String,
    /** Current value stored as string to support text, numbers and booleans */
    val value: String,
    /** "NUMBER", "TEXT", or "BOOLEAN" */
    val attributeType: String = "NUMBER",
    /** Optional note added by the master when updating */
    val note: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
