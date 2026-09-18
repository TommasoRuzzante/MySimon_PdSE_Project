package com.myapp.mysimon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the User entity.
 * Handles database operations for the user profile.
 */
@Dao
interface UserDao {
    /**
     * Retrieves the single local user profile.
     * Returns a Flow that emits the User object whenever it is updated in the database.
     */
    @Query("SELECT * FROM user WHERE id = 1")
    fun getUser(): Flow<User?>

    /**
     * Inserts a new user or updates the existing one.
     * Uses OnConflictStrategy.REPLACE to overwrite the existing profile (ID 1).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: User)
}
