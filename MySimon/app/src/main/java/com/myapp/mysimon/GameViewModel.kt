package com.myapp.mysimon

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.myapp.mysimon.data.AppDatabase
import com.myapp.mysimon.data.Game
import com.myapp.mysimon.data.GameRepository
import com.myapp.mysimon.model.SimonGame
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameState {
    STARTING,
    CPU_TURN,
    USER_TURN,
    GAME_OVER,
    PAUSE
}

class GameViewModel(application: Application) : AndroidViewModel(application) {
    // Instance of the repository
    private val repository: GameRepository
    init {
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao())
    }

    // Instance of the current SimonGame
    private val simonGame = SimonGame()

    // Last button pressed by the user
    private var userIndex = 0

    // Initializing the observable state of the game
    private val _gameState = MutableStateFlow(GameState.STARTING)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Initializing the observable state of the sequence
    private val _sequenceString = MutableStateFlow("")
    val sequenceString: StateFlow<String> = _sequenceString.asStateFlow()

    // Function to start a new game
    fun startNewGame() {
        userIndex = 0
        _gameState.value = GameState.CPU_TURN
        addNewColor()
    }

    // Function that generate a new color and add it to the sequence
    fun addNewColor() {
        _gameState.value = GameState.CPU_TURN

        // Generate an int between 0 and 5 and add it to the sequence
        val nextColor = Random.nextInt(0, 6)
        simonGame.increment(nextColor)
        _sequenceString.value = simonGame.getSequenceString()

        // Pass the game state to the user
        _gameState.value = GameState.USER_TURN
    }

    // Function that handle the user click, the parameter is the index of the button pressed
    fun userClick(btn: Int) {
        // Check if it's the user turn
        if (_gameState.value != GameState.USER_TURN) return

        // Check if the user press the right button
        val rightColor = simonGame.sequence[userIndex]
        if (rightColor == btn) {
            // The user has guess the right color so we increment his counter
            userIndex++

            // Check if the user has completed this round's sequence
            if (userIndex == simonGame.count) {
                userIndex = 0
                addNewColor()
            }
        } else {
            // The user has guess the wrong button, so the game end
            gameOver()
        }
    }

    // Function that handle the end of the game
    fun gameOver() {
        _gameState.value = GameState.GAME_OVER

        // Save the values that has to be inserted in the database
        val game = Game(counter = simonGame.count, sequence = simonGame.getSequenceString(), error = userIndex)

        // Launch a coroutine to insert the game in the database safely
        viewModelScope.launch {
            repository.insert(game)
        }
    }

    // Function called when the pause button is clicked by the user
    fun pauseGame() {
        // The function can be called only during the cpu turn
        if (_gameState.value != GameState.CPU_TURN) return
        _gameState.value = GameState.PAUSE
    }

    // Function used to return to the cpu turn
    fun resumeGame() {
        _gameState.value = GameState.CPU_TURN
    }

    // Function called when the endgame button is clicked by the user
    fun endGame() {
        gameOver()
    }
}