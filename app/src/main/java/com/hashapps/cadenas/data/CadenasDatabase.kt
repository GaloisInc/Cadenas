package com.hashapps.cadenas.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hashapps.cadenas.data.channels.Channel
import com.hashapps.cadenas.data.channels.ChannelDao

/**
 * Room database storing Cadenas messaging channels.
 */
@Database(entities = [Channel::class], version = 9, exportSchema = false)
abstract class CadenasDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao

    companion object {
        @Volatile
        private var Instance: CadenasDatabase? = null

        /**
         * Synchronous access to the [CadenasDatabase], which exposes the DAOs
         * used to interact with the tables.
         *
         * @return The [CadenasDatabase] instance, which is created the first
         * time this method is called.
         */
        fun getDatabase(context: Context): CadenasDatabase {
            return Instance ?: synchronized(this) {
                if (Instance == null) {
                    Room.databaseBuilder(context, CadenasDatabase::class.java, "cadenas_database")
                        .build()
                        .also { Instance = it }
                } else {
                    return Instance as CadenasDatabase
                }
            }
        }
    }
}