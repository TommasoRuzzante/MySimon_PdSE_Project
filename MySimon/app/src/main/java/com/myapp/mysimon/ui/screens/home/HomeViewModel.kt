package com.myapp.mysimon.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myapp.mysimon.data.AppDatabase
import com.myapp.mysimon.data.Game
import com.myapp.mysimon.data.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the Home screen.
 * Responsible for fetching and providing the list of previous games from the repository.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {
    // Access to the database through the repository
    private val repository: GameRepository

    // Observable state containing the list of games, ordered by most recent
    val games: StateFlow<List<Game>>

    init {
        // Initialize the database and repository
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao())

        // Map the flow from the repository to reverse the order and convert it to a StateFlow
        // SharingStarted.WhileSubscribed keeps the flow active only when the UI is visible
        games = repository.getAllGames()
            .map { it.reversed() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
}
