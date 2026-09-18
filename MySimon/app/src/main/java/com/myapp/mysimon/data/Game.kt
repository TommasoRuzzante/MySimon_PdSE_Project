package com.myapp.mysimon.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a single game session in the Room database.
 * Stores the final score, the sequence generated, and where the user made a mistake.
 */
@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // The final score, representing the number of correct steps in the sequence
    @ColumnInfo(name = "counter")
    val counter: Int,

    // A comma-separated string representation of the color sequence
    @ColumnInfo(name = "sequence")
    val sequence: String,

    // The index within the sequence where the user clicked the wrong color
    @ColumnInfo(name = "error_index")
    val error: Int
)
