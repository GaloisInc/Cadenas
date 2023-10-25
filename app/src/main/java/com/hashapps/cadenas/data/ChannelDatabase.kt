package com.hashapps.cadenas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database storing Cadenas messaging channels.
 */
@Database(entities = [Channel::class], version = 9, exportSchema = false)
abstract class ChannelDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao

    companion object {
        @Volatile
        private var Instance: ChannelDatabase? = null

        /**
         * Synchronous access to the [ChannelDatabase], which exposes the DAOs
         * used to interact with the tables.
         *
         * @return The [ChannelDatabase] instance, which is created the first
         * time this method is called.
         */
        fun getDatabase(context: Context): ChannelDatabase {
            return Instance ?: synchronized(this) {
                if (Instance == null) {
                    Room.databaseBuilder(context, ChannelDatabase::class.java, "cadenas_database")
                        .build()
                        .also { Instance = it }
                } else {
                    return Instance as ChannelDatabase
                }
            }
        }
    }
}