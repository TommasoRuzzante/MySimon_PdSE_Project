package com.myapp.mysimon.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room

/**
 * Main database class for the application using Room.
 * It serves as the primary access point for the persisted data.
 * The database manages two entities: Game (history) and User (profile).
 */
@Database(entities = [Game::class, User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    // Abstract methods to obtain the Data Access Objects (DAOs)
    abstract fun gameDao(): GameDao
    abstract fun userDao(): UserDao

    companion object {
        // Singleton instance to prevent multiple instances of the database opening at the same time
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton instance of the database.
         * If it doesn't exist, it creates it in a thread-safe manner.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "game-db"
                    )
                    // Allows Room to destructively recreate database tables if migrations are missing.
                    // version 2 adds the User table.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
