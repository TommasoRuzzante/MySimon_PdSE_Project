package com.myapp.mysimon.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "counter")
    val counter: Int,

    @ColumnInfo(name = "sequence")
    val sequence: String,

    @ColumnInfo(name = "error_index")
    val error: Int
)
