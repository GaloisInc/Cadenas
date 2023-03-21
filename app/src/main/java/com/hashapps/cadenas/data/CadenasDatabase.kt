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
abstract class CadenasDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    abstract fun modelDao(): ModelDao

    companion object {
        @Volatile
        private var Instance: CadenasDatabase? = null

        fun getDatabase(context: Context): CadenasDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, CadenasDatabase::class.java, "cadenas_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}