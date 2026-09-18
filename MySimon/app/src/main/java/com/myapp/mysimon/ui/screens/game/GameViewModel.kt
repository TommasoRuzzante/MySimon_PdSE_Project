package com.myapp.mysimon.ui.screens.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.myapp.mysimon.data.AppDatabase
import com.myapp.mysimon.data.Game
import com.myapp.mysimon.data.GameRepository
import com.myapp.mysimon.model.SimonGame
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * ViewModel for the Game screen.
 * Manages the Simon game logic, including sequence generation, user input verification,
 * and state persistence across process death using SavedStateHandle.
 */
class GameViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    // Access to the database through the repository
    private val repository: GameRepository

    // Core game logic instance
    private val simonGame = SimonGame()

    // Current position in the sequence the user is expected to match
    private var userIndex = 0
        set(value) {
            field = value
            savedStateHandle["user_index"] = value
        }

    // Observable state of the current game phase (e.g., STARTING, CPU_TURN, USER_TURN)
    private val _gameState = MutableStateFlow(GameState.STARTING)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Human-readable string representation of the current sequence
    private val _sequenceString = MutableStateFlow("")
    val sequenceString: StateFlow<String> = _sequenceString.asStateFlow()

    // Index of the button to be visually highlighted during playback (-1 for none)
    private val _activeButtonIndex = MutableStateFlow(-1)
    val activeButtonIndex: StateFlow<Int> = _activeButtonIndex.asStateFlow()

    init {
        // Initialize the database and repository
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao(), db.userDao())

        // Restore game state from SavedStateHandle after process death
        val savedSequence = savedStateHandle.get<ArrayList<Int>>("sequence")
        savedSequence?.forEach { color ->
            simonGame.sequence.add(color)
            simonGame.count++
        }

        userIndex = savedStateHandle.get<Int>("user_index") ?: 0

        val savedStateName = savedStateHandle.get<String>("game_state")
        if (savedStateName != null) {
            val restoredState = GameState.valueOf(savedStateName)
            _gameState.value = restoredState
            _sequenceString.value = simonGame.getSequenceString(userIndex)

            // If the game was active, replay the sequence to remind the user
            if (restoredState == GameState.CPU_TURN || restoredState == GameState.USER_TURN) {
                replayCurrentSequence()
            }
        }
    }

    /**
     * Updates the game state and persists it to SavedStateHandle.
     */
    private fun updateGameState(newState: GameState) {
        _gameState.value = newState
        savedStateHandle["game_state"] = newState.name
    }

    /**
     * Replays the entire current sequence visually for the user.
     */
    private fun replayCurrentSequence() {
        viewModelScope.launch {
            updateGameState(GameState.CPU_TURN)
            _sequenceString.value = ""

            for (colorIndex in simonGame.sequence) {
                // Wait if the user pauses the game during playback
                while (_gameState.value == GameState.PAUSE) {
                    delay(100)
                }

                _activeButtonIndex.value = colorIndex
                delay(500)
                _activeButtonIndex.value = -1
                delay(200)
            }

            updateGameState(GameState.USER_TURN)
            _sequenceString.value = simonGame.getSequenceString(userIndex)
        }
    }

    /**
     * Starts a fresh game, clearing previous progress.
     */
    fun startNewGame() {
        simonGame.reset()
        savedStateHandle["sequence"] = ArrayList<Int>()
        userIndex = 0
        updateGameState(GameState.CPU_TURN)
        addNewColor()
    }

    /**
     * Generates a new color, adds it to the sequence, and initiates playback.
     */
    fun addNewColor() {
        viewModelScope.launch {
            updateGameState(GameState.CPU_TURN)
            _sequenceString.value = ""

            val nextColor = Random.nextInt(0, 6)
            simonGame.increment(nextColor)
            savedStateHandle["sequence"] = ArrayList(simonGame.sequence)

            for (colorIndex in simonGame.sequence) {
                while (_gameState.value == GameState.PAUSE) {
                    delay(100)
                }

                _activeButtonIndex.value = colorIndex
                delay(500)
                _activeButtonIndex.value = -1
                delay(200)
            }

            updateGameState(GameState.USER_TURN)
            _sequenceString.value = simonGame.getSequenceString(userIndex)
        }
    }

    /**
     * Handles user interaction with a colored button.
     * Verifies if the click matches the sequence.
     */
    fun userClick(btn: Int) {
        if (_gameState.value != GameState.USER_TURN || userIndex >= simonGame.count) return

        updateGameState(GameState.WAITING)

        viewModelScope.launch {
            // Visual feedback for the click
            _activeButtonIndex.value = btn
            delay(100)
            _activeButtonIndex.value = -1

            val rightColor = simonGame.sequence[userIndex++]

            if (rightColor == btn) {
                _sequenceString.value = simonGame.getSequenceString(userIndex)

                if (userIndex == simonGame.count) {
                    // Sequence completed successfully, add a new step
                    userIndex = 0
                    delay(400)
                    addNewColor()
                } else {
                    updateGameState(GameState.USER_TURN)
                }
            } else {
                // Incorrect button clicked
                gameOver()
            }
        }
    }

    /**
     * Concludes the game and saves the result to the local database.
     */
    fun gameOver() {
        updateGameState(GameState.GAME_OVER)

        val game = Game(
            counter = simonGame.count - 1,
            sequence = simonGame.getSequenceString(),
            error = userIndex
        )

        viewModelScope.launch {
            repository.insert(game)
        }
    }

    /**
     * Suspends sequence playback.
     */
    fun pauseGame() {
        updateGameState(GameState.PAUSE)
    }

    /**
     * Resumes sequence playback.
     */
    fun resumeGame() {
        updateGameState(GameState.CPU_TURN)
    }

    /**
     * Manually ends the game (e.g., via Back button or End Game button).
     */
    fun endGame() {
        if (simonGame.count <= 1 && userIndex == 0) return

        userIndex++ // Mark the end position
        gameOver()
    }
}
