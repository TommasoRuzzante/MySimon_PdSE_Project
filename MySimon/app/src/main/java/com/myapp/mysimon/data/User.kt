package com.myapp.mysimon.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing the local user profile in the Room database.
 * Stores the user's name and a unique alphanumeric tag for future identification.
 * Since this application currently supports a single local user, the ID is fixed to 1.
 */
@Entity
data class User(
    @PrimaryKey val id: Int = 1, // Constant ID for the single-user local profile
    val name: String = "",       // User-defined name
    val tag: String = ""         // Unique alphanumeric identifier (e.g., generated via UUID)
)
