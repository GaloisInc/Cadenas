package com.hashapps.cadenas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Profile::class], version = 7, exportSchema = false)
abstract class ProfileDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var Instance: ProfileDatabase? = null

        fun getDatabase(context: Context): ProfileDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ProfileDatabase::class.java, "cadenas_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}