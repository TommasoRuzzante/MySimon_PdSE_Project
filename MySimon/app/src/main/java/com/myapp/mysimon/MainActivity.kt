package com.myapp.mysimon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.myapp.mysimon.audio.GameAudioManager
import com.myapp.mysimon.ui.navigation.*
import com.myapp.mysimon.ui.components.FabNewGame
import com.myapp.mysimon.ui.theme.*

/**
 * The main activity of the application, serving as the entry point.
 * It sets up the UI using Jetpack Compose, initializes global components like the audio manager,
 * and manages the top-level navigation structure including the Floating Action Button visibility.
 */
class MainActivity : ComponentActivity() {
    // Instance of the audio manager to provide sound effects across the app
    private lateinit var gameAudioManager: GameAudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enables edge-to-edge display for a modern UI look
        enableEdgeToEdge()

        // Initialize the audio manager with the activity context
        gameAudioManager = GameAudioManager(this)

        // Set the UI content using the application's theme
        setContent {
            MySimonTheme {
                // Initialize the navigation controller for managing screen transitions
                val navController = rememberNavController()
                
                // Observe the current navigation destination to dynamically update the UI
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        // The FAB for starting a new game is only visible on Home and Detail screens
                        val isVisible = currentDestination?.hasRoute<HomeRoute>() == true ||
                                currentDestination?.hasRoute<DetailRoute>() == true
                        if (isVisible) {
                            FabNewGame(onButtonClick = { navController.navigate(GameRoute) })
                        }
                    }
                ) { innerPadding ->
                    // Main navigation host that defines the screens and their transitions
                    MySimonNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding),
                        playButtonSound = { btn ->
                            gameAudioManager.playSound(btn)
                        },
                        playEndGameSound = {
                            gameAudioManager.playSound(99)
                        }
                    )
                }
            }
        }
    }

    /**
     * Clean up resources when the activity is destroyed to prevent memory leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        if (this::gameAudioManager.isInitialized) {
            gameAudioManager.release()
        }
    }
}
