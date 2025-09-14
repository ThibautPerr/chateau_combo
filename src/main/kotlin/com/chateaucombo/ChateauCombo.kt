package com.chateaucombo

import com.chateaucombo.joueur.model.Joueur
import io.github.oshai.kotlinlogging.KotlinLogging

class ChateauCombo {
    private val logger = KotlinLogging.logger {  }

    fun play(joueurs: List<Joueur>) {
        joueurs.forEach { logger.info { "Joueur ${it.id} : ${it.or} or, ${it.cle} clés" } }
    }
}