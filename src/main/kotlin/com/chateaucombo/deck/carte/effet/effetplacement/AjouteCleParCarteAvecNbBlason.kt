package com.chateaucombo.deck.carte.effet.effetplacement

import com.chateaucombo.deck.carte.effet.Effet
import com.chateaucombo.deck.carte.effet.EffetContext
import com.fasterxml.jackson.annotation.JsonTypeName
import io.github.oshai.kotlinlogging.KotlinLogging

@JsonTypeName("AjouteCleParCarteAvecNbBlason")
class AjouteCleParCarteAvecNbBlason(val nbBlason: Int) : Effet {
    private val logger = KotlinLogging.logger {}

    override fun apply(context: EffetContext) {
        val cle = context.joueurActuel.tableau.cartesPositionees.count { it.carte.blasons.size == nbBlason }
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }

}
