package com.myapp.mysimon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Data Access Object (DAO) for the Game entity
@Dao
interface GameDao {
    @Insert
    suspend fun insert(game: Game)

    @Query("SELECT * FROM game")
    fun getAll() : Flow<List<Game>>

    @Query("SELECT * FROM game WHERE id = :id")
    suspend fun selectById(id: Int) : Game

    @Query("SELECT MAX(counter) FROM game")
    suspend fun getBestScore() : Int

    @Query("SELECT COUNT(*) FROM game")
    suspend fun getGamesPlayed() : Int
}