package com.hashapps.cadenas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hashapps.cadenas.data.model.Model
import com.hashapps.cadenas.data.model.ModelDao
import com.hashapps.cadenas.data.profile.Profile
import com.hashapps.cadenas.data.profile.ProfileDao

@Database(entities = [Profile::class, Model::class], version = 5, exportSchema = false)
abstract class ConfigDatabase : RoomDatabase() {
    abstract fun modelDao(): ModelDao
    abstract fun profileDao(): ProfileDao
    abstract fun configDao() : ConfigDao

    companion object {
        @Volatile
        private var Instance: ConfigDatabase? = null

        fun getDatabase(context: Context): ConfigDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ConfigDatabase::class.java, "cadenas_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}