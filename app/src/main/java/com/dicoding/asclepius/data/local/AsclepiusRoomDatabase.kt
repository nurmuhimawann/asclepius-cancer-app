package com.dicoding.asclepius.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Asclepius::class], version = 2, exportSchema = false)
abstract class AsclepiusRoomDatabase : RoomDatabase() {
    abstract fun asclepiusDao(): AsclepiusDao
    companion object {
        @Volatile
        private var INSTANCE: AsclepiusRoomDatabase? = null
        @JvmStatic
        fun getDatabase(context: Context): AsclepiusRoomDatabase {
            if (INSTANCE == null) {
                synchronized(AsclepiusRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AsclepiusRoomDatabase::class.java, "asclepius_database")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE as AsclepiusRoomDatabase
        }
    }
}
