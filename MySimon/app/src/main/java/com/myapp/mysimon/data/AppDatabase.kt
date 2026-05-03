package com.myapp.mysimon.data
import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room

@Database(entities = arrayOf(Game::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    // Singleton Pattern: Create a single instance of the database accessible by the whole application
    companion object {
        // Volatile annotation ensures that the value of INSTANCE is always up-to-date and the same to all execution threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Function to get the database instance
        fun getDatabase(context: Context): AppDatabase {
            // If the INSTANCE is not null, then return it, otherwise create a new database instance
            // Creating the database with synchronized ensures only one thread of execution at a time can enter this block of code
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "game-db"
                    ).build()
                INSTANCE = instance // Save the new instance in the INSTANCE variable
                instance // The result of the synchronized block
            }
        }
    }
}