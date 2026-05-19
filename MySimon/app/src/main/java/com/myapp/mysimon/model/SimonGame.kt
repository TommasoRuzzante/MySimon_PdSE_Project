package com.myapp.mysimon.model

class SimonGame {
    // List of the colors of the buttons
    private val colorsLetters = listOf("R", "M", "G", "Y", "B", "C")

    // The list contain Int with value between 0 and 5, each number representing a color
    val sequence: MutableList<Int> = mutableListOf()
    // The length of the sequence
    var count: Int = 0

    // Add a color to the sequence
    fun increment(btn: Int) {
        sequence.add(btn)
        count++
    }

    // Return the sequence as a string
    fun getSequenceString() : String {
        var s = ""
        if (sequence.isEmpty()) {
            return s
        }
        else {
            s = colorsLetters[sequence[0]]
            for (i in 1 until count) {
                s += ", " + colorsLetters[sequence[i]]
            }
            return s
        }
    }

    // Reset to empty/0 the values of this game
    fun reset() {
        sequence.clear()
        count = 0
    }
}