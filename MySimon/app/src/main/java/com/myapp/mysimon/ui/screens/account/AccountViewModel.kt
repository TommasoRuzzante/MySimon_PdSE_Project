package com.myapp.mysimon.ui.screens.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myapp.mysimon.data.AppDatabase
import com.myapp.mysimon.data.GameRepository
import com.myapp.mysimon.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for the Account screen.
 * Retrieves aggregate statistics (best score and total games) and user profile information.
 */
class AccountViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: GameRepository

    // Observable state for the user's personal best
    private val _bestScore = MutableStateFlow(0)
    val bestScore: StateFlow<Int> = _bestScore.asStateFlow()

    // Observable state for the total number of games completed
    private val _gamesPlayed = MutableStateFlow(0)
    val gamesPlayed: StateFlow<Int> = _gamesPlayed.asStateFlow()

    // Observable user profile state
    val user: StateFlow<User?>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao(), db.userDao())
        
        user = repository.getUser().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _bestScore.value = repository.getBestScore()
            _gamesPlayed.value = repository.getGamesPlayed()
        }
    }

    /**
     * Updates the user's name and ensures a tag exists.
     */
    fun updateName(newName: String) {
        viewModelScope.launch {
            val currentUser = user.value ?: User(tag = generateUniqueTag())
            repository.saveUser(currentUser.copy(name = newName))
        }
    }

    /**
     * Generates a unique 8-character alphanumeric tag if the user doesn't have one.
     */
    private fun generateUniqueTag(): String {
        return UUID.randomUUID().toString().substring(0, 8).uppercase()
    }
}
