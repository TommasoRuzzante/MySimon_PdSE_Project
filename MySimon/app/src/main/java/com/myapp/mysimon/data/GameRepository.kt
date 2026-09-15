package com.myapp.mysimon.data

import kotlinx.coroutines.flow.Flow

// Repository class that manage the access to the database
// This class is used in the activities and in the view model as the interface to the database
class GameRepository(private val gameDao: GameDao) {
    // Use the DAO to insert a new game into the database
    suspend fun insert(game: Game) {
        gameDao.insert(game)
    }

    // Use the DAO to get all games from the database
    fun getAllGames() : Flow<List<Game>> {
        return gameDao.getAll()
    }

    // Use the DAO to get a specific game from the database
    suspend fun selectGame(id: Int) : Game {
        return gameDao.selectById(id)
    }

    // Use the DAO to get the best score from the database
    suspend fun getBestScore() : Int {
        return gameDao.getBestScore()
    }

    // Use the DAO to get the number of games played from the database
    suspend fun getGamesPlayed() : Int {
        return gameDao.getGamesPlayed()
    }
}