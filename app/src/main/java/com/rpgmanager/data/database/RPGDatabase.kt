package com.rpgmanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rpgmanager.data.dao.AttributeDao
import com.rpgmanager.data.dao.CharacterDao
import com.rpgmanager.data.dao.GroupDao
import com.rpgmanager.data.model.Attribute
import com.rpgmanager.data.model.Character
import com.rpgmanager.data.model.Group

@Database(
    entities = [Group::class, Character::class, Attribute::class],
    version = 1,
    exportSchema = false
)
abstract class RPGDatabase : RoomDatabase() {

    abstract fun groupDao(): GroupDao
    abstract fun characterDao(): CharacterDao
    abstract fun attributeDao(): AttributeDao

    companion object {
        @Volatile
        private var INSTANCE: RPGDatabase? = null

        fun getDatabase(context: Context): RPGDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RPGDatabase::class.java,
                    "rpg_manager_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
