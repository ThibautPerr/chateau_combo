package com.chateaucombo

import com.chateaucombo.player.Player
import org.junit.jupiter.api.Test

class ChateauComboTest {
    private val app = ChateauCombo()

    @Test
    fun `should play one game`() {
        play()
    }

    private fun play(players: List<Player> = given4Players()) {
        app.play(players)
    }

    @Test
    fun `should play a game with 4 players`() {
        val players = given4Players()

        play(players)
    }

    private fun given4Players() = List(4) { Player(id = it) }
}