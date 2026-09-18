package com.myapp.mysimon.ui.screens.game

/**
 * Enumeration representing the possible states of the Simon game.
 * Used to drive the UI logic and control user interactions.
 */
enum class GameState {
    STARTING,  // The game is ready to begin; waiting for user to press Start.
    CPU_TURN,  // The application is generating and displaying a sequence of colors.
    PAUSE,     // The game progress is temporarily suspended.
    USER_TURN, // The application is waiting for the user to replicate the sequence.
    WAITING,   // A transitional state used during internal checks or delays.
    GAME_OVER  // The game has ended, either through a user mistake or manual termination.
}
