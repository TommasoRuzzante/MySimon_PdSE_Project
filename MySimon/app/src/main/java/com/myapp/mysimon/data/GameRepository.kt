package com.myapp.mysimon.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository class that manages access to the Room database.
 * It acts as an intermediary between the ViewModels and the Data Access Objects (DAOs),
 * providing a clean API for the rest of the app to interact with the database.
 */
class GameRepository(private val gameDao: GameDao, private val userDao: UserDao) {
    
    // --- Game Data Operations ---

    /**
     * Saves a completed game session to the database.
     */
    suspend fun insert(game: Game) {
        gameDao.insert(game)
    }

    /**
     * Observes the full history of games played.
     */
    fun getAllGames(): Flow<List<Game>> {
        return gameDao.getAll()
    }

    /**
     * Retrieves the details of a specific game by its ID.
     */
    suspend fun selectGame(id: Int): Game {
        return gameDao.selectById(id)
    }

    /**
     * Gets the best score achieved by the user across all sessions.
     */
    suspend fun getBestScore(): Int {
        return gameDao.getBestScore()
    }

    /**
     * Gets the total number of games played.
     */
    suspend fun getGamesPlayed(): Int {
        return gameDao.getGamesPlayed()
    }

    // --- User Profile Operations ---

    /**
     * Observes the local user profile (name and tag).
     */
    fun getUser(): Flow<User?> {
        return userDao.getUser()
    }

    /**
     * Updates the user's profile information in the database.
     */
    suspend fun saveUser(user: User) {
        userDao.insertOrUpdate(user)
    }
}
