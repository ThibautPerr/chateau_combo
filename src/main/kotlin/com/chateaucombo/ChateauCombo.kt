package com.chateaucombo

import com.chateaucombo.player.Player
import io.github.oshai.kotlinlogging.KotlinLogging

class ChateauCombo {
    private val logger = KotlinLogging.logger {  }
    fun play(players: List<Player>) {
        players.forEach { logger.info { "Player ${it.id} : ${it.gold} golds, ${it.key} keys" } }
    }
}