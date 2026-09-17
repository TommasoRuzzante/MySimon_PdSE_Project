package com.myapp.mysimon

import com.myapp.mysimon.model.SimonGame
import org.junit.Assert.assertEquals
import org.junit.Test

class SimonGameTest {

    @Test
    fun increment_addsButtonsAndUpdatesSequenceString() {
        val game = SimonGame()

        game.increment(0)
        game.increment(2)

        assertEquals(listOf(0, 2), game.sequence)
        assertEquals(2, game.count)
        assertEquals("R, G", game.getSequenceString())
    }

    @Test
    fun reset_clearsSequenceAndCounter() {
        val game = SimonGame()
        game.increment(1)
        game.increment(3)

        game.reset()

        assertEquals(emptyList<Int>(), game.sequence)
        assertEquals(0, game.count)
        assertEquals("", game.getSequenceString())
    }
}
