package com.chateaucombo.effet.model

import io.github.oshai.kotlinlogging.KotlinLogging

class AjouteCleParCarteAvecUnSeulBlason : Effet {
    private val logger = KotlinLogging.logger {}

    override fun apply(context: EffetContext) {
        val cle = context.joueurActuel.tableau.cartesPositionees.count { it.carte.blasons.size == 1 }
        logger.info { "Ajout de $cle clés au joueur ${context.joueurActuel.id}" }
        context.joueurActuel.cle += cle
    }

}
