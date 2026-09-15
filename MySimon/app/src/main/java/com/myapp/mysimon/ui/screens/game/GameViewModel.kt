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

// Enumeration of the state of the game
enum class GameState {
    STARTING, // Waiting for the user to start a new game
    CPU_TURN, // The CPU is generating the sequence
    PAUSE, // The game is paused by the user
    USER_TURN, // The user can click on the buttons
    WAITING, // Waiting for the game to change state during the check
    GAME_OVER // The game ended due to an error or by the button End Game
}

// View model of the game
class GameViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    // Instance of the repository
    private val repository: GameRepository

    // Instance of the current SimonGame
    private val simonGame = SimonGame()

    // Number of buttons pressed by the user yet (synchronized with the savedStateHandle)
    // Used as an index of the sequence
    private var userIndex = 0
        set(value) {
            field = value
            savedStateHandle["user_index"] = value
        }

    // Observable state of the game
    private val _gameState = MutableStateFlow(GameState.STARTING)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Sequence of the game initialized as a state
    private val _sequenceString = MutableStateFlow("")
    val sequenceString: StateFlow<String> = _sequenceString.asStateFlow()

    // Tracks which button should be highlighted (-1 means none)
    private val _activeButtonIndex = MutableStateFlow(-1)
    val activeButtonIndex: StateFlow<Int> = _activeButtonIndex.asStateFlow()

    init {
        // Initialize the repository that the view model will use
        val db = AppDatabase.getDatabase(application)
        repository = GameRepository(db.gameDao())

        // Retrieves the game sequence and rebuilds the simonGame object
        val savedSequence = savedStateHandle.get<ArrayList<Int>>("sequence")
        savedSequence?.forEach { color ->
            simonGame.sequence.add(color)
            simonGame.count++
        }

        // Retrieves the index of the last button pressed by the user
        userIndex = savedStateHandle.get<Int>("user_index") ?: 0

        // Retrieves the current state of the game as a string
        val savedStateName = savedStateHandle.get<String>("game_state")
        if (savedStateName != null) {
            // Get the state of the game from the string
            val restoredState = GameState.valueOf(savedStateName)

            // Restore the state of the game
            _gameState.value = restoredState
            // Reconstruct display string if the user was mid-turn
            _sequenceString.value = simonGame.getSequenceString(userIndex)

            // Handle non static situation (the cpu was showing the sequence or the user was playing)
            if (restoredState == GameState.CPU_TURN || restoredState == GameState.USER_TURN) {
                // Restart by re-showing the current sequence to the user to remind them of the flow
                replayCurrentSequence()
            }
        }
    }

    // Utility function to update the game state
    private fun updateGameState(newState: GameState) {
        _gameState.value = newState
        savedStateHandle["game_state"] = newState.name
    }

    // Utility function used to show one more time the current sequence to the user
    private fun replayCurrentSequence() {
        viewModelScope.launch {
            // Set the state to CPU_TURN and hide the sequence during playback
            updateGameState(GameState.CPU_TURN)
            _sequenceString.value = ""

            for (colorIndex in simonGame.sequence) {
                // Stop playback if game is paused
                while (_gameState.value == GameState.PAUSE) {
                    // Wait 100 ms and then check if the game is resumed, if not repeat the loop
                    delay(100)
                }

                // Every button is illuminated for 600ms and there's a gap of 200ms between buttons
                _activeButtonIndex.value = colorIndex
                delay(600)
                _activeButtonIndex.value = -1
                delay(200)
            }

            // Pass the game state to the user
            updateGameState(GameState.USER_TURN)
            // Restore the progress string (what the user already matched)
            _sequenceString.value = simonGame.getSequenceString(userIndex)
        }
    }

    // Function to start a new game
    fun startNewGame() {
        simonGame.reset() // Make sure the game is cleared
        savedStateHandle["sequence"] = ArrayList<Int>()
        userIndex = 0
        updateGameState(GameState.CPU_TURN)
        addNewColor()
    }

    // Function that generate a new color and add it to the sequence
    fun addNewColor() {
        viewModelScope.launch {
            // Set the state to CPU_TURN
            updateGameState(GameState.CPU_TURN)

            // Clear the string every time the user has to repeat the sequence
            _sequenceString.value = ""

            // Generate an int between 0 and 5 and add it to the sequence
            val nextColor = Random.nextInt(0, 6)
            simonGame.increment(nextColor)
            savedStateHandle["sequence"] = ArrayList(simonGame.sequence)

            // Show the sequence to the user
            for (colorIndex in simonGame.sequence) {
                // Stop playback if game is paused
                while (_gameState.value == GameState.PAUSE) {
                    // Wait 100 ms and then check if the game is resumed, if not repeat the loop
                    delay(100)
                }

                // Every button is illuminated for 600ms and there's a gap of 200ms between buttons
                _activeButtonIndex.value = colorIndex
                delay(600)
                _activeButtonIndex.value = -1
                delay(200)
            }

            // Pass the game state to the user
            updateGameState(GameState.USER_TURN)
            // Ensure the display shows current progress (empty at start of turn)
            _sequenceString.value = simonGame.getSequenceString(userIndex)
        }
    }

    // Function that handle the user click, the parameter is the index of the button pressed
    fun userClick(btn: Int) {
        // Check if the user can actually click a button
        if (_gameState.value != GameState.USER_TURN || userIndex >= simonGame.count) return

        // Change the game state during the check
        updateGameState(GameState.WAITING)

        viewModelScope.launch {
            // Visual feedback for user click
            _activeButtonIndex.value = btn
            delay(100)
            _activeButtonIndex.value = -1

            // Button the user should have clicked
            // The user has pressed a button so the index is incremented
            val rightColor = simonGame.sequence[userIndex++]

            if (rightColor == btn) {
                // Update the sequence showed with the last button pressed
                _sequenceString.value = simonGame.getSequenceString(userIndex)

                // Check if the user has completed this round's sequence
                if (userIndex == simonGame.count) {
                    userIndex = 0
                    delay(500)
                    addNewColor()
                } else {
                    // If there are one or more other button the user can click again
                    updateGameState(GameState.USER_TURN)
                }
            } else {
                // The user has guess the wrong button, so the game end
                gameOver()
            }
        }
    }

    // Function that handle the end of the game
    fun gameOver() {
        updateGameState(GameState.GAME_OVER)

        // Values of the game that has to be saved
        val game = Game(
            counter = simonGame.count - 1,
            sequence = simonGame.getSequenceString(),
            error = userIndex
        )

        // Launch a coroutine to insert the game in the database safely
        viewModelScope.launch {
            repository.insert(game)
        }
    }

    // Function called when the pause button is clicked by the user
    fun pauseGame() {
        updateGameState(GameState.PAUSE)
    }

    // Function used to return to the cpu turn
    fun resumeGame() {
        updateGameState(GameState.CPU_TURN)
    }

    // Function called when the user end the game with the endgame button or with the system back button
    fun endGame() {
        // If the user hasn't pressed any button do not save the game
        if (simonGame.count <= 1 && userIndex == 0) return

        // Increment the number of buttons pressed by the user, because the error is the button never pressed
        userIndex++
        gameOver()
    }
}
