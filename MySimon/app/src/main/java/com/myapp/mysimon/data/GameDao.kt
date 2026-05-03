package com.myapp.mysimon.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameDao {
    @Insert
    fun insert(game: Game)

    @Query("SELECT * FROM game")
    fun getAll() : List<Game>

    @Query("SELECT * FROM game WHERE id = :id")
    fun selectById(id: Int) : Game
}