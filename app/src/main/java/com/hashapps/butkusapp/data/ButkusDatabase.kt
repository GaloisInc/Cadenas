package com.hashapps.butkusapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hashapps.butkusapp.data.model.Model
import com.hashapps.butkusapp.data.model.ModelDao
import com.hashapps.butkusapp.data.profile.Profile
import com.hashapps.butkusapp.data.profile.ProfileDao

@Database(entities = [Profile::class, Model::class], version = 3, exportSchema = false)
abstract class ButkusDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    abstract fun modelDao(): ModelDao

    companion object {
        @Volatile
        private var Instance: ButkusDatabase? = null

        fun getDatabase(context: Context): ButkusDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ButkusDatabase::class.java, "butkus_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}