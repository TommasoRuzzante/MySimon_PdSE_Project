package com.myapp.mysimon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the Game entity.
 * Provides methods for interacting with the 'game' table in the database.
 */
@Dao
interface GameDao {
    /**
     * Inserts a new game record into the database.
     */
    @Insert
    suspend fun insert(game: Game)

    /**
     * Retrieves all games from the database as a Flow of lists.
     * The Flow will emit updated lists whenever the table changes.
     */
    @Query("SELECT * FROM game")
    fun getAll() : Flow<List<Game>>

    /**
     * Selects a single game by its unique ID.
     */
    @Query("SELECT * FROM game WHERE id = :id")
    suspend fun selectById(id: Int) : Game

    /**
     * Retrieves the highest 'counter' (score) recorded across all games.
     */
    @Query("SELECT MAX(counter) FROM game")
    suspend fun getBestScore() : Int

    /**
     * Counts the total number of game sessions stored in the database.
     */
    @Query("SELECT COUNT(*) FROM game")
    suspend fun getGamesPlayed() : Int
}