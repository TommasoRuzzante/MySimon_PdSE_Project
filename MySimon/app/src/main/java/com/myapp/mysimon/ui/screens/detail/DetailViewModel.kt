package com.myapp.mysimon.ui.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.myapp.mysimon.data.AppDatabase
import com.myapp.mysimon.data.Game
import com.myapp.mysimon.data.GameRepository
import com.myapp.mysimon.ui.navigation.DetailRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Detail screen.
 * Fetches specific game data from the repository based on the ID passed through navigation.
 */
class DetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    // Access to the database through the repository
    private val repository: GameRepository
    
    // Extract navigation arguments safely using Type-safe Navigation
    private val detailRoute = savedStateHandle.toRoute<DetailRoute>()

    // Observable state for the single game being viewed
    private val _game = MutableStateFlow<Game?>(null)
    val game: StateFlow<Game?> = _game.asStateFlow()

    init {
        // Initialize the database and repository
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao())
        
        // Load game details immediately upon initialization
        loadGame(detailRoute.id)
    }

    /**
     * Fetches a single game by its ID from the repository.
     */
    private fun loadGame(id: Int) {
        viewModelScope.launch {
            _game.value = repository.selectGame(id)
        }
    }
}
