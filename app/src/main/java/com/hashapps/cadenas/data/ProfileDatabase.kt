package com.hashapps.cadenas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database storing Cadenas messaging profiles.
 */
@Database(entities = [Profile::class], version = 7, exportSchema = false)
abstract class ProfileDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var Instance: ProfileDatabase? = null

        /**
         * Synchronous access to the [ProfileDatabase], which exposes the DAOs
         * used to interact with the tables.
         *
         * @return The [ProfileDatabase] instance, which is created the first
         * time this method is called.
         */
        fun getDatabase(context: Context): ProfileDatabase {
            return Instance ?: synchronized(this) {
                if (Instance == null) {
                    Room.databaseBuilder(context, ProfileDatabase::class.java, "cadenas_database")
                        .build()
                        .also { Instance = it }
                } else {
                    return Instance as ProfileDatabase
                }
            }
        }
    }
}