package com.chateaucombo.effet.model

import com.chateaucombo.deck.model.Blason
import io.github.oshai.kotlinlogging.KotlinLogging

class AjouteCleParBlasonAbsent : Effet {
    private val logger = KotlinLogging.logger { }

    override fun apply(context: EffetContext) {
        val blasonsPresents = context.joueurActuel.tableau.cartesPositionees
            .flatMap { it.carte.blasons }
            .toSet()
        val cle = Blason.entries.count { it !in blasonsPresents }
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }
}
