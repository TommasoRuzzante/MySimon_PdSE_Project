package com.myapp.mysimon.ui.screens.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myapp.mysimon.data.AppDatabase
import com.myapp.mysimon.data.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Account screen.
 * Retrieves aggregate statistics (best score and total games) from the repository.
 */
class AccountViewModel(application: Application) : AndroidViewModel(application) {
    // Access to the database through the repository
    private val repository: GameRepository

    // Observable state for the user's personal best
    private val _bestScore = MutableStateFlow(0)
    val bestScore: StateFlow<Int> = _bestScore.asStateFlow()

    // Observable state for the total number of games completed
    private val _gamesPlayed = MutableStateFlow(0)
    val gamesPlayed: StateFlow<Int> = _gamesPlayed.asStateFlow()

    init {
        // Initialize the database and repository
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao())
        
        // Refresh statistics when the ViewModel is created
        loadStats()
    }

    /**
     * Queries the repository for the latest user statistics.
     */
    private fun loadStats() {
        viewModelScope.launch {
            _bestScore.value = repository.getBestScore()
            _gamesPlayed.value = repository.getGamesPlayed()
        }
    }
}
