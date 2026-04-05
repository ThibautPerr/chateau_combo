package com.chateaucombo.deck.carte.effet.effetplacement

import com.fasterxml.jackson.annotation.JsonTypeName
import com.chateaucombo.deck.carte.Blason
import com.chateaucombo.deck.carte.effet.Effet
import com.chateaucombo.deck.carte.effet.EffetContext
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteCleParBlasonAbsent")
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
